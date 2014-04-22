package com.apple.remote.jaxws;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import org.apache.log4j.Logger;

import com.apple.reflect.util.AnnotationScannerListener;
import com.apple.reflect.util.PackagesScanner;

public class JaxWsServiceExporter {	
	
	public static final String DEFAULT_BASE_ADDRESS = "http://localhost:8080/";
	
	protected Logger logger = Logger.getLogger(this.getClass());
	private String baseAddress = DEFAULT_BASE_ADDRESS;
	
	private Executor executor = Executors.newFixedThreadPool(20);
	private final Set<Endpoint> publishedEndpoints = new LinkedHashSet<Endpoint>();
	
	public void export() throws Exception{
		Set<Class<?>> classes = getWebServices();
		for(Class<?> clazz : classes){
			if(!clazz.isInterface()){
				Endpoint endpoint = Endpoint.create(clazz.newInstance());
				endpoint.setExecutor(executor);
				publishEndpoint(endpoint, clazz.getAnnotation(WebService.class));
				publishedEndpoints.add(endpoint);
			}
		}
	}
	
	public void stop() {
		for (Endpoint endpoint : publishedEndpoints) {
			endpoint.stop();
		}
	}
	
	protected void publishEndpoint(Endpoint endpoint, WebService annotation) {
		String fullAddress = this.baseAddress + annotation.serviceName();
		endpoint.publish(fullAddress);
	}
	
	private Set<Class<?>> getWebServices(){		
		PackagesScanner scanner = new PackagesScanner("com.apple");
		@SuppressWarnings("unchecked")
		AnnotationScannerListener asl = new AnnotationScannerListener(WebService.class);
		scanner.scan(asl);
		return asl.getAnnotatedClasses();
	}
	

}
