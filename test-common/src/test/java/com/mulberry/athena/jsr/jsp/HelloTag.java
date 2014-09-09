package com.mulberry.athena.jsr.jsp;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class HelloTag extends SimpleTagSupport {

	private String message = "Hello World!";
	
	@Override
	public void doTag() throws JspException, IOException {		
		JspWriter out = getJspContext().getOut();
		out.print(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}