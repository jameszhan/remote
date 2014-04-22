package com.apple.remote.jaxws;

import java.net.URL;
import java.util.concurrent.Executor;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.handler.HandlerResolver;

public class LocalJaxWsServiceFactory {

	private URL wsdlDocumentUrl;

	private String namespaceUri;

	private String serviceName;

	private Executor executor;

	private HandlerResolver handlerResolver;

	public void setWsdlDocumentUrl(URL wsdlDocumentUrl) {
		this.wsdlDocumentUrl = wsdlDocumentUrl;
	}

	public URL getWsdlDocumentUrl() {
		return this.wsdlDocumentUrl;
	}

	public void setNamespaceUri(String namespaceUri) {
		this.namespaceUri = (namespaceUri != null ? namespaceUri.trim() : null);
	}

	public String getNamespaceUri() {
		return this.namespaceUri;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceName() {
		return this.serviceName;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	public void setTaskExecutor(Executor executor) {
		this.executor = executor;
	}

	public void setHandlerResolver(HandlerResolver handlerResolver) {
		this.handlerResolver = handlerResolver;
	}

	public Service createJaxWsService() {
		Service service = (this.wsdlDocumentUrl != null ? 
				Service.create(this.wsdlDocumentUrl, getQName(this.serviceName)) : 
				Service.create(getQName(this.serviceName)));
		if (this.executor != null) {
			service.setExecutor(this.executor);
		}
		if (this.handlerResolver != null) {
			service.setHandlerResolver(this.handlerResolver);
		}
		return service;
	}

	protected QName getQName(String name) {
		return (getNamespaceUri() != null ? new QName(getNamespaceUri(), name) : new QName(name));
	}

}
