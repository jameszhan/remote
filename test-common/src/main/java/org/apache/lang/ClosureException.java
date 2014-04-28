package org.apache.lang;

public class ClosureException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ClosureException() {
		super();		
	}

	public ClosureException(String message, Throwable cause) {
		super(message, cause);	
	}

	public ClosureException(String message) {
		super(message);		
	}

	public ClosureException(Throwable cause) {
		super(cause);		
	}
	
}
