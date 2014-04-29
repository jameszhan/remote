/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.remote.httpinvoker;

import com.mulberry.athena.remote.HelloService;
import com.mulberry.athena.toolkit.http.HttpConstants;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 6:49 PM
 */
public class HttpInvokerTests {

    private final static int PORT = 8765;
    private static HttpServer webServer = HttpServer.createSimpleServer(".", PORT);

    @BeforeClass
    public static void setUpWebServer() throws Exception {
        WebappContext webappContext = new WebappContext("httpInvoker");

        ServletRegistration commonRegistration = webappContext.addServlet("common", new HttpInvokerServlet());
        commonRegistration.setInitParameter("service-class", "com.mulberry.athena.remote.HelloServiceImpl");
        commonRegistration.setInitParameter("load-on-startup", "1");
        commonRegistration.addMapping("*.httpinvoker", "/httpinvoker/*");

        ServletRegistration rmiRegistration = webappContext.addServlet("rmi", new HttpInvokerServlet());
        rmiRegistration.setInitParameter("load-on-startup", "1");
        rmiRegistration.setInitParameter("service-class", "com.mulberry.athena.remote.HelloServiceImpl");
        rmiRegistration.setInitParameter("request-handler-class", "com.mulberry.athena.remote.httpinvoker.RmiRequestHandler");
        rmiRegistration.addMapping("/rmi/*");
        webappContext.deploy(webServer);

        webServer.start();
    }

    @AfterClass
    public static void closeWebServer() throws Exception {
        webServer.shutdown();
    }

    @Test
    public void httpInvoker() throws Exception {
        HelloService helloService = HttpInvokerClient.getProxy("http://localhost:8765/httpinvoker", HelloService.class);
        Assert.assertEquals(helloService.echo("Good"), "HelloService echo: Good");
    }

    @Test
    public void rmi() throws Exception {
        String serviceUrl = "http://localhost:8765/rmi";
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject("RMI");
        oos.flush();
        oos.close();

        HttpURLConnection conn = openConnection(serviceUrl);
        prepareConnection(conn, os.size());

        os.writeTo(conn.getOutputStream());

        InputStream in = conn.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(in);

        Assert.assertEquals(ois.readObject(), "HelloService echo: RMI");
    }


    protected static HttpURLConnection openConnection(String url) throws IOException {
        URLConnection conn = new URL(url).openConnection();
        if (!(conn instanceof HttpURLConnection)) {
            throw new IOException("Service URL [" + url + "] is not an HTTP URL");
        }
        return (HttpURLConnection) conn;
    }

    protected static void prepareConnection(HttpURLConnection conn, int contentLength) throws IOException {
        conn.setDoOutput(true);
        conn.setRequestMethod(HttpConstants.HTTP_METHOD_POST);
        conn.setRequestProperty(HttpConstants.HTTP_HEADER_CONTENT_TYPE, "text/xml");
        conn.setRequestProperty(HttpConstants.HTTP_HEADER_CONTENT_LENGTH, Integer.toString(contentLength));
    }
}
