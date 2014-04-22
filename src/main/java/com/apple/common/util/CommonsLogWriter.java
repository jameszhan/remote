package com.apple.common.util;

import java.io.Writer;

import org.apache.log4j.Logger;


public class CommonsLogWriter extends Writer {

	private final Logger logger;

	private final StringBuffer buffer = new StringBuffer();

	public CommonsLogWriter(Logger logger) {		
		this.logger = logger;
	}


	public void write(char ch) {
		if (ch == '\n' && this.buffer.length() > 0) {
			this.logger.debug(this.buffer.toString());
			this.buffer.setLength(0);
		}
		else {
			this.buffer.append((char) ch);
		}
	}

	public void write(char[] buffer, int offset, int length) {
		for (int i = 0; i < length; i++) {
			char ch = buffer[offset + i];
			if (ch == '\n' && this.buffer.length() > 0) {
				this.logger.debug(this.buffer.toString());
				this.buffer.setLength(0);
			}
			else {
				this.buffer.append((char) ch);
			}
		}
	}

	public void flush() {
	}

	public void close() {
	}

}
