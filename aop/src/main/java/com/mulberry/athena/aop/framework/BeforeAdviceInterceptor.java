package com.mulberry.athena.aop.framework;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class BeforeAdviceInterceptor implements MethodInterceptor {
	
	private final BeforeAdvice advice;

	public BeforeAdviceInterceptor(BeforeAdvice advice) {
		super();
		this.advice = advice;
	}

	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		advice.before(mi.getMethod(), mi.getArguments(), mi.getThis());
		return mi.proceed();
	}

}
