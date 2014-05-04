package com.mulberry.athena.aop.framework.test;

public class LoginServiceImp implements LoginService{

	@Override
	public boolean login(String username, String password) {
		System.out.format("username: %s, password: %s have login.\n", username, password);
		return true;
	}

	@Override
	public void sayHello(String message) {
		System.out.println(message);		
	}

	@Override
	public boolean logout(String username) {
		System.out.format("username: %s has log out.\n", username);
		return true;
	}

	@Override
	public void exception() {
		throw new IllegalStateException("My Exception.");		
	}
	
}
