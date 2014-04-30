
package com.mulberry.athena.remote.jaxrs.demo;

import com.mulberry.athena.remote.jaxrs.AthenaApplication;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

public class WebServer {

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost").port(8086).path("/").build();
    }

    public static void main(String[] args) throws IOException {
        ResourceConfig rc = new AthenaApplication();
        HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(getBaseURI(), rc);

        httpServer.start();
        try {
            System.out.println("Press any key to stop the server...");
            System.in.read();
        } finally {
            httpServer.shutdown();
        }
    }
}
