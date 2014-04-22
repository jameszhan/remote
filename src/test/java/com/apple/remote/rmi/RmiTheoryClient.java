package com.apple.remote.rmi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class RmiTheoryClient {
	
	protected static final String HTTP_METHOD_POST = "POST";
	protected static final String HTTP_HEADER_ACCEPT_LANGUAGE = "Accept-Language";
	protected static final String HTTP_HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	protected static final String HTTP_HEADER_CONTENT_ENCODING = "Content-Encoding";
	protected static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	protected static final String HTTP_HEADER_CONTENT_LENGTH = "Content-Length";
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String serviceUrl = "http://localhost:8080/rmi";
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject("Hello World!");
		oos.flush();
		oos.close();
		
		HttpURLConnection conn = openConnection(serviceUrl);
		prepareConnection(conn, os.size());
		
		os.writeTo(conn.getOutputStream());
		
		InputStream in = conn.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(in);
		System.out.println(ois.readObject());
		
	}	

	protected static HttpURLConnection openConnection(String url) throws IOException {
		URLConnection conn = new URL(url).openConnection();
		if (!(conn instanceof HttpURLConnection)) {
			throw new IOException("Service URL [" + url + "] is not an HTTP URL");
		}
		return (HttpURLConnection) conn;
	}

	protected static void prepareConnection(HttpURLConnection conn, int contentLength) throws IOException {
		conn.setDoOutput(true);
		conn.setRequestMethod(HTTP_METHOD_POST);
		conn.setRequestProperty(HTTP_HEADER_CONTENT_TYPE, "text/xml");
		conn.setRequestProperty(HTTP_HEADER_CONTENT_LENGTH, Integer.toString(contentLength));
	}
}
