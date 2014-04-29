package com.google.restful.provider.entity;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.spi.scanning.AnnotationScannerListener;

@Produces({"text/html", "text/plain"})
@Provider
public class ListProvider implements MessageBodyWriter<List<?>>
{	
	private List<Processer> processors = new ArrayList<Processer>();
	
	public ListProvider()
	{
		try {
			initialize();
		} catch (InstantiationException e) {		
			e.printStackTrace();
		} catch (IllegalAccessException e) {			
			e.printStackTrace();
		}				
	}
	
	public void initialize() throws InstantiationException, IllegalAccessException {
		AnnotationScannerListener scanner = new AnnotationScannerListener(ListProcessor.class);
		Set<Class<?>> set = scanner.getAnnotatedClasses();
		for(Class<?> clazz : set){
			processors.add((Processer) clazz.newInstance());
		}		
	}
		
	@Override
	public long getSize(List<?> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {		
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {		
		return List.class.isAssignableFrom(type);
	}

	@Override
	public void writeTo(List<?> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException, WebApplicationException {
		String content = getListString(t, mediaType);
		entityStream.write(content.getBytes());
	}
	
	
	protected String getListString(List<?> t, MediaType mediaType){
		for(Processer p : processors){
			if(p.accept(t.get(0).getClass())){
				return p.process(t, mediaType);
			}
		}	
		return null;
	}
	


}
