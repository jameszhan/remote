package com.apple.jaxws.sample;

import javax.jws.WebService;


@WebService(endpointInterface="com.apple.jaxws.sample.HelloService")
public class HelloServiceImp implements HelloService{

	@Override
	public String greet(String username) {		
		return "hELLO " + username;
	}

	@Override
	public User getUser(String arg0) {
		User user = new User();
		user.setId("Peter");
		user.setName("Name");
		return user;
	}

}
