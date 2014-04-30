package com.mulberry.athena.remote.jaxrs.demo;

import com.mulberry.athena.remote.jaxrs.demo.resources.Default;

import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

public class SimpleServer {

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost").port(8086).path("/").build();
    }


    public static void main(String[] args) throws IOException {
        ResourceConfig rc = new ResourceConfig(Default.class);
        HttpServer server = JdkHttpServerFactory.createHttpServer(getBaseURI(), rc);

        System.in.read();

        server.stop(0);
    }
}
