package com.mulberry.athena.remote.jaxrs.demo.entity;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Contact {
	
	private String addr;
	private String email;
	private String tel;
	private String cellphone;
	
	private Map<String, String> contacts = new HashMap<String, String>();
	
	
	public String put(String key, String value) {
		return contacts.put(key, value);
	}

	public String remove(Object key) {
		return contacts.remove(key);
	}
	
	public Map<String, String> getContacts() {
		return contacts;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}
	
	
	
	

}
