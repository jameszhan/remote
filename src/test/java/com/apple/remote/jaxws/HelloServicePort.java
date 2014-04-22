package com.apple.remote.jaxws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(name = "HelloService", targetNamespace = "http://jaxws.remote.apple.com/")
public interface HelloServicePort {

	@WebMethod(operationName = "greet")
	public String sayHello(@WebParam(name = "username", mode = WebParam.Mode.IN) String username);
	
	@WebMethod
	public User getUser(String name);

}
