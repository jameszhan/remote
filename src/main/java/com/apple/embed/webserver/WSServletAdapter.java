package com.apple.embed.webserver;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.Servlet;

import com.sun.grizzly.http.servlet.ServletAdapter;
import com.sun.grizzly.http.servlet.ServletContextImpl;
import com.sun.grizzly.util.ExpandJar;

/**
 * There is a bug in ServletContextImp, for local resource, it will treat it as
 * a ftp resource.
 * 
 * (1) WSServlet - servlet, which redirects calls to (2) WSServletDelegate -
 * implements logic, which binds HTTP transport with WS endpoints (3)
 * WSServletContextListener - Servlet context listener, which is responsible for
 * parsing WS deployment descriptor and generation WS adapters, which represent
 * WS.
 * 
 * @author James.Zhan
 */
public class WSServletAdapter extends ServletAdapter {

	public WSServletAdapter() {
		super();
	}

	public WSServletAdapter(Servlet servlet, ServletContextImpl servletContext) {
		super(servlet, servletContext);

	}

	public WSServletAdapter(Servlet servlet) {
		super(servlet);
	}

	public WSServletAdapter(String publicDirectory) {
		super(publicDirectory);
	}

	@Override
	protected void configureClassLoader(String applicationPath) throws IOException {
		if (classLoader == null) {
			classLoader = createURLClassLoader(applicationPath);
		}
	}

	public static URLClassLoader createURLClassLoader(String dirPath) throws MalformedURLException, IOException {
		String path;
		File file = null;
		URL appRoot = null;
		URL classesURL = null;

		if (!dirPath.endsWith(File.separator)) {
			dirPath += File.separator;
		}

		if (dirPath != null && (dirPath.endsWith(".war") || dirPath.endsWith(".jar"))) {
			file = new File(dirPath);
			appRoot = new URL("jar:file:" + file.getCanonicalPath() + "!/");
			classesURL = new URL("jar:file:" + file.getCanonicalPath() + "!/WEB-INF/classes/");
			path = ExpandJar.expand(appRoot);
		} else {
			path = dirPath;
			classesURL = new URL("file:///" + path + "WEB-INF/classes/");
			appRoot = new URL("file:///" + path);
		}

		String absolutePath = new File(path).getAbsolutePath();
		URL[] urls = null;
		File libFiles = new File(absolutePath + File.separator + "WEB-INF" + File.separator + "lib");
		int arraySize = (appRoot == null ? 1 : 2);

		// Must be a better way because that sucks!
		String separator = (System.getProperty("os.name").toLowerCase().startsWith("win") ? "/" : "//");

		if (libFiles.exists() && libFiles.isDirectory()) {
			urls = new URL[libFiles.listFiles().length + arraySize];
			for (int i = 0; i < libFiles.listFiles().length; i++) {
				urls[i] = new URL("jar:file:" + separator + libFiles.listFiles()[i].toString().replace('\\', '/') + "!/");
			}
		} else {
			urls = new URL[arraySize];
		}

		urls[urls.length - 1] = classesURL;
		urls[urls.length - 2] = appRoot;
		URLClassLoader urlClassloader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
		return urlClassloader;
	}

}
