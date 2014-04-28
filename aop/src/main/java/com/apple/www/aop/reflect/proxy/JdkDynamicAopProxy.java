package com.apple.www.aop.reflect.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.mulberry.toolkit.reflect.Reflections;


public class JdkDynamicAopProxy implements InvocationHandler {

	private final Class<?>[] proxiedInterfaces;
	private final Object target;
	
	public JdkDynamicAopProxy(Object target, Class<?>... proxiedInterfaces) {		
		this.proxiedInterfaces = proxiedInterfaces;
		this.target = target;
	}	
	
	public Object getProxy(){
		return getProxy(Reflections.getContextClassLoader());
	}
	
	public Object getProxy(ClassLoader classLoader) {	
		return Proxy.newProxyInstance(classLoader, proxiedInterfaces, this);
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Reflections.makeAccessible(method);
		return method.invoke(target, args);
	}
}
