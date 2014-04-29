package com.google.restful.user.storage;

import java.util.ArrayList;
import java.util.List;

import com.google.restful.user.bean.User;

public class UserStorage {
	
	private final static UserStorage storage = new UserStorage();
	private List<User> users = new ArrayList<User>();
	static{
		storage.users.add(new User("user-001", "James"));
		storage.users.add(new User("user-002", "KK"));
	}
	
	public static UserStorage getStorage(){
		return storage;		
	}
	
	public List<User> getUsers(){
		return users;		
	}

	public boolean add(User e) {
		return users.add(e);
	}

	public boolean remove(Object o) {
		return users.remove(o);
	}
	
	public User getUser(String userId){
		for(User u : users){
			if(u.getUserId().equals(userId)){
				return u;
			}
		}
		return null;
	}
	
	public void updateUser(User user){
		for(User u : users){
			if(u.getUserId().equals(user.getUserId())){
				u.setName(user.getName());
			}
		}
	}
	
	public void deleteUser(String userId){
		User user = null;
		for(User u : users){
			if(u.getUserId().equals(userId)){
				user = u;
			}
		}
		users.remove(user);
	}
	
}
