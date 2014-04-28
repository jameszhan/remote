package com.apple.www.aop.framework.test;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class RoundMethodInterceptor implements MethodInterceptor{

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		System.out.println("\n-----Before Around---RoundMethodInterceptor.");		
		Object ret = invocation.proceed();	
		System.out.println("------After Around---RoundMethodInterceptor.\n");
	
		return ret;
	}

}
