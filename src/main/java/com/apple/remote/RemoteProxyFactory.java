package com.apple.remote;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.apple.reflect.util.ReflectionHelper;


public class RemoteProxyFactory implements InvocationHandler {

	private final RemoteInvocationHandler stub;
	private final Class<?>[] proxiedInterfaces;
	private Object proxy;
	
		
	public RemoteProxyFactory(RemoteInvocationHandler stub, Class<?>... proxiedInterfaces) {		
		this.stub = stub;
		this.proxiedInterfaces = proxiedInterfaces;
	}
		
	public Object getProxy(){	
		proxy = Proxy.newProxyInstance(ReflectionHelper.getContextClassLoader(), proxiedInterfaces, this);
		return proxy;
	}

	public Object getProxy(ClassLoader classloader){	
		proxy = Proxy.newProxyInstance(classloader, proxiedInterfaces, this);
		return proxy;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		RemoteInvocation invocation = new RemoteInvocation(method.getName(), method.getParameterTypes(), args);
		return stub.invoke(invocation);
	}

}
