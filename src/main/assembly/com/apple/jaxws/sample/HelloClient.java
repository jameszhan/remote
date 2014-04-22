package com.apple.jaxws.sample;

public class HelloClient {
	
	public static void main(String[] args) {
		HelloService_Service service = new HelloService_Service();
		HelloService svc = service.getHelloServicePort();
		String hello = svc.greet("James");
		System.out.println(hello);
		
	}

}
