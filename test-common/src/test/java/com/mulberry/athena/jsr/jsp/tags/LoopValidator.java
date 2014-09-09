package com.mulberry.athena.jsr.jsp.tags;

import java.util.Enumeration;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.ValidationMessage;
import javax.servlet.jsp.tagext.VariableInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoopValidator extends TagExtraInfo {
	
	private final Logger logger = LoggerFactory.getLogger(LoopValidator.class);

	@Override
	public VariableInfo[] getVariableInfo(TagData data) {		
		return super.getVariableInfo(data);
	}

	@Override
	public boolean isValid(TagData data) {	
		try {	
			Integer.parseInt(data.getAttribute("times").toString());
			return true;
		}catch(Exception ex){
			return false;
		}		
	}

	@Override
	public ValidationMessage[] validate(TagData data) {		
		Enumeration<String> attrs = data.getAttributes();
		while(attrs.hasMoreElements()){
			String attrName = attrs.nextElement();
			logger.info("{key: {} - value: {}}", attrName, data.getAttribute(attrName));
		}
		return super.validate(data);
	}
	
	

}
