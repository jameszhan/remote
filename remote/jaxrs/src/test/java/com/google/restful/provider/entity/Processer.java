package com.google.restful.provider.entity;

import java.util.List;

import javax.ws.rs.core.MediaType;

public interface Processer {
	
	boolean accept(Class<?> clazz);
	String process(List<?> t, MediaType mediaType);
	
}
