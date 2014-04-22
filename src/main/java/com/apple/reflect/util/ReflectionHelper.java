package com.apple.reflect.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class ReflectionHelper {

	public static final String ARRAY_SUFFIX = "[]";

	/** Prefix for internal array class names: "[L" */
	public static final String INTERNAL_ARRAY_PREFIX = "[L";

	/** The package separator character '.' */
	public static final char PACKAGE_SEPARATOR = '.';

	/** The inner class separator character '$' */
	public static final char INNER_CLASS_SEPARATOR = '$';

	/** The CGLIB class separator character "$$" */
	public static final String CGLIB_CLASS_SEPARATOR = "$$";

	/** The ".class" file suffix */
	public static final String CLASS_FILE_SUFFIX = ".class";
	
	private static final Map<Type, Class<?>> primitiveWrapperTypeMap = new HashMap<Type, Class<?>>(8);
	private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<String, Class<?>>(16);
	static {
		primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
		primitiveWrapperTypeMap.put(Byte.class, byte.class);
		primitiveWrapperTypeMap.put(Character.class, char.class);
		primitiveWrapperTypeMap.put(Double.class, double.class);
		primitiveWrapperTypeMap.put(Float.class, float.class);
		primitiveWrapperTypeMap.put(Integer.class, int.class);
		primitiveWrapperTypeMap.put(Long.class, long.class);
		primitiveWrapperTypeMap.put(Short.class, short.class);

		Set<Class<?>> primitiveTypeNames = new HashSet<Class<?>>(16);
		primitiveTypeNames.addAll(primitiveWrapperTypeMap.values());
		primitiveTypeNames.addAll(Arrays.asList(new Class<?>[] {
				boolean[].class, byte[].class, char[].class, double[].class,
				float[].class, int[].class, long[].class, short[].class}));
		for (Iterator<Class<?>> it = primitiveTypeNames.iterator(); it.hasNext();) {
			Class<?> primitiveClass = it.next();
			primitiveTypeNameMap.put(primitiveClass.getName(), primitiveClass);
		}
	}

	public static void makeAccessible(final Member m) {
		if (!Modifier.isPublic(m.getModifiers() & m.getDeclaringClass().getModifiers())) {
			makeAccessible((AccessibleObject) m);
		}
	}

	public static String objectToString(Object o) {
		StringBuffer sb = new StringBuffer();
		sb.append(o.getClass().getName()).append('@').append(Integer.toHexString(o.hashCode()));
		return sb.toString();
	}

	public static Method findMethodOnClass(Class<?> c, Method m) {
		try {
			return c.getMethod(m.getName(), m.getParameterTypes());
		} catch (NoSuchMethodException ex) {
			for (Method _m : c.getMethods()) {
				if (_m.getName().equals(m.getName()) && _m.getParameterTypes().length == m.getParameterTypes().length) {
					if (compareParameterTypes(m.getGenericParameterTypes(), _m.getGenericParameterTypes())) {
						return _m;
					}
				}
			}
		}
		return null;
	}

	public static String methodInstanceToString(Object o, Method m) {
		StringBuffer sb = new StringBuffer();
		sb.append(o.getClass().getName()).append('@').append(Integer.toHexString(o.hashCode())).append('.').append(m.getName()).append('(');

		Class<?>[] params = m.getParameterTypes();
		for (int i = 0; i < params.length; i++) {
			sb.append(getTypeName(params[i]));
			if (i < (params.length - 1))
				sb.append(",");
		}

		sb.append(')');

		return sb.toString();
	}

	private static String getTypeName(Class<?> type) {
		if (type.isArray()) {
			try {
				Class<?> cl = type;
				int dimensions = 0;
				while (cl.isArray()) {
					dimensions++;
					cl = cl.getComponentType();
				}
				StringBuffer sb = new StringBuffer();
				sb.append(cl.getName());
				for (int i = 0; i < dimensions; i++) {
					sb.append("[]");
				}
				return sb.toString();
			} catch (Throwable e) { /* FALLTHRU */
			}
		}
		return type.getName();
	}
	
	public static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
		Class<?> clazz = resolvePrimitiveClassName(name);
		if (clazz != null) {
			return clazz;
		}

		// "java.lang.String[]" style arrays
		if (name.endsWith(ARRAY_SUFFIX)) {
			String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
			Class<?> elementClass = forName(elementClassName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		// "[Ljava.lang.String;" style arrays
		int internalArrayMarker = name.indexOf(INTERNAL_ARRAY_PREFIX);
		if (internalArrayMarker != -1 && name.endsWith(";")) {
			String elementClassName = null;
			if (internalArrayMarker == 0) {
				elementClassName = name.substring(INTERNAL_ARRAY_PREFIX.length(), name.length() - 1);
			}
			else if (name.startsWith("[")) {
				elementClassName = name.substring(1);
			}
			Class<?> elementClass = forName(elementClassName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		ClassLoader classLoaderToUse = classLoader;
		if (classLoaderToUse == null) {
			classLoaderToUse = getContextClassLoader();
		}
		return classLoaderToUse.loadClass(name);
	}
	
	public static Class<?> resolvePrimitiveClassName(String name) {
		Class<?> result = null;
		// Most class names will be quite long, considering that they SHOULD sit in a package, so a length check is worthwhile.
		if (name != null && name.length() <= 8) {
			// Could be a primitive - likely.
			result = primitiveTypeNameMap.get(name);
		}
		return result;
	}

	public static Class<?> classForName(String name) {
		return classForName(name, getContextClassLoader());
	}

	public static Class<?> classForName(String name, ClassLoader cl) {
		if (cl != null) {
			try {
				return Class.forName(name, false, cl);
			} catch (ClassNotFoundException ex) {
			}
		}
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException ex) {
		}

		return null;
	}

	public static Class<?> classForNameWithException(String name) throws ClassNotFoundException {
		return classForNameWithException(name, getContextClassLoader());
	}

	public static Class<?> classForNameWithException(String name, ClassLoader cl) throws ClassNotFoundException {
		if (cl != null) {
			try {
				return Class.forName(name, false, cl);
			} catch (ClassNotFoundException ex) {
			}
		}
		return Class.forName(name);
	}

	public static ClassLoader getContextClassLoader() {
		return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
			public ClassLoader run() {
				ClassLoader cl = null;
				try {
					cl = Thread.currentThread().getContextClassLoader();
				} catch (SecurityException ex) {
				}
				return cl;
			}
		});
	}

	public static Class<?> getGenericClass(Type parameterizedType) throws IllegalArgumentException {
		final Type t = getTypeArgumentOfParameterizedType(parameterizedType);
		if (t == null)
			return null;

		final Class<?> c = getClassOfType(t);
		if (c == null) {
			throw new IllegalArgumentException(String.format("%s, %s not supported!", t, parameterizedType));
		}
		return c;
	}

	public static TypeClassPair getTypeArgumentAndClass(Type parameterizedType) throws IllegalArgumentException {
		final Type t = getTypeArgumentOfParameterizedType(parameterizedType);
		if (t == null)
			return null;

		final Class<?> c = getClassOfType(t);
		if (c == null) {
			throw new IllegalArgumentException(String.format("%s, %s!", t, parameterizedType));
		}

		return new TypeClassPair(t, c);
	}

	private static Type getTypeArgumentOfParameterizedType(Type parameterizedType) {
		if (!(parameterizedType instanceof ParameterizedType))
			return null;

		ParameterizedType type = (ParameterizedType) parameterizedType;
		Type[] genericTypes = type.getActualTypeArguments();
		if (genericTypes.length != 1)
			return null;

		return genericTypes[0];
	}

	public static Class<?> getClassOfType(Type type) {
		Class<?> clazz = primitiveWrapperTypeMap.get(type);
		if (clazz != null) {
			return clazz;
		}
		if (type instanceof Class) {
			return (Class<?>) type;
		} else if (type instanceof GenericArrayType) {
			GenericArrayType arrayType = (GenericArrayType) type;
			Type t = arrayType.getGenericComponentType();
			if (t instanceof Class) {
				Class<?> c = (Class<?>) t;
				try {
					// TODO is there a better way to get the Class object
					// representing an array
					Object o = Array.newInstance(c, 0);
					return o.getClass();
				} catch (Exception e) {
					throw new IllegalArgumentException(e);
				}
			}
		} else if (type instanceof ParameterizedType) {
			ParameterizedType subType = (ParameterizedType) type;
			Type t = subType.getRawType();
			if (t instanceof Class) {
				return (Class<?>) t;
			}
		}
		return null;
	}

	public static Method getValueOfStringMethod(Class<?> c) {
		try {
			Method m = c.getDeclaredMethod("valueOf", String.class);
			if (!Modifier.isStatic(m.getModifiers()) && m.getReturnType() == c) {
				return null;
			}
			return m;
		} catch (Exception e) {
			return null;
		}
	}

	public static Method getFromStringStringMethod(Class<?> c) {
		try {
			Method m = c.getDeclaredMethod("fromString", String.class);
			if (!Modifier.isStatic(m.getModifiers()) && m.getReturnType() == c) {
				return null;
			}
			return m;
		} catch (Exception e) {
			return null;
		}
	}

	public static Constructor<?> getStringConstructor(Class<?> c) {
		try {
			return c.getConstructor(String.class);
		} catch (Exception e) {
			return null;
		}
	}

	public static Class<?>[] getParameterizedClassArguments(DeclaringClassInterfacePair p) {
		if (p.genericInterface instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) p.genericInterface;
			Type[] as = pt.getActualTypeArguments();
			Class<?>[] cas = new Class[as.length];

			for (int i = 0; i < as.length; i++) {
				Type a = as[i];
				if (a instanceof Class) {
					cas[i] = (Class<?>) a;
				} else if (a instanceof ParameterizedType) {
					pt = (ParameterizedType) a;
					cas[i] = (Class<?>) pt.getRawType();
				} else if (a instanceof TypeVariable) {
					ClassTypePair ctp = resolveTypeVariable(p.concreteClass, p.declaringClass, (TypeVariable<?>) a);
					cas[i] = (ctp != null) ? ctp.c : Object.class;
				}
			}
			return cas;
		} else {
			return null;
		}
	}

	public static Type[] getParameterizedTypeArguments(DeclaringClassInterfacePair p) {
		if (p.genericInterface instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) p.genericInterface;
			Type[] as = pt.getActualTypeArguments();
			Type[] ras = new Type[as.length];

			for (int i = 0; i < as.length; i++) {
				Type a = as[i];
				if (a instanceof Class) {
					ras[i] = a;
				} else if (a instanceof ParameterizedType) {
					pt = (ParameterizedType) a;
					ras[i] = a;
				} else if (a instanceof TypeVariable) {
					ClassTypePair ctp = resolveTypeVariable(p.concreteClass, p.declaringClass, (TypeVariable<?>) a);
					ras[i] = ctp.t;
				}
			}
			return ras;
		} else {
			return null;
		}
	}

	public static DeclaringClassInterfacePair getClass(Class<?> concrete, Class<?> iface) {
		return getClass(concrete, iface, concrete);
	}

	private static DeclaringClassInterfacePair getClass(Class<?> concrete, Class<?> iface, Class<?> c) {
		Type[] gis = c.getGenericInterfaces();
		DeclaringClassInterfacePair p = getType(concrete, iface, c, gis);
		if (p != null)
			return p;

		c = c.getSuperclass();
		if (c == null || c == Object.class)
			return null;

		return getClass(concrete, iface, c);
	}

	private static DeclaringClassInterfacePair getType(Class<?> concrete, Class<?> iface, Class<?> c, Type[] ts) {
		for (Type t : ts) {
			DeclaringClassInterfacePair p = getType(concrete, iface, c, t);
			if (p != null)
				return p;
		}
		return null;
	}

	private static DeclaringClassInterfacePair getType(Class<?> concrete, Class<?> iface, Class<?> c, Type t) {
		if (t instanceof Class) {
			if (t == iface) {
				return new DeclaringClassInterfacePair(concrete, c, t);
			} else {
				return getClass(concrete, iface, (Class<?>) t);
			}
		} else if (t instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) t;
			if (pt.getRawType() == iface) {
				return new DeclaringClassInterfacePair(concrete, c, t);
			} else {
				return getClass(concrete, iface, (Class<?>) pt.getRawType());
			}
		}
		return null;
	}

	public static ClassTypePair resolveTypeVariable(Class<?> c, Class<?> dc, TypeVariable<?> tv) {
		return resolveTypeVariable(c, dc, tv, new HashMap<TypeVariable<?>, Type>());
	}

	private static ClassTypePair resolveTypeVariable(Class<?> c, Class<?> dc, TypeVariable<?> tv, Map<TypeVariable<?>, Type> map) {
		Type[] gis = c.getGenericInterfaces();
		for (Type gi : gis) {
			if (gi instanceof ParameterizedType) {
				// process pt of interface
				ParameterizedType pt = (ParameterizedType) gi;
				ClassTypePair ctp = resolveTypeVariable(pt, (Class<?>) pt.getRawType(), dc, tv, map);
				if (ctp != null)
					return ctp;
			}
		}

		Type gsc = c.getGenericSuperclass();
		if (gsc instanceof ParameterizedType) {
			// process pt of class
			ParameterizedType pt = (ParameterizedType) gsc;
			return resolveTypeVariable(pt, c.getSuperclass(), dc, tv, map);
		} else if (gsc instanceof Class) {
			return resolveTypeVariable(c.getSuperclass(), dc, tv, map);
		}
		return null;
	}

	private static ClassTypePair resolveTypeVariable(ParameterizedType pt, Class<?> c, Class<?> dc, TypeVariable<?> tv, Map<TypeVariable<?>, Type> map) {
		Type[] typeArguments = pt.getActualTypeArguments();

		TypeVariable<?>[] typeParameters = c.getTypeParameters();

		Map<TypeVariable<?>, Type> submap = new HashMap<TypeVariable<?>, Type>();
		for (int i = 0; i < typeArguments.length; i++) {
			// Substitute a type variable with the Java class
			if (typeArguments[i] instanceof TypeVariable) {
				Type t = map.get(typeArguments[i]);
				submap.put(typeParameters[i], t);
			} else {
				submap.put(typeParameters[i], typeArguments[i]);
			}
		}

		if (c == dc) {
			Type t = submap.get(tv);
			if (t instanceof Class) {
				return new ClassTypePair((Class<?>) t);
			} else if (t instanceof GenericArrayType) {
				t = ((GenericArrayType) t).getGenericComponentType();
				if (t instanceof Class) {
					c = (Class<?>) t;
					try {
						// TODO is there a better way to get the Class object
						// representing an array
						Object o = Array.newInstance(c, 0);
						return new ClassTypePair(o.getClass());
					} catch (Exception e) {
					}
					return null;
				} else {
					return null;
				}
			} else if (t instanceof ParameterizedType) {
				pt = (ParameterizedType) t;
				if (pt.getRawType() instanceof Class) {
					return new ClassTypePair((Class<?>) pt.getRawType(), pt);
				} else
					return null;
			} else {
				return null;
			}
		} else {
			return resolveTypeVariable(c, dc, tv, submap);
		}
	}

	private static boolean compareParameterTypes(Type[] ts, Type[] _ts) {
		for (int i = 0; i < ts.length; i++) {
			if (!ts[i].equals(_ts[i])) {
				if (!(_ts[i] instanceof TypeVariable)) {
					return false;
				}
			}
		}
		return true;
	}

	private static void makeAccessible(final AccessibleObject o) {
		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() {
				if (!o.isAccessible()) {
					o.setAccessible(true);
				}
				return o;
			}
		});
	}

	public static class ClassTypePair {

		public final Class<?> c;
		public final Type t;

		public ClassTypePair(Class<?> c) {
			this(c, c);
		}

		public ClassTypePair(Class<?> c, Type t) {
			this.c = c;
			this.t = t;
		}
	}

	public static final class TypeClassPair {
		public final Type t;
		public final Class<?> c;

		public TypeClassPair(Type t, Class<?> c) {
			this.t = t;
			this.c = c;
		}
	}

	public static class DeclaringClassInterfacePair {
		public final Class<?> concreteClass;

		public final Class<?> declaringClass;

		public final Type genericInterface;

		private DeclaringClassInterfacePair(Class<?> concreteClass, Class<?> declaringClass, Type genericInteface) {
			this.concreteClass = concreteClass;
			this.declaringClass = declaringClass;
			this.genericInterface = genericInteface;
		}
	}

}
