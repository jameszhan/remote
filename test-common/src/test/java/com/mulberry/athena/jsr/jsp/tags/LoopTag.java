package com.mulberry.athena.jsr.jsp.tags;

import javax.servlet.jsp.JspException;

public class LoopTag extends LoggingTagSupport{

	private static final long serialVersionUID = 1L;
	
	private int times = 0;

	public void setTimes(int times) {
		this.times = times;
	}

	public int doStartTag() throws JspException {
		super.doStartTag();
		return EVAL_BODY_INCLUDE;
	}

	public int doAfterBody() throws JspException {
		super.doAfterBody();
		if (--times > 0) {			
			return EVAL_BODY_AGAIN;
		}
		return SKIP_BODY;
	}
	
	
	
	

}
