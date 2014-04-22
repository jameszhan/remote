package com.test.jaxws;

public interface UserService {
	
	User getUserInfo(User user);
	

}


class User {
	public String name;
	public String info;
	
	public String toString(){
		return name + " " + info; 
	}
}