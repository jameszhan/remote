/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.rmi;

import com.google.common.io.Closer;
import com.mulberry.remote.HelloService;
import com.mulberry.remote.HelloServiceImpl;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 9:05 AM
 */
public class RmiInvocationTests {

    @BeforeClass
    public static void bindService() throws RemoteException{
        RmiServiceRegistry.rebind("String", "Hello World!");
        RmiServiceRegistry.rebind("Hello", new HelloServiceImpl());
        RmiServiceRegistry.rebind("StandardRemote", new StandardRemoteImpl());

    }

    @AfterClass
    public static void unbindService() throws NotBoundException, RemoteException {
        RmiServiceRegistry.unbind("String");
        RmiServiceRegistry.unbind("Hello");
    }

    @Test
    public void testStringService() throws RemoteException, NotBoundException {
        CharSequence cs = RmiServiceRegistry.lookup("String", CharSequence.class);
        Assert.assertEquals(cs.toString(), "Hello World!");
        Assert.assertEquals(cs.length(), 12);
    }

    @Test
    public void testHelloService() throws RemoteException, NotBoundException {
        HelloService hs = RmiServiceRegistry.lookup("Hello", HelloService.class);
        String echo = hs.echo("Hello World!");
        Assert.assertEquals(echo, "HelloService echo: Hello World!");
        Assert.assertEquals(hs.sayHello("James"), "Hello James");
    }

    @Test
    public void testStandardRemote() throws RemoteException, NotBoundException {
        StandardRemote sr = RmiServiceRegistry.lookup("StandardRemote", StandardRemote.class);
        String echo = sr.echo("Hello World");
        Assert.assertEquals(echo, "Standard Remote Service Echo: Hello World.");
        Assert.assertEquals(sr.callRemoteService(), "Call Standard Remote Service.");
    }

    @Test
    public void httpClient() throws IOException, ClassNotFoundException {
        String serviceUrl = "http://localhost:1099/Hello";
        HttpURLConnection conn = openConnection(serviceUrl);

        Closer closer = Closer.create();
        try {
            ByteArrayOutputStream os = closer.register(new ByteArrayOutputStream());
            ObjectOutputStream oos = closer.register(new ObjectOutputStream(os));
            oos.writeObject("Hello World!");
            oos.flush();
            prepareConnection(conn, os.size());
            os.writeTo(conn.getOutputStream());
        } finally {
            closer.close();
        }

        closer = Closer.create();
        InputStream in = closer.register(conn.getInputStream());
        try {
            ObjectInputStream ois = new ObjectInputStream(in);
            System.out.println(ois.readObject());
        } finally {
            closer.close();
        }
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
        conn.setRequestMethod(HTTP_METHOD_POST);
        conn.setRequestProperty(HTTP_HEADER_CONTENT_TYPE, "text/xml");
        conn.setRequestProperty(HTTP_HEADER_CONTENT_LENGTH, Integer.toString(contentLength));
    }

    protected static final String HTTP_METHOD_POST = "POST";
    protected static final String HTTP_HEADER_ACCEPT_LANGUAGE = "Accept-Language";
    protected static final String HTTP_HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    protected static final String HTTP_HEADER_CONTENT_ENCODING = "Content-Encoding";
    protected static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
    protected static final String HTTP_HEADER_CONTENT_LENGTH = "Content-Length";
}
