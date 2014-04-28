package org.apache.lang;

public interface Func<T, TResult> {
	
	TResult call(T t);
	
}
