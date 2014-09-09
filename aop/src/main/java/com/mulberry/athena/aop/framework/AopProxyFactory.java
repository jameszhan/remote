package com.mulberry.athena.aop.framework;

import org.aopalliance.aop.Advice;


public interface AopProxyFactory {
	
	AopProxy createAopProxy(Object target, Advice... advices);

}
