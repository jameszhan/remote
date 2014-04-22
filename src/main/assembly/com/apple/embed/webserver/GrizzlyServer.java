package com.apple.embed.webserver;

import java.io.IOException;

import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.http.servlet.ServletAdapter;
import com.sun.xml.ws.transport.http.servlet.WSServlet;


public class GrizzlyServer {

	public static void main(String[] args) throws IOException {

		GrizzlyWebServer ws = new GrizzlyWebServer(8080, "./war");		
			
		ServletAdapter wsa = new WSServletAdapter("./war");			
		wsa.addServletListener("com.sun.xml.ws.transport.http.servlet.WSServletContextListener");		
		wsa.setServletInstance(new WSServlet());		
		ws.addGrizzlyAdapter(wsa, new String[]{"/ws"});


		ws.start();

	}

}
