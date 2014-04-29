/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.remote.httpinvoker;

import com.mulberry.athena.remote.RemoteInvocationHandler;
import com.mulberry.athena.remote.RemoteProxyFactory;
import com.mulberry.athena.remote.ServiceInterfacesAware;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 6:40 PM
 */
public class HttpInvokerClient {

    public static <T> T getProxy(String serviceUrl, Class<T> ifc) throws Exception{
        return getProxy(new HttpInvokerStub(serviceUrl), ifc);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getProxy(RemoteInvocationHandler stub, Class<T> ifc) throws Exception {
        Set<Class<?>> ifcs = new HashSet<Class<?>>();
        if(ifc != null){
            ifcs.add(ifc);
        }
        if(stub instanceof ServiceInterfacesAware){
            ifcs.addAll(((ServiceInterfacesAware) stub).getInterfaces());
        }
        return (T) new RemoteProxyFactory(stub, ifcs.toArray(new Class<?>[ifcs.size()])).getProxy();
    }
}
