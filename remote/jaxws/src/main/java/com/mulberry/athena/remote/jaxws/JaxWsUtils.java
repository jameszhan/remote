/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.remote.jaxws;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 11:45 PM
 */
public final class JaxWsUtils {
    private JaxWsUtils(){}

    public static Service createJaxWsService(URL wsdlDocumentUrl, QName serviceName){
        return (wsdlDocumentUrl != null ?  Service.create(wsdlDocumentUrl, serviceName)
                : Service.create(serviceName));
    }

    public static <T> T createJaxWsPort(URL wsdlDocumentUrl, QName serviceName, QName portName, Class<T> ifc){
        Service svc = createJaxWsService(wsdlDocumentUrl, serviceName);
        return svc.getPort(portName, ifc);
    }

    public static Dispatch<Source> createDispatch(URL wsdlDocumentUrl, QName serviceName, QName portName){
        Service svc = createJaxWsService(wsdlDocumentUrl, serviceName);
        return svc.createDispatch(portName, Source.class, Service.Mode.PAYLOAD);
    }

}
