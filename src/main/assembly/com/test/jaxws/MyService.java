package com.test.jaxws;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService(serviceName="HelloService", name="HelloService")
public interface MyService {

	@WebMethod(operationName="greet")
	String test(String name);

}
