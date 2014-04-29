
package com.mulberry.athena.remote.jaxrs.demo;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import org.glassfish.grizzly.http.server.HttpServer;

import java.io.IOException;

public class WebServer {

    public static void main(String[] args) throws IOException {
        ResourceConfig rc = new PackagesResourceConfig("com.mulberry.athena.remote.jaxws");
        HttpServer httpServer = GrizzlyServerFactory.createHttpServer("http://localhost:8080", rc);

        httpServer.start();
        try {
            System.out.println("Press any key to stop the server...");
            System.in.read();
        } finally {
            httpServer.shutdown();
        }
    }
}
