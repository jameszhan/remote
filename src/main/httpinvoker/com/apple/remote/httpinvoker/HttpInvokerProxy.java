package com.apple.remote.httpinvoker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.util.zip.GZIPInputStream;

import com.apple.remote.RemoteInvocation;
import com.apple.remote.RemoteInvocationHandler;
import com.apple.remote.RemoteInvocationResult;
import com.apple.remote.RemoteProxyFactory;

public class HttpInvokerProxy implements RemoteInvocationHandler{

	public static final String CONTENT_TYPE_SERIALIZED_OBJECT = "application/x-java-serialized-object";
	protected static final String HTTP_METHOD_POST = "POST";
	protected static final String HTTP_HEADER_ACCEPT_LANGUAGE = "Accept-Language";
	protected static final String HTTP_HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	protected static final String HTTP_HEADER_CONTENT_ENCODING = "Content-Encoding";
	protected static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	protected static final String HTTP_HEADER_CONTENT_LENGTH = "Content-Length";
	protected static final String ENCODING_GZIP = "gzip";

	private static final int SERIALIZED_INVOCATION_BYTE_ARRAY_INITIAL_SIZE = 1024;

	private String contentType = CONTENT_TYPE_SERIALIZED_OBJECT;
	private boolean acceptGzipEncoding = true;
	private String serviceUrl;
	private String codebaseUrl;
	
	@SuppressWarnings("unchecked")
	public <T> T getProxy(Class<T> ifc){
		return (T) new RemoteProxyFactory(this, ifc).getProxy();
	}

	public Object invoke(RemoteInvocation invocation) throws RemoteException {
		try {
			RemoteInvocationResult result = doExecuteRequest(serviceUrl, getByteArrayOutputStream(invocation));
			return result.getValue();
		} catch (IOException e) {
			throw new RemoteException(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			throw new RemoteException(e.getMessage(), e);
		} 
		
	}

	protected RemoteInvocationResult doExecuteRequest(String serviceUrl, ByteArrayOutputStream baos) throws IOException, ClassNotFoundException {

		HttpURLConnection conn = openConnection(serviceUrl);
		prepareConnection(conn, baos.size());
		writeRequestBody(conn, baos);
		validateResponse(conn);
		InputStream responseBody = readResponseBody(conn);

		return readRemoteInvocationResult(responseBody, codebaseUrl);
	}

	protected RemoteInvocationResult readRemoteInvocationResult(InputStream is, String codebaseUrl) throws IOException, ClassNotFoundException {

		ObjectInputStream ois = new ObjectInputStream(is);
			//new CodebaseAwareObjectInputStream(is, ReflectionHelper.getContextClassLoader(), codebaseUrl);
		try {
			return doReadRemoteInvocationResult(ois);
		} finally {
			ois.close();
		}
	}

	protected InputStream readResponseBody(HttpURLConnection con) throws IOException {
		if (isGzipResponse(con)) {
			// GZIP response found - need to unzip.
			return new GZIPInputStream(con.getInputStream());
		} else {
			// Plain response found.
			return con.getInputStream();
		}
	}

	protected void writeRequestBody(HttpURLConnection conn, ByteArrayOutputStream baos) throws IOException {
		baos.writeTo(conn.getOutputStream());
	}

	protected void validateResponse(HttpURLConnection conn) throws IOException {
		if (conn.getResponseCode() >= 300) {
			throw new IOException("Did not receive successful HTTP response: status code = " + conn.getResponseCode() 
				+ ", status message = [" + conn.getResponseMessage() + "]");
		}
	}

	protected HttpURLConnection openConnection(String url) throws IOException {
		URLConnection conn = new URL(url).openConnection();
		if (!(conn instanceof HttpURLConnection)) {
			throw new IOException("Service URL [" + url + "] is not an HTTP URL");
		}
		return (HttpURLConnection) conn;
	}

	protected void prepareConnection(HttpURLConnection conn, int contentLength) throws IOException {
		conn.setDoOutput(true);
		conn.setRequestMethod(HTTP_METHOD_POST);
		conn.setRequestProperty(HTTP_HEADER_CONTENT_TYPE, contentType);
		conn.setRequestProperty(HTTP_HEADER_CONTENT_LENGTH, Integer.toString(contentLength));

		if (acceptGzipEncoding) {
			conn.setRequestProperty(HTTP_HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
		}
	}

	protected boolean isGzipResponse(HttpURLConnection con) {
		String encodingHeader = con.getHeaderField(HTTP_HEADER_CONTENT_ENCODING);
		return (encodingHeader != null && encodingHeader.toLowerCase().indexOf(ENCODING_GZIP) != -1);
	}

	protected RemoteInvocationResult doReadRemoteInvocationResult(ObjectInputStream ois) throws IOException, ClassNotFoundException {

		Object obj = ois.readObject();
		if (!(obj instanceof RemoteInvocationResult)) {
			throw new RemoteException("Deserialized object needs to be assignable to type [" + RemoteInvocationResult.class.getName() + "]: " + obj);
		}
		return (RemoteInvocationResult) obj;
	}

	protected ByteArrayOutputStream getByteArrayOutputStream(RemoteInvocation invocation) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(SERIALIZED_INVOCATION_BYTE_ARRAY_INITIAL_SIZE);
		writeRemoteInvocation(invocation, baos);
		return baos;
	}

	protected void writeRemoteInvocation(RemoteInvocation invocation, OutputStream os) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(os);
		try {
			oos.writeObject(invocation);
			oos.flush();
		} finally {
			oos.close();
		}
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean isAcceptGzipEncoding() {
		return acceptGzipEncoding;
	}

	public void setAcceptGzipEncoding(boolean acceptGzipEncoding) {
		this.acceptGzipEncoding = acceptGzipEncoding;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public String getCodebaseUrl() {
		return codebaseUrl;
	}

	public void setCodebaseUrl(String codebaseUrl) {
		this.codebaseUrl = codebaseUrl;
	}
	
}
