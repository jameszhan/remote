package com.mulberry.athena.web;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DebugServlet extends HttpServlet {

	private static final long serialVersionUID = -1907354474110799439L;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write(toString(req.getInputStream()));
    }

    public static String toString(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte[] buf = new byte[1024];
        int len = in.read(buf);
        while (len >= 0) {
            sb.append(new String(buf, 0, len));
            len = in.read(buf);
        }
        return sb.toString();
    }
}
