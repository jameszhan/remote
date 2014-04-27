/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.jaxws;

import com.mulberry.embed.webserver.DebugGrizzlyAdapter;
import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.http.servlet.ServletAdapter;
import com.sun.grizzly.tcp.http11.GrizzlyAdapter;
import com.sun.xml.ws.transport.http.servlet.WSServlet;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 10:05 PM
 */
public class GrizzlyServer {

    public static void main(String[] args) throws IOException {

        GrizzlyWebServer ws = new GrizzlyWebServer(8080, "./site");

        GrizzlyAdapter debugAdapter = new DebugGrizzlyAdapter();
        ws.addGrizzlyAdapter(debugAdapter, new String[] { "/debug", "*.dbg" });

        ServletAdapter wsa = new WSServletAdapter("./war");
        wsa.addServletListener("com.sun.xml.ws.transport.http.servlet.WSServletContextListener");
        wsa.setServletInstance(new WSServlet());
        ws.addGrizzlyAdapter(wsa, new String[]{"/ws"});

        ws.start();
    }
}
