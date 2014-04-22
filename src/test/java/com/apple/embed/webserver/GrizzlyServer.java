package com.apple.embed.webserver;

import java.io.IOException;

import com.apple.remote.httpinvoker.HttpInvokerServlet;
import com.apple.remote.rmi.RmiHelloServiceServlet;
import com.caucho.hessian.server.HessianServlet;
import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.http.servlet.ServletAdapter;
import com.sun.grizzly.tcp.http11.GrizzlyAdapter;
import com.sun.xml.ws.transport.http.servlet.WSServlet;


public class GrizzlyServer {

	public static void main(String[] args) throws IOException {

		GrizzlyWebServer ws = new GrizzlyWebServer(8080, "./war");
		
		GrizzlyAdapter debugAdapter = new DebugGrizzlyAdapter();
		ws.addGrizzlyAdapter(debugAdapter, new String[] { "/debug", "*.dbg" });
		
		ServletAdapter wsa = new WSServletAdapter("./war");			
		wsa.addServletListener("com.sun.xml.ws.transport.http.servlet.WSServletContextListener");		
		wsa.setServletInstance(new WSServlet());		
		ws.addGrizzlyAdapter(wsa, new String[]{"/ws"});

		ServletAdapter sa = new ServletAdapter();
		HessianServlet hs = new HessianServlet();
		sa.addInitParameter("home-class", "com.apple.remote.HelloServiceImp");
		sa.addInitParameter("home-api", "com.apple.remote.HelloService");
		sa.setServletInstance(hs);
		ws.addGrizzlyAdapter(sa, new String[] { "*.hs", "/hessian" });
	
		ServletAdapter hisa = new ServletAdapter(new HttpInvokerServlet());
		hisa.addInitParameter("service-class", "com.apple.remote.HelloServiceImp");
		ws.addGrizzlyAdapter(hisa, new String[] { "*.httpinvoker", "/httpinvoker/*" });
		
		ServletAdapter rmisa = new ServletAdapter(new RmiHelloServiceServlet());
		ws.addGrizzlyAdapter(rmisa, new String[]{"/rmi/*"});
		

		ws.start();

	}

}
