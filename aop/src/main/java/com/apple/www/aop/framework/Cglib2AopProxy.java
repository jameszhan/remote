package com.apple.www.aop.framework;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;

import com.mulberry.toolkit.reflect.Reflections;
import net.sf.cglib.core.CodeGenerationException;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.transform.impl.UndeclaredThrowableStrategy;

import org.aopalliance.aop.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cglib2AopProxy implements AopProxy {
	/** Logger available to subclasses; static to optimize serialization */
	protected static final Logger logger = LoggerFactory.getLogger(Cglib2AopProxy.class);	

	// Constants for CGLIB callback array indices
	private static final int AOP_PROXY = 0;
	private static final int INVOKE_TARGET = 1;
	private static final int NO_OVERRIDE = 2;
	private static final int INVOKE_EQUALS = 3;
	private static final int INVOKE_HASHCODE = 4;


	private final Object target;
	private final List<Advice> adviceList;

	public Cglib2AopProxy(Object target, List<Advice> adviceList) {
		super();
		this.target = target;
		this.adviceList = adviceList;
	}

	public Object getProxy() {
		return getProxy(null);
	}

	public Object getProxy(ClassLoader classLoader) {
		if (logger.isDebugEnabled()) {
			logger.debug("Creating CGLIB2 proxy: target source is " + this.target.getClass());
		}

		try {
			Class<?> rootClass = this.target.getClass();
			if (rootClass == null) {
				throw new IllegalStateException("Target class must be available for creating a CGLIB proxy");
			}

			Class<?> proxySuperClass = rootClass;

			// Validate the class, writing log messages as necessary.
			doValidateClass(proxySuperClass);

			// Configure CGLIB Enhancer...
			Enhancer enhancer = createEnhancer();
			if (classLoader != null) {
				enhancer.setClassLoader(classLoader);
			}
			enhancer.setSuperclass(proxySuperClass);
			enhancer.setStrategy(new UndeclaredThrowableStrategy(UndeclaredThrowableException.class));
			enhancer.setInterfaces(this.target.getClass().getInterfaces());
			enhancer.setInterceptDuringConstruction(false);

			/*Callback[] callbacks = getCallbacks(rootClass);
			enhancer.setCallbacks(callbacks);*/
			enhancer.setCallbackFilter(new ProxyCallbackFilter(this.target, adviceList));

			/*			
			Class<?>[] types = new Class[callbacks.length];
			for (int x = 0; x < types.length; x++) {
				types[x] = callbacks[x].getClass();
			}
			enhancer.setCallbackTypes(types);
 			*/
			// Generate the proxy class and create a proxy instance.
			Object proxy = enhancer.create();
			

			return proxy;
		}
		catch (CodeGenerationException ex) {
			throw new RuntimeException("Could not generate CGLIB subclass of class [" +
					this.target.getClass() + "]: " +
					"Common causes of this problem include using a final class or a non-visible class",
					ex);
		}
		catch (IllegalArgumentException ex) {
			throw new RuntimeException("Could not generate CGLIB subclass of class [" +
					this.target.getClass() + "]: " +
					"Common causes of this problem include using a final class or a non-visible class",
					ex);
		}
		catch (Exception ex) {
			// TargetSource.getTarget() failed
			throw new RuntimeException("Unexpected AOP exception", ex);
		}
	}

	protected Enhancer createEnhancer() {
		return new Enhancer();
	}
	
	@Override
	public int hashCode() {
		return Cglib2AopProxy.class.hashCode() * 13 + target.hashCode();
	}	
	
	private void doValidateClass(Class<?> proxySuperClass) {
		Method[] methods = proxySuperClass.getMethods();
		for (Method method : methods) {
			if (!Object.class.equals(method.getDeclaringClass()) && Modifier.isFinal(method.getModifiers())) {
				logger.warn("Unable to proxy method [" + method + "] because it is final: " +
						"All calls to this method via a proxy will be routed directly to the proxy.");
			}
		}
	}

	private static class ProxyCallbackFilter implements CallbackFilter {

		private final Object advised;
		private final List<Advice> adviceList;

		public ProxyCallbackFilter(Object advised, List<Advice> adviceList) {
			this.advised = advised;
			this.adviceList = adviceList;
		}
	
		public int accept(Method method) {
			logger.info(advised.toString());
			if (Reflections.isFinalizeMethod(method)) {
				logger.debug("Found finalize() method - using NO_OVERRIDE");
				return NO_OVERRIDE;
			}
	
			// We must always proxy equals, to direct calls to this.
			if (Reflections.isEqualsMethod(method)) {
				logger.debug("Found 'equals' method: " + method);
				return INVOKE_EQUALS;
			}
			// We must always calculate hashCode based on the proxy.
			if (Reflections.isHashCodeMethod(method)) {
				logger.debug("Found 'hashCode' method: " + method);
				return INVOKE_HASHCODE;
			}
					
			boolean haveAdvice = !adviceList.isEmpty();
	
			if (haveAdvice) {			
				return AOP_PROXY;
			}
			else {				
				return INVOKE_TARGET;				
			}
		}

		@Override
		public boolean equals(Object other) {
			if (other == this) {
				return true;
			}
			if (!(other instanceof ProxyCallbackFilter)) {
				return false;
			}
			return super.equals(other);
		}
	}
	
	

}
