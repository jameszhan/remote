package com.apple.remote.jaxws;

import java.util.UUID;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

@WebService(serviceName = "HelloService")
@SOAPBinding(parameterStyle=ParameterStyle.BARE)
public class HelloService {
	
	@WebMethod(operationName="greet")
	public String sayHello(@WebParam(name="username", mode=WebParam.Mode.IN)  String username){
		return "Hello " + username + "!";
	}	
	
	public User getUser(String name){
		User user = new User();
		user.setName(name);
		user.setId(UUID.randomUUID());
		return user;	
	}
	
}
