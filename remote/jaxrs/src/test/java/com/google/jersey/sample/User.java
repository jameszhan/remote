package com.google.jersey.sample;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User {
	
	private String id;
	private String name;
	private String gender;
	private String age;
	

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getId() {
		return id;
	}

	public void setId(String value) {
		id = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String value) {
		name = value;
	}
}
