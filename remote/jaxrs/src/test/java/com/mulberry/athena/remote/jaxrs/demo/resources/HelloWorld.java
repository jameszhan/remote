package com.mulberry.athena.remote.jaxrs.demo.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/hello")
public class HelloWorld {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHello(){
        return "Hello Restful!";
    }

}