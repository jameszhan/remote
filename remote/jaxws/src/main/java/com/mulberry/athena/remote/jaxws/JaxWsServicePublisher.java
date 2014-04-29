/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.remote.jaxws;

import com.google.common.collect.Sets;
import com.mulberry.athena.toolkit.reflect.Reflections;
import com.mulberry.athena.toolkit.scan.AnnotatedScanner;
import com.mulberry.athena.toolkit.scan.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebService;
import javax.xml.transform.Source;
import javax.xml.ws.Endpoint;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 11:16 PM
 */
public class JaxWsServicePublisher {

    protected final static Logger LOGGER = LoggerFactory.getLogger(JaxWsServicePublisher.class);

    private final static String DEFAULT_SERVICE = "DefaultService";
    private final static String DEFAULT_BASE_ADDRESS = "http://localhost:8080/";

    private final Set<Endpoint> publishedEndpoints = new LinkedHashSet<Endpoint>();
    private final List<Object> implementors = new ArrayList<Object>();
    private String baseAddress = DEFAULT_BASE_ADDRESS;
    private Set<String> packages = Sets.newConcurrentHashSet();
    private Executor executor;
    private List<Source> metadata;
    private Map<String, Object> properties;

    public void start() throws Exception {
        Scanner scanner = new AnnotatedScanner(packages, WebService.class);
        for (Class<?> clazz : scanner.scan()) {
            if(!clazz.isInterface()){
                implementors.add(Reflections.instantiate(clazz));
            }
        }
    }

    public void publish() {
        for(Object implementor : implementors){
            publish(implementor);
        }
    }

    public void stop() {
        for (Endpoint endpoint : publishedEndpoints) {
            endpoint.stop();
        }
    }

    protected void publish(Object implementor) {
        WebService annotation = implementor.getClass().getAnnotation(WebService.class);
        if (annotation != null) {
            Endpoint endpoint = Endpoint.create(implementor);
            prepareEndpoint(endpoint);
            publishEndpoint(endpoint, findServiceName(annotation));
            publishedEndpoints.add(endpoint);
        } else {
            throw new IllegalArgumentException("Can't create webservice for implementor " + implementor
                    + ", it is not WebService annotation present.");
        }
    }

    protected void prepareEndpoint(Endpoint endpoint){
        if (executor != null) {
            endpoint.setExecutor(executor);
        }
        if (metadata != null) {
            endpoint.setMetadata(metadata);
        }
        if (properties != null) {
            endpoint.setProperties(properties);
        }
    }

    protected void publishEndpoint(Endpoint endpoint, String serviceName) {
        String fullAddress = this.baseAddress + serviceName;
        endpoint.publish(fullAddress);
    }

    protected String findServiceName(WebService annotation) {
        String serviceName;
        serviceName = annotation.serviceName();
        String endpointInterface = annotation.endpointInterface();
        if ("".equalsIgnoreCase(serviceName) && !"".equalsIgnoreCase(endpointInterface)) {
            Class<?> ifc = Reflections.classForName(endpointInterface);
            if (ifc != null) {
                annotation = ifc.getAnnotation(WebService.class);
                if (annotation != null) {
                    serviceName = annotation.serviceName();
                }
            }
        }

        if (null == serviceName || "".equalsIgnoreCase(serviceName)) {
            serviceName = DEFAULT_SERVICE;
        }
        return serviceName;
    }


    public Executor getExecutor() {
        return executor;
    }

    public String getBaseAddress() {
        return baseAddress;
    }

    public void setBaseAddress(String baseAddress) {
        this.baseAddress = baseAddress;
    }

    public List<Source> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<Source> metadata) {
        this.metadata = metadata;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Set<Endpoint> getPublishedEndpoints() {
        return publishedEndpoints;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public boolean add(Object e) {
        return implementors.add(e);
    }

    public boolean remove(Object o) {
        return implementors.remove(o);
    }

    public boolean addAll(Collection<? extends Object> c) {
        return implementors.addAll(c);
    }

    public void setPackages(Set<String> packages) {
        this.packages = packages;
    }

}
