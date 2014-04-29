/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.remote.jaxws;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.handler.HandlerResolver;
import java.net.URL;
import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 11:43 PM
 */
public class JaxWsPortStubProxy<T> extends SoapHeader {

    private URL wsdlDocumentUrl;
    private String namespaceUri;
    private String serviceName;

    private Executor executor;
    private HandlerResolver handlerResolver;

    private Class<T> serviceInterface;

    private String portName;

    private T portStub;

    protected T createServicePortStub(){
        if (portStub == null) {
            Service serviceToUse = createService();
            QName portQName = createQName(portName != null ? portName : serviceInterface.getName());
            T stub = (portName != null ? serviceToUse.getPort(portQName, serviceInterface) :
                    serviceToUse.getPort(serviceInterface));
            handleSoapHeader(stub);
            portStub = stub;
        }
        return portStub;
    }

    protected Service createService() {
        Service service = JaxWsUtils.createJaxWsService(wsdlDocumentUrl, createQName(serviceName));
        if (this.executor != null) {
            service.setExecutor(this.executor);
        }
        if (this.handlerResolver != null) {
            service.setHandlerResolver(this.handlerResolver);
        }
        return service;
    }

    protected QName createQName(String name) {
        return (getNamespaceUri() != null ? new QName(getNamespaceUri(), name) : new QName(name));
    }

    public URL getWsdlDocumentUrl() {
        return wsdlDocumentUrl;
    }

    public void setWsdlDocumentUrl(URL wsdlDocumentUrl) {
        this.wsdlDocumentUrl = wsdlDocumentUrl;
    }

    public String getNamespaceUri() {
        return namespaceUri;
    }

    public void setNamespaceUri(String namespaceUri) {
        this.namespaceUri = namespaceUri;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public HandlerResolver getHandlerResolver() {
        return handlerResolver;
    }

    public void setHandlerResolver(HandlerResolver handlerResolver) {
        this.handlerResolver = handlerResolver;
    }

    public Class<T> getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(Class<T> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

}