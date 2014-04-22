package com.apple.remote.jaxws;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public abstract class JaxWsUtils {
	
	public static Service createService(URL wsdlLocation, QName serviceName){
		return Service.create(wsdlLocation, serviceName);
	}
	
	public static <T> T getPort(Service service, QName portName, Class<T> serviceEndpointInterface){
		return service.getPort(portName, serviceEndpointInterface);
	}
	

}
