package com.test.jaxws;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService(serviceName="HelloService", name="HelloService")
public interface HelloService {
	
	@WebMethod(operationName="greet")
	String sayHello(String name);
	
	String test1();
	
	String test2();
	
	String test3();
	
	String test4();
	
	String test5();

}
