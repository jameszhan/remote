package com.mulberry.athena.aop.framework;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class ReflectiveMethodInvocation implements MethodInvocation {

	protected final Object proxy;
	protected final Object target;
	protected final Method method;
	protected Object[] arguments;

	private List<MethodInterceptor> interceptorList;

	public ReflectiveMethodInvocation(Object proxy, Object target, Method method, Object[] arguments,
			List<MethodInterceptor> interceptorList) {
		super();
		this.proxy = proxy;
		this.target = target;
		this.method = method;
		this.arguments = arguments;
		this.interceptorList = interceptorList;
	}

	private int currentInterceptorIndex = -1;

	public final Object getProxy() {
		return this.proxy;
	}

	public final Object getThis() {
		return this.target;
	}

	public final AccessibleObject getStaticPart() {
		return this.method;
	}

	public final Method getMethod() {
		return this.method;
	}

	public final Object[] getArguments() {
		return (this.arguments != null ? this.arguments : new Object[0]);
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	@Override
	public Object proceed() throws Throwable {
		if (this.currentInterceptorIndex == this.interceptorList.size() - 1) {
			return method.invoke(target, arguments);
		}
		MethodInterceptor interceptor = interceptorList.get(++currentInterceptorIndex);
		return interceptor.invoke(this);
	}

}
