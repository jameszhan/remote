package com.google.jersey.server;

import java.io.IOException;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

public class RestfulService {

	public static void main(String[] args) throws IllegalArgumentException, IOException {
		HttpServer server = HttpServerFactory.create("http://localhost:8888");
		server.start();
	}

}
