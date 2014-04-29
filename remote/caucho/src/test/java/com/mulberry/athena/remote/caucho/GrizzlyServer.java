/*
package com.mulberry.athena.remote.caucho;

import com.caucho.hessian.server.HessianServlet;
import com.mulberry.embed.webserver.DebugGrizzlyAdapter;
import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.http.servlet.ServletAdapter;
import com.sun.grizzly.tcp.http11.GrizzlyAdapter;

import java.io.IOException;


public class GrizzlyServer {

    public static void main(String[] args) throws IOException {
        GrizzlyWebServer ws = new GrizzlyWebServer(8080, "./site");

        GrizzlyAdapter debugAdapter = new DebugGrizzlyAdapter();
        ws.addGrizzlyAdapter(debugAdapter, new String[] { "/debug", "*.dbg" });

        ServletAdapter sa = new ServletAdapter();
        HessianServlet hs = new HessianServlet();
        sa.addInitParameter("home-class", "com.mulberry.athena.remote.HelloServiceImpl");
        sa.addInitParameter("home-api", "com.mulberry.athena.remote.HelloService");
        sa.setServletInstance(hs);
        ws.addGrizzlyAdapter(sa, new String[] { "*.hs", "/hessian" });

        ws.start();
    }

}

*/