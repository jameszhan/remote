package com.mulberry.athena.remote.jaxrs.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBException;

import com.mulberry.athena.remote.jaxrs.demo.entity.Person;
import com.mulberry.athena.remote.jaxrs.demo.entity.PersonList;
import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.jersey.api.json.JSONMarshaller;
import com.sun.jersey.api.json.JSONUnmarshaller;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;

@Provider
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.TEXT_PLAIN)
public class PlainTextReaderWriterProvider extends AbstractMessageReaderWriterProvider<Object> {
	
	private JSONMarshaller marshaller;
	private JSONUnmarshaller unmarshaller;

	public PlainTextReaderWriterProvider() throws JAXBException {
		JSONJAXBContext context = new JSONJAXBContext(Person.class, PersonList.class);
		marshaller = context.createJSONMarshaller();
		unmarshaller = context.createJSONUnmarshaller();
	}

	@Override
	public boolean isReadable(Class<?> clazz, Type type, Annotation[] as,
			MediaType mime) {
		return mime.equals(MediaType.TEXT_PLAIN_TYPE);
	}

	@Override
	public Object readFrom(Class<Object> clazz, Type type, Annotation[] as,
			MediaType mime, MultivaluedMap<String, String> map,
			InputStream in) throws IOException, WebApplicationException {
		
		try {
			return unmarshaller.unmarshalFromJSON(in, clazz);
		} catch (JAXBException e) {			
			throw new IOException(e);
		}
	}

	@Override
	public boolean isWriteable(Class<?> clazz, Type type, Annotation[] as,
			MediaType mime) {
		return mime.equals(MediaType.TEXT_PLAIN_TYPE);
	}

	@Override
	public void writeTo(Object obj, Class<?> clazz, Type type,
			Annotation[] as, MediaType mime,
			MultivaluedMap<String, Object> map, OutputStream out)
			throws IOException, WebApplicationException {
		try {
			marshaller.marshallToJSON(obj, out);
		} catch (JAXBException e) {
			throw new IOException(e);
		}
	}

}
