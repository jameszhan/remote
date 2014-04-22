package com.apple.remote.httpinvoker;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.apple.reflect.util.ReflectionHelper;

public class HttpInvokerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;	
	private static final String SERVICE_CLASS_NAME = "service-class";
	
	private HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();

	@Override
	public void init(ServletConfig config) throws ServletException {
		try{
			String serviceClass = config.getInitParameter(SERVICE_CLASS_NAME);
			Class<?> clazz = ReflectionHelper.classForName(serviceClass);			
			exporter.setServiceInstance(clazz.newInstance());		
		}catch(Exception e){
			throw new ServletException(e);
		}
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			exporter.handleRequest(request, response);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}


}
