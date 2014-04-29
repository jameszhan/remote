package com.mulberry.athena.remote.jaxrs.demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpServer;

public class SimpleServer {


    public static void main(String[] args) throws IOException {
        final Map<String, Object> initParams = new HashMap<String, Object>();
        initParams.put("com.sun.jersey.config.property.packages", "com.sun.jersey.samples.helloworld.resources");
        ResourceConfig rc = new DefaultResourceConfig();
        rc.setPropertiesAndFeatures(initParams);
        HttpServer server = HttpServerFactory.create("http://localhost:8888", rc);
    }
}
