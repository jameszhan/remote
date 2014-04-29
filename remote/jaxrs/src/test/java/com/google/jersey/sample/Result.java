package com.google.jersey.sample;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class Result<T> {

	private boolean success;
	private String message;
	private T data;

	public boolean getSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@XmlTransient
	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
