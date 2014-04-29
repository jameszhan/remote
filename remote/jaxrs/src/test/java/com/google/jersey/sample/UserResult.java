package com.google.jersey.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("users")
public class UserResult {

	private Map<String, User> users = new HashMap<String, User>();
	
	@GET
	@Path("{id}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Result<User> show(@PathParam("id") String id) {
		User user = new User();
		user.setId(id);
		user.setName("user" + id);
		Result<User> result = new Result<User>();
		result.setSuccess(true);
		result.setData(user);
		return result;
	}

	@GET
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Result<List<User>> list() {
		List<User> userList = new ArrayList<User>();
		User user1 = new User();
		user1.setId("1");
		user1.setName("user1");
		userList.add(user1);
		User user2 = new User();
		user2.setId("2");
		user2.setName("user2");
		userList.add(user2);
		Result<List<User>> result = new Result<List<User>>();
		result.setSuccess(true);
		result.setData(userList);
		return result;
	}

}
