package com.mulberry.athena.aop.framework;

public interface AopProxy {

	Object getProxy();

	
	Object getProxy(ClassLoader classLoader);

}
