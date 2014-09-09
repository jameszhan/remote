package com.google.restful.provider.entity;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.Sets;
import com.mulberry.athena.toolkit.reflect.Reflections;
import com.mulberry.athena.toolkit.scan.Scanners;

@Produces({"text/html", "text/plain"})
@Provider
public class ListProvider implements MessageBodyWriter<List<?>> {

	private Set<Processor> processors = Sets.newConcurrentHashSet();
	
	public ListProvider() {
        try {
            Collection<Class<?>> classes = Scanners.annotatedBy("com.mulberry.athena.remote.jaxws", ListProcessor.class);
            for (Class<?> clazz : classes) {
                processors.add((Processor)Reflections.instantiate(clazz));
            }
        } catch (Exception e) {
            //FIXME
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
		for(Processor p : processors){
			if(p.accept(t.get(0).getClass())){
				return p.process(t, mediaType);
			}
		}	
		return null;
	}
	


}
