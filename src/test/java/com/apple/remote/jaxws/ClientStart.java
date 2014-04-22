package com.apple.remote.jaxws;

import java.net.MalformedURLException;
import java.net.URL;

public class ClientStart {
	
	public static void main(String[] args) throws MalformedURLException {
		JaxWsPortPoxyFactory pf = new JaxWsPortPoxyFactory();
		pf.setWsdlDocumentUrl(new URL("http://localhost:8080/HelloService?wsdl"));
		pf.setServiceName("HelloService");
		pf.setServiceInterface(HelloServicePort.class);
		pf.setNamespaceUri("http://jaxws.remote.apple.com/");
		pf.init();
		
		HelloServicePort proxy = (HelloServicePort) pf.getProxy();
				
		String result = proxy.sayHello("James");
		System.out.println(result);
		User user = proxy.getUser("James");
		System.out.println(user.getId());
		System.out.println(user.getName());
		
	}

}
