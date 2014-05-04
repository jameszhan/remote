package com.mulberry.athena.aop.framework;

import java.util.Arrays;

import org.aopalliance.aop.Advice;

public class DefaultAopProxyFactory implements AopProxyFactory {

	@Override
	public AopProxy createAopProxy(Object target, Advice... advices) {		
		return new JdkDynamicAopProxy(target, Arrays.asList(advices));
	}

}
