/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.jaxws;

import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 11:46 PM
 */
public final class JaxWsClient {
    private JaxWsClient(){}

    public static <T> T getProxy(URL wsdlDocumentUrl, String namespaceUri, String serviceName, String portName, Class<T> ifc){
        JaxWsPortStubProxy<T> stub = new JaxWsPortStubProxy<T>();
        stub.setServiceName(serviceName);
        stub.setWsdlDocumentUrl(wsdlDocumentUrl);
        stub.setNamespaceUri(namespaceUri);
        stub.setPortName(portName);
        stub.setServiceInterface(ifc);
        return stub.createServicePortStub();
    }
}
