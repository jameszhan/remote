package com.apple.www.jsptag.samples;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LifecycleTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;
	private int maxLoop = 5;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	private StringBuffer sb = new StringBuffer();
	
	private int startFlag = EVAL_BODY_BUFFERED;
	private int afterBodyFlag = SKIP_BODY;
	private int endFlag = EVAL_PAGE;	

	@Override
	public int doStartTag() throws JspException {	
		maxLoop = 5;
		logger.info("******doStartTag: {}", this);
		sb.append("[").append("doStartTag:").append(this.hashCode()).append("]\n");
		return startFlag;
	}
	
	@Override
	public void doInitBody() throws JspException {	
		sb.append("\t[").append("doInitBody:").append(this.hashCode()).append("]\n");
		logger.info("************doInitBody: {}", this);
		super.doInitBody();
	}
	
	@Override
	public int doAfterBody() throws JspException {	
		sb.append("\t[").append("doAfterBody:").append(this.hashCode()).append("]\n");
		logger.info("************doAfterBody: {}", this);
		if(--maxLoop <= 0){
			return SKIP_BODY;
		}
		return afterBodyFlag;
	}

	@Override
	public int doEndTag() throws JspException {	
		sb.append("[").append("doEndTag:").append(this.hashCode()).append("]\n");
		logger.info("******doEndTag: {}", this);
		try {
			JspWriter out = pageContext.getOut();
			out.println(sb.toString());			
		} catch (IOException e) {			
			e.printStackTrace();
		}finally{
			sb.delete(0, sb.length());
		}
		return endFlag;
	}	

	@Override
	public void release() {	
		sb.append("[").append("release:").append(this.hashCode()).append("]\n");
		logger.info("release: {}", this);
		super.release();
	}
	
	@Override
	public void setId(String id) {
		sb.append("[").append("setId:").append(id).append(" ").append(this.hashCode()).append("]\n");
		logger.info("Calling by system, setId: {}", id);
		super.setId(id);
	}

	@Override
	public void setPageContext(PageContext pageContext) {	
		sb.append("[").append("setPageContext:").append(pageContext).append(" ").append(this.hashCode()).append("]\n");
		logger.info("Calling by system, setPageContext: {}", pageContext);
		super.setPageContext(pageContext);
	}

	@Override
	public void setParent(Tag t) {	
		sb.append("[").append("setParent:").append(t).append(" ").append(this.hashCode()).append("]\n");
		logger.info("Calling by system, setParent: {}", t);
		super.setParent(t);
	}	

	@Override
	public void setBodyContent(BodyContent b) {
		sb.append("\t[").append("setBodyContent:").append(b).append(" ").append(this.hashCode()).append("]\n");
		logger.info("Calling by system, setBodyContent: {}", b);
		super.setBodyContent(b);
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
	

	@Override
	public BodyContent getBodyContent() {		
		return super.getBodyContent();
	}

	@Override
	public JspWriter getPreviousOut() {	
		return super.getPreviousOut();
	}

	public void setStartFlag(int startFlag) {
		this.startFlag = startFlag;
	}

	public void setAfterBodyFlag(int afterBodyFlag) {
		this.afterBodyFlag = afterBodyFlag;
	}

	public void setEndFlag(int endFlag) {
		this.endFlag = endFlag;
	}

}
