package com.apple.remote.jaxws;

import java.util.UUID;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "user", propOrder = { "id", "name" })
public class User {

	private UUID id;
	private String name;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
