package com.mulberry.athena.remote.jaxrs.demo.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PersonList {
	
	private List<Person> list;
	
	@XmlElement(name="list")
	public List<Person> getList() {
		return list;
	}

	public void setList(List<Person> list) {
		this.list = list;
	}
	
	
}
