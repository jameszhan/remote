package com.apple.remote;

public class HelloServiceImp implements HelloService {

	@Override
	public String sayHello(String message) {		
		return "Hello " + message;
	}

	@Override
	public String echo(String message) {	
		return "HelloService echo: " + message;
	}

}
