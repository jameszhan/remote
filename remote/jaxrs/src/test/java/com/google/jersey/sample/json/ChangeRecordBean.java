
package com.google.jersey.sample.json;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "change")
public class ChangeRecordBean {

	@XmlAttribute
	public boolean madeByAdmin;

	public int linesChanged;
	public String logMessage;

	public ChangeRecordBean() {
	};

	public ChangeRecordBean(boolean madeByAdmin, int linesChanged, String logMessage) {
		this.madeByAdmin = madeByAdmin;
		this.linesChanged = linesChanged;
		this.logMessage = logMessage;
	}
}
