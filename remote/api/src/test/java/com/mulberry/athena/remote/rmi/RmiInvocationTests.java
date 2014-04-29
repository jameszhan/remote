/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.remote.rmi;

import com.mulberry.athena.remote.HelloService;
import com.mulberry.athena.remote.HelloServiceImpl;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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
        RmiServiceRegistry.rebind("HelloService", new HelloServiceImpl());
        RmiServiceRegistry.rebind("StandardRemote", new StandardRemoteImpl());
    }

    @AfterClass
    public static void unbindService() throws NotBoundException, RemoteException {
        RmiServiceRegistry.unbind("String");
        RmiServiceRegistry.unbind("HelloService");
        RmiServiceRegistry.unbind("StandardRemote");
    }

    @Test
    public void testStringService() throws Exception {
        CharSequence cs = RmiServiceRegistry.lookup("String", CharSequence.class);
        Assert.assertEquals(cs.toString(), "Hello World!");
        Assert.assertEquals(cs.length(), 12);
    }

    @Test
    public void testHelloService() throws Exception {
        HelloService hs = RmiServiceRegistry.lookup("rmi://localhost:1099/HelloService", HelloService.class);
        String echo = hs.echo("Hello World!");
        Assert.assertEquals(echo, "HelloService echo: Hello World!");
        Assert.assertEquals(hs.sayHello("James"), "Hello James");
    }

    @Test
    public void testStandardRemote() throws Exception {
        StandardRemote sr = RmiServiceRegistry.lookup("StandardRemote", StandardRemote.class);
        String echo = sr.echo("Hello World");
        Assert.assertEquals(echo, "Standard Remote Service Echo: Hello World.");
        Assert.assertEquals(sr.callRemoteService(), "Call Standard Remote Service.");
    }

}
