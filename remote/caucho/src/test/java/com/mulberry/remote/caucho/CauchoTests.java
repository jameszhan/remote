/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.caucho;

import com.caucho.hessian.client.HessianProxyFactory;
import com.mulberry.remote.HelloService;
import junit.framework.Assert;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 10:19 PM
 */
public class CauchoTests {

    private final static int PORT = 8765;
    private static HttpServer httpServer = HttpServer.createSimpleServer(".", PORT);

    @BeforeClass
    public static void setUpWebServer() throws Exception {
        WebappContext webappContext = new WebappContext("httpInvoker");

        ServletRegistration registration = webappContext.addServlet("hessian", "com.caucho.hessian.server.HessianServlet");
        registration.setInitParameter("service-class", "com.mulberry.remote.HelloServiceImpl");
        registration.setInitParameter("load-on-startup", "1");
        registration.addMapping("*.hs", "/hessian");

        webappContext.deploy(httpServer);

        httpServer.start();
    }

    @AfterClass
    public static void closeWebServer() throws Exception {
        httpServer.shutdown();
    }

    @Test
    public void helloService() throws MalformedURLException {
        String url = "http://localhost:8765/hessian";
        HessianProxyFactory factory = new HessianProxyFactory();

        HelloService hs = (HelloService) factory.create(HelloService.class, url);
        Assert.assertEquals(hs.echo("Hello James!"), "HelloService echo: Hello James!");
        Assert.assertEquals(hs.sayHello("James!"), "Hello James!");
    }
}
