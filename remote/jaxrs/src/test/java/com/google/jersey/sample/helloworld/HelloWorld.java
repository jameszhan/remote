package com.google.jersey.sample.helloworld;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("helloworld")
public class HelloWorld {
	
	@GET
	@Produces("text/plain")
	public String helloWorld(){		
		return "Hello World!";
	}
	
}
