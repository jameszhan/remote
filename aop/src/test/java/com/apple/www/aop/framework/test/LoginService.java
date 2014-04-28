package com.apple.www.aop.framework.test;

public interface LoginService {
	
	boolean login(String username, String password);
	
	void sayHello(String message);
	
	boolean logout(String username);
	
	void exception();
}
