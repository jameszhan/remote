package com.google.restful.user.resource;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.google.restful.user.bean.User;
import com.google.restful.user.storage.UserStorage;

@Path("/users")
public class UsersResource {

	@GET
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML, MediaType.TEXT_PLAIN })
	public List<User> getUsers() {
		return UserStorage.getStorage().getUsers();
	}

	@GET
	@Path("count")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML, MediaType.TEXT_PLAIN })
	public String getCount() {
		int count = UserStorage.getStorage().getUsers().size();
		return String.valueOf(count);
	}

	@GET
	@Path("/{userId}/")
	public User getItem(@PathParam("userId") String userId) {
		User user = UserStorage.getStorage().getUser(userId);
		if (user == null)
			throw new NotFoundException("User, " + userId + ", is not found");
		return user;
	}

	@POST	
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML, MediaType.TEXT_PLAIN })
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<User> createUser(@FormParam("id") String id, @FormParam("name") String name) {
		User user = new User(id, name);
		UserStorage.getStorage().add(user);
		return getUsers();
	}
	
	@PUT
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML, MediaType.TEXT_PLAIN })
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<User> updateUser(@FormParam("id") String id, @FormParam("name") String name) {		
		User user = new User(id, name);
		UserStorage.getStorage().updateUser(user);
		return getUsers();
	}
	
	@DELETE
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML, MediaType.TEXT_PLAIN })
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<User> deleteUser(@FormParam("id") String id) {		
		UserStorage.getStorage().deleteUser(id);
		return getUsers();
	}
	
	
}
