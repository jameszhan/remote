package com.apple.www.aop.framework;

public interface AopProxy {

	Object getProxy();

	
	Object getProxy(ClassLoader classLoader);

}
