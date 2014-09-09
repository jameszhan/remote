package com.google.jersey.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("user")
public class UserResource {
	
	private static List<User> s_users = new ArrayList<User>();
	private static AtomicInteger s_ids = new AtomicInteger();
	
	static{
		User user = new User();
		String userId = "user-" + s_ids.incrementAndGet();
		user.setId(userId);
		user.setName("James");
		s_users.add(user);
	}
	
	@GET
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<User> list() {
		return s_users;
	}

}
