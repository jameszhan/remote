package com.mulberry.athena.aop.framework;

import java.lang.reflect.Method;

import org.aopalliance.aop.Advice;

public interface AfterAdvice extends Advice{
	
	void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable;
	
}
