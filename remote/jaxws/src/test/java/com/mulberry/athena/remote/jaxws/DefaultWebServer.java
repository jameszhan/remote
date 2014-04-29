/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.remote.jaxws;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/28/14
 *         Time: 1:17 PM
 */
public class DefaultWebServer {

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.createSimpleServer("./remote/jaxws/src/site", 8080);

        WebappContext webapp = new WebappContext("jaxws", "/ws", "./remote/jaxws/src/site");
        ServletRegistration registration = webapp.addServlet("WSServlet", "com.sun.xml.ws.transport.http.servlet.WSServlet");
        registration.setInitParameter("load-on-startup", "1");
        webapp.addListener("com.sun.xml.ws.transport.http.servlet.WSServletContextListener");
        registration.addMapping("/*");
        webapp.deploy(httpServer);

        httpServer.start();

        System.in.read();
        httpServer.shutdown();
    }

}
