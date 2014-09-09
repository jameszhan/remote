package com.mulberry.athena.remote.jaxrs.demo.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Person {
	
	private String name;	
	private String company;
	private String university;	
	private String nativePlace;
	
	private Contact contact;
		
	public Person() {
	
	}

	public Person(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}

	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getUniversity() {
		return university;
	}
	public void setUniversity(String university) {
		this.university = university;
	}
	public String getNativePlace() {
		return nativePlace;
	}
	public void setNativePlace(String nativePlace) {
		this.nativePlace = nativePlace;
	}
	
	@XmlElement(name="Contact")
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contacts) {
		this.contact = contacts;
	}

}
