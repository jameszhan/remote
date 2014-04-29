package com.apple.embed.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServer {

	public static void main(String[] args) throws Exception {
		Server server = new Server();

		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(8088);
		server.addConnector(connector);

		WebAppContext webapp = new WebAppContext("Reference/webs/src/main/webapp", "/");

		server.setHandler(webapp);
		server.start();
		server.join();
	}

}
