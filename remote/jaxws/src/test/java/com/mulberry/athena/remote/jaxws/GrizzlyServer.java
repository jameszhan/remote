/*
package com.mulberry.athena.remote.jaxws;

import com.mulberry.embed.webserver.DebugGrizzlyAdapter;
import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.http.servlet.ServletAdapter;
import com.sun.grizzly.tcp.http11.GrizzlyAdapter;
import com.sun.xml.ws.transport.http.servlet.WSServlet;

import java.io.IOException;

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
*/