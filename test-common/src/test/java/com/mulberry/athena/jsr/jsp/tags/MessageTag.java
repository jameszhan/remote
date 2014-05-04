package com.mulberry.athena.jsr.jsp.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;


public class MessageTag extends SimpleTagSupport{

	@Override
	public void doTag() throws JspException, IOException {		
		super.doTag();
		JspWriter out = getJspContext().getOut();
		out.write("<span style=\"color:red; font-weight:bold\">");
        JspFragment jspFragment = getJspBody();
        if (jspFragment != null) {
            jspFragment.invoke(null);
        }
		out.write("</span>");
	}
	
}
