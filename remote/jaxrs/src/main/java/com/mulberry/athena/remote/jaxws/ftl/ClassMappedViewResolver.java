package com.mulberry.athena.remote.jaxws.ftl;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;

//import com.mulberry.athena.remote.jaxrs.demo.entity.Person;
import org.slf4j.LoggerFactory;

@FtlAssemble
public class ClassMappedViewResolver implements ViewResolver {

	private final static Logger LOGGER = LoggerFactory.getLogger(ClassMappedViewResolver.class);
	private Map<Class<?>, String> map = new HashMap<Class<?>, String>();

	private @Context UriInfo ui;

	public ClassMappedViewResolver() {
	//	map.put(List.class, "list.ftl");
	//	map.put(Person.class, "person.ftl");
	}

	@Override
	public int getOrder() {
		return 10;
	}

	@Override
	public String resolveViewName(Object t, Class<?> type, OutputStream entityStream) {	
		String viewName = null;
		for(Class<?> clazz : map.keySet()){
			if(clazz.isAssignableFrom(type)){
				viewName = map.get(clazz);
				LOGGER.info(String.format("For request:%s, find view:%s!", ui.getRequestUri(), viewName));
				break;
			}
		}		
		return viewName;
	}



}
