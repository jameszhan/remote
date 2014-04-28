package com.apple.www.aop.framework;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class AfterAdviceInterceptor implements MethodInterceptor {
	
	private final AfterAdvice advice;
	
	public AfterAdviceInterceptor(AfterAdvice advice) {
		super();
		this.advice = advice;
	}
	
	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		Object ret = mi.proceed();
		advice.afterReturning(ret, mi.getMethod(), mi.getArguments(), mi.getThis());
		return ret;
	}

}
