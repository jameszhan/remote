package com.google.restful.user.bean;


import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User {
	
	private String userId;
	private String name;
	
	public User() {
		
	}

	public User(String userId, String name) {
		super();
		this.userId = userId;
		this.name = name;
	}

	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;				
	}
	
	
	
	
}
