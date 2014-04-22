package com.apple.reflect.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;

import org.apache.log4j.Logger;

import com.apple.common.util.Closing;
import com.apple.common.util.FileScanner;
import com.apple.common.util.JarFileScanner;
import com.apple.common.util.ScannerListener;

public class PackagesScanner {

	private Logger logger = Logger.getLogger(this.getClass());
	private final ClassLoader classloader;
	private final String[] packages;

	public PackagesScanner(String... packages) {
		this(ReflectionHelper.getContextClassLoader(), packages);
	}

	public PackagesScanner(ClassLoader classloader, String... packages) {
		this.classloader = classloader;
		this.packages = packages;
	}

	public void scan(ScannerListener sl) {		
		for (String p : packages) {
			try {
				Enumeration<URL> urls = classloader.getResources(p.replace(".", "/"));
				if (urls != null) {
					while (urls.hasMoreElements()) {
						URL url = urls.nextElement();
						scan(toURI(url), sl);
					}
				}
			} catch (Exception e) {
				logger.warn("Can't find resource for " + p, e);
			} 
		}
		
	}

	private void scan(URI uri, final ScannerListener sl) {
		String scheme = uri.getScheme();
		if("file".equalsIgnoreCase(scheme)){
			FileScanner.scan(new File(uri.getPath()), sl);
		}else if("zip".equalsIgnoreCase(scheme) || "jar".equalsIgnoreCase(scheme)){
			final String ssp = uri.getRawSchemeSpecificPart();
	        final String jarUrlString = ssp.substring(0, ssp.lastIndexOf('!'));
	        final String parent = ssp.substring(ssp.lastIndexOf('!') + 2);
	        try {
	            closing(jarUrlString).f(new Closing.Closure() {
	                public void f(final InputStream in) throws IOException {
	                    JarFileScanner.scan(in, parent, sl);
	                }
	            });
	        } catch (IOException ex) {
	            throw new RuntimeException("IO error when scanning jar " + uri, ex);
	        }
		}		
	}
	
	protected Closing closing(String jarUrlString) throws IOException {
		return new Closing(new URL(jarUrlString).openStream());
	}

	private URI toURI(URL url) throws URISyntaxException {
		try {
			return url.toURI();
		} catch (URISyntaxException e) {			
			return URI.create(toExternalForm(url));
		}
	}

	private String toExternalForm(URL u) {
		StringBuffer result = new StringBuffer();
		result.append(u.getProtocol());
		result.append(":");
		if (u.getAuthority() != null && u.getAuthority().length() > 0) {
			result.append("//");
			result.append(u.getAuthority());
		}
		if (u.getPath() != null) {
			result.append(u.getPath());
		}
		if (u.getQuery() != null) {
			result.append('?');
			result.append(u.getQuery());
		}
		if (u.getRef() != null) {
			result.append("#");
			result.append(u.getRef());
		}
		return result.toString();
	}

}
