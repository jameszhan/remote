package com.apple.remote.httpinvoker;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.apple.reflect.util.ReflectionHelper;
import com.apple.remote.RemoteInvocation;
import com.apple.remote.RemoteInvocationResult;
import com.apple.remote.rmi.CodebaseAwareObjectInputStream;

public class HttpInvokerServiceExporter {

	public static final String CONTENT_TYPE_SERIALIZED_OBJECT = "application/x-java-serialized-object";
	
	private String contentType = CONTENT_TYPE_SERIALIZED_OBJECT;
	private Object serviceInstance;

	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			RemoteInvocation invocation = readRemoteInvocation(request);
			RemoteInvocationResult result = invokeAndCreateResult(invocation, serviceInstance);
			writeRemoteInvocationResult(request, response, result);
		} catch (ClassNotFoundException ex) {
			throw new ServletException("Class not found during deserialization", ex);
		}
	}

	protected RemoteInvocation readRemoteInvocation(HttpServletRequest request) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = createObjectInputStream(request.getInputStream());
		try {
			return doReadRemoteInvocation(ois);
		} finally {
			ois.close();
		}
	}
	
	protected RemoteInvocationResult invokeAndCreateResult(RemoteInvocation invocation, Object targetObject) {
		try {
			Object value = invocation.invoke(targetObject);
			return new RemoteInvocationResult(value);
		} catch (Throwable ex) {
			return new RemoteInvocationResult(ex);
		}
	}


	protected void writeRemoteInvocationResult(HttpServletRequest request, HttpServletResponse response, RemoteInvocationResult result) throws IOException {
		response.setContentType(getContentType());
		ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
		try {
			doWriteRemoteInvocationResult(result, oos);
			oos.flush();
		} finally {
			oos.close();
		}
	}

	protected RemoteInvocation doReadRemoteInvocation(ObjectInputStream ois) throws IOException, ClassNotFoundException {

		Object obj = ois.readObject();
		if (!(obj instanceof RemoteInvocation)) {
			throw new RemoteException("Deserialized object needs to be assignable to type [" + RemoteInvocation.class.getName() + "]: " + obj);
		}
		return (RemoteInvocation) obj;
	}

	protected void doWriteRemoteInvocationResult(RemoteInvocationResult result, ObjectOutputStream oos) throws IOException {
		oos.writeObject(result);
	}
	
	protected ObjectInputStream createObjectInputStream(InputStream is) throws IOException {
		return new CodebaseAwareObjectInputStream(is, ReflectionHelper.getContextClassLoader(), null);
	}
	
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Object getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(Object serviceInstance) {
		this.serviceInstance = serviceInstance;
	}
	
	

}
