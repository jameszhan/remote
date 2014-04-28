package com.apple.www.aop.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class CommonMethodInterceptor implements MethodInterceptor {

	private Object target;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {

		Method method = invocation.getMethod();
		try {
			return method.invoke(target, invocation.getArguments());
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

}
