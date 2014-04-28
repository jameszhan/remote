/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.httpinvoker;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/28/14
 *         Time: 3:52 PM
 */
public class GrizzlyWebServer {

    private final static int PORT = 5678;

    public static void main(String[] args) throws Exception {
        HttpServer webServer = HttpServer.createSimpleServer("./remote/jaxws/src/site", PORT);

        WebappContext webappContext = new WebappContext("httpInvoker", "/");

        ServletRegistration commonRegistration = webappContext.addServlet("common", new HttpInvokerServlet());
        commonRegistration.setInitParameter("service-class", "com.mulberry.remote.HelloServiceImpl");
        commonRegistration.setInitParameter("load-on-startup", "1");
        commonRegistration.addMapping("*.httpinvoker", "/httpinvoker");

        ServletRegistration rmiRegistration = webappContext.addServlet("rmi", new HttpInvokerServlet());
        rmiRegistration.setInitParameter("load-on-startup", "1");
        rmiRegistration.setInitParameter("service-class", "com.mulberry.remote.HelloServiceImpl");
        rmiRegistration.setInitParameter("request-handler-class", "com.mulberry.remote.httpinvoker.RmiRequestHandler");
        rmiRegistration.addMapping("/rmi");
        webappContext.deploy(webServer);

        webServer.start();
        System.in.read();

        webServer.shutdown();
    }
}
