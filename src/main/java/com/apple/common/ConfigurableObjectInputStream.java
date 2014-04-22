package com.apple.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Proxy;

import com.apple.reflect.util.ReflectionHelper;

public class ConfigurableObjectInputStream extends ObjectInputStream {

	private final ClassLoader classLoader;

	public ConfigurableObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
		super(in);
		this.classLoader = classLoader;
	}

	protected Class<?> resolveClass(ObjectStreamClass classDesc) throws IOException, ClassNotFoundException {
		try {
			if (this.classLoader != null) {
				// Use the specified ClassLoader to resolve local classes.
				return ReflectionHelper.forName(classDesc.getName(), this.classLoader);
			}
			else {
				// Use the default ClassLoader...
				return super.resolveClass(classDesc);
			}
		}
		catch (ClassNotFoundException ex) {
			return resolveFallbackIfPossible(classDesc.getName(), ex);
		}
	}

	protected Class<?> resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
		if (this.classLoader != null) {
			// Use the specified ClassLoader to resolve local proxy classes.
			Class<?>[] resolvedInterfaces = new Class[interfaces.length];
			for (int i = 0; i < interfaces.length; i++) {
				try {
					resolvedInterfaces[i] = ReflectionHelper.forName(interfaces[i], this.classLoader);
				}
				catch (ClassNotFoundException ex) {
					resolvedInterfaces[i] = resolveFallbackIfPossible(interfaces[i], ex);
				}
			}
			try {
				return Proxy.getProxyClass(this.classLoader, resolvedInterfaces);
			}
			catch (IllegalArgumentException ex) {
				throw new ClassNotFoundException(null, ex);
			}
		}
		else {
			// Use ObjectInputStream's default ClassLoader...
			try {
				return super.resolveProxyClass(interfaces);
			}
			catch (ClassNotFoundException ex) {
				Class<?>[] resolvedInterfaces = new Class[interfaces.length];
				for (int i = 0; i < interfaces.length; i++) {
					resolvedInterfaces[i] = resolveFallbackIfPossible(interfaces[i], ex);
				}
				return Proxy.getProxyClass(getFallbackClassLoader(), resolvedInterfaces);
			}
		}
	}

	protected Class<?> resolveFallbackIfPossible(String className, ClassNotFoundException ex)
			throws IOException, ClassNotFoundException{
		throw ex;
	}

	protected ClassLoader getFallbackClassLoader() throws IOException {
		return null;
	}

}
