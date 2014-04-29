/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.remote.jaxws;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 11:36 PM
 */
public class HttpServerWsPublisher extends JaxWsServicePublisher {

    private HttpServer server;
    private List<Filter> filters;
    private Authenticator authenticator;
    private int port = 8080;
    private String hostname;
    private String basePath = "/";
    private int backlog = -1;

    @Override
    public void start() throws Exception {
        super.start();
        startHttpServer();
    }

    @Override
    protected void publishEndpoint(Endpoint endpoint, String serviceName) {
        String fullPath = basePath + serviceName;
        HttpContext httpContext = server.createContext(fullPath);
        if (filters != null) {
            httpContext.getFilters().addAll(filters);
        }
        if (authenticator != null) {
            httpContext.setAuthenticator(authenticator);
        }
        endpoint.publish(httpContext);
    }

    private void startHttpServer() throws IOException {
        if (this.server == null) {
            InetSocketAddress address = (hostname != null ? new InetSocketAddress(hostname, port)
                    : new InetSocketAddress(port));
            server = HttpServer.create(address, backlog);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Starting HttpServer at address " + address);
            }
            server.start();
        }
    }

    public HttpServer getServer() {
        return server;
    }

    public void setServer(HttpServer server) {
        this.server = server;
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
