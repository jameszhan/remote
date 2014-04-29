package com.apple.www.debug;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DebugServlet extends HttpServlet {

	private static final long serialVersionUID = -1907354474110799439L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String result = getAsPsfRequest(req.getInputStream());
		resp.getWriter().write(result);
	}

	public static String getAsPsfRequest(InputStream in) throws IOException {
		byte[] buf = new byte[65536];

		int len = in.read(buf);
		String str = new String(buf, 0, len);
		System.out.println(str);
		return str;
	}

}
