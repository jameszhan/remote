package com.apple.remote;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

public class RemoteInvocationResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private Object value;
	private Throwable exception;

	public RemoteInvocationResult(Object value) {
		this.value = value;
	}

	public RemoteInvocationResult(Throwable exception) {
		this.exception = exception;
	}

	public boolean hasException() {
		return (this.exception != null);
	}

	public boolean hasInvocationTargetException() {
		return (this.exception instanceof InvocationTargetException);
	}

	public Object getValue() {
		if (exception != null) {
			throw new IllegalStateException("There is some exception while remote invocation.", exception);
		}
		return value;
	}
}
