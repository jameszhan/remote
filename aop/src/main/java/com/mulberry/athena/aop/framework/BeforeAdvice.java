package com.mulberry.athena.aop.framework;

import java.lang.reflect.Method;

import org.aopalliance.aop.Advice;

public interface BeforeAdvice extends Advice{
	
	void before(Method method, Object[] args, Object target) throws Throwable;

}
