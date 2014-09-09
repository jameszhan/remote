package com.google.restful.config;

import java.io.File;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.restful.provider.entity.ListProcessor;
//import com.sun.jersey.server.impl.container.config.AnnotatedClassScanner;

public class ResourceHandler {
	
	public static void main(String[] args) {
		
		Pattern ap = Pattern.compile("abc");
		ap.matcher(null);
		
		 String classPath = System.getProperty("java.class.path");
	     String[] paths = classPath.split(File.pathSeparator);
	     
	     System.out.println(File.pathSeparator);
	     for(String p : paths){
	    	 System.out.println(p);
	     }
	     
	     System.out.println(true | Boolean.FALSE);
/*
	     AnnotatedClassScanner scanner = new AnnotatedClassScanner(ListProcessor.class);
	     
	     Set<Class<?>> set = scanner.scan(new String[]{""});
	     for(Class<?> c : set){
	    	 System.out.println(c);
	     }
*/
	}

}
