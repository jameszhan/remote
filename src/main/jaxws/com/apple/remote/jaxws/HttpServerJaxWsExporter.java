package com.apple.remote.jaxws;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

public class HttpServerJaxWsExporter extends JaxWsServiceExporter {

	private HttpServer server;
	private int port = 8080;
	private String hostname;
	private int backlog = -1;
	private int shutdownDelay = 0;
	private String basePath = "/";
	private List<Filter> filters;
	private Authenticator authenticator;

	protected void publishEndpoint(Endpoint endpoint, WebService annotation) {
		String fullPath = this.basePath + annotation.serviceName();
		HttpContext httpContext = this.server.createContext(fullPath);
		if (this.filters != null) {
			httpContext.getFilters().addAll(this.filters);
		}
		if (this.authenticator != null) {
			httpContext.setAuthenticator(this.authenticator);
		}
		endpoint.publish(httpContext);
	}

	
	public void export() throws Exception{
		startHttpServer();
		super.export();
	}
	
	public void stop() {
		super.stop();
		this.server.stop(this.shutdownDelay);
	}

	private void startHttpServer() throws IOException{
		if (this.server == null) {
			InetSocketAddress address = (this.hostname != null ?
					new InetSocketAddress(this.hostname, this.port) : new InetSocketAddress(this.port));
			this.server = HttpServer.create(address, this.backlog);
			if (logger.isInfoEnabled()) {
				logger.info("Starting HttpServer at address " + address);
			}
			this.server.start();			
		}
	}


	public HttpServer getServer() {
		return server;
	}

	public void setServer(HttpServer server) {
		this.server = server;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getBacklog() {
		return backlog;
	}

	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}

	public int getShutdownDelay() {
		return shutdownDelay;
	}

	public void setShutdownDelay(int shutdownDelay) {
		this.shutdownDelay = shutdownDelay;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public Authenticator getAuthenticator() {
		return authenticator;
	}

	public void setAuthenticator(Authenticator authenticator) {
		this.authenticator = authenticator;
	}

}
