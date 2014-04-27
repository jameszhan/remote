/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.caucho;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.server.HessianServlet;
import com.mulberry.remote.HelloService;
import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.http.servlet.ServletAdapter;
import junit.framework.Assert;
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

    private static GrizzlyWebServer ws = new GrizzlyWebServer(8080, "./site");

    @BeforeClass
    public static void setUpWebServer() throws Exception {
        ServletAdapter sa = new ServletAdapter();
        HessianServlet hs = new HessianServlet();
        sa.addInitParameter("home-class", "com.mulberry.remote.HelloServiceImpl");
        sa.addInitParameter("home-api", "com.mulberry.remote.HelloService");
        sa.setServletInstance(hs);
        ws.addGrizzlyAdapter(sa, new String[] { "*.hs", "/hessian" });
        ws.start();
    }

    @AfterClass
    public static void closeWebServer() throws Exception {
        ws.stop();
    }

    @Test
    public void helloService() throws MalformedURLException {
        String url = "http://localhost:8080/hessian";
        HessianProxyFactory factory = new HessianProxyFactory();

        HelloService hs = (HelloService) factory.create(HelloService.class, url);
        Assert.assertEquals(hs.echo("Hello James!"), "HelloService echo: Hello James!");
        Assert.assertEquals(hs.sayHello("James!"), "Hello James!");
    }
}
