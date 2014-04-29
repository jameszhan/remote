package com.apple.www.jsptag.samples;

import java.util.Enumeration;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingTagSupport extends TagSupport{

	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public int doStartTag() throws JspException {	
		logger.info("doStartTag: {}", this);
		return super.doStartTag();
	}
	
	@Override
	public int doAfterBody() throws JspException {		
		logger.info("doAfterBody: {}", this);
		return super.doAfterBody();
	}

	@Override
	public int doEndTag() throws JspException {	
		logger.info("doEndTag: {}", this);
		return super.doEndTag();
	}	

	@Override
	public void release() {	
		logger.info("release: {}", this);
		super.release();
	}
	
	@Override
	public void setId(String id) {
		logger.info("Calling by system, setId: {}", id);
		super.setId(id);
	}

	@Override
	public void setPageContext(PageContext pageContext) {	
		logger.info("Calling by system, setPageContext: {}", pageContext);
		super.setPageContext(pageContext);
	}

	@Override
	public void setParent(Tag t) {	
		logger.info("Calling by system, setParent: {}", t);
		super.setParent(t);
	}

	@Override
	public void setValue(String k, Object o) {	
		logger.info("set value ({}: {}) ", k, o);
		super.setValue(k, o);
	}
	
	@Override
	public Object getValue(String k) {	
		logger.info("get value for key: {}", k);
		return super.getValue(k);
	}
	
	@Override
	public void removeValue(String k) {	
		logger.info("remove value (key = {})", k);
		super.removeValue(k);
	}	

	@Override
	public Enumeration<String> getValues() {
		logger.info("Calling getValues().");
		return super.getValues();
	}	

	@Override
	public String getId() {		
		return super.getId();
	}

	@Override
	public Tag getParent() {		
		return super.getParent();
	}

}
