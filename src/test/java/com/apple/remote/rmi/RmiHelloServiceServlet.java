package com.apple.remote.rmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.apple.remote.HelloService;
import com.apple.remote.HelloServiceImp;

public class RmiHelloServiceServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;	
	
	private HelloService hs;	

	@Override
	public void init(ServletConfig config) throws ServletException {
		hs = new HelloServiceImp();
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ObjectInputStream ois = new ObjectInputStream(request.getInputStream());
		try{
			Object o = ois.readObject();
			String result;
			if(o instanceof String){
				result = hs.echo((String) o);
			}else{
				result = "not support request type: " + o.getClass().getCanonicalName();
			}
			ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
			oos.writeObject(result);
		}catch(Exception ex){
			throw new ServletException(ex);
		}
	}
	
}
