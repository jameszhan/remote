package com.apple.www.aop.framework;

import org.aopalliance.aop.Advice;


public interface AopProxyFactory {
	
	AopProxy createAopProxy(Object target, Advice... advices);

}
