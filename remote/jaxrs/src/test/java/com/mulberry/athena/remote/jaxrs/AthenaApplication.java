package com.mulberry.athena.remote.jaxrs;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import java.util.logging.Logger;

public class AthenaApplication extends ResourceConfig {

    private static final Logger LOGGER = Logger.getLogger("AthenaApplication");

    public AthenaApplication() {
        // Register resources and providers using package-scanning.
        packages("com.mulberry.athena.remote.jaxrs.demo");

        // Register my custom provider - not needed if it's in com.mulberry.athena.remote.jaxrs.demo.
        //register(SecurityRequestFilter.class);

        // Register an instance of LoggingFilter.
        register(new LoggingFilter(LOGGER, true));

        // Enable Tracing support.
        property(ServerProperties.TRACING, "ALL");
    }
}
