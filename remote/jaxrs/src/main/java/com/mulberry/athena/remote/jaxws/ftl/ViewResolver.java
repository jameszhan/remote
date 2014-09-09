package com.mulberry.athena.remote.jaxws.ftl;

import java.io.OutputStream;

public interface ViewResolver {
	
	int getOrder();
	
	String resolveViewName(Object t, Class<?> type, OutputStream entityStream);

}
