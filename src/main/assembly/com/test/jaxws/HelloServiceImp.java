package com.test.jaxws;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

@WebService(endpointInterface="com.test.jaxws.HelloService")
public class HelloServiceImp implements HelloService{
	
	@WebMethod
	public String sayHello(String name){
		return "Hello " + name + "!";
	}
	
	public static void main(String[] args) {		
		Endpoint endpoint = Endpoint.create(new HelloServiceImp());
		endpoint.publish("http://localhost:8888/HelloService");		
	}

	@Override
	public String test1() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String test2() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String test3() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String test4() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String test5() {
		// TODO Auto-generated method stub
		return null;
	}

}
