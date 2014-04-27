/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import com.mulberry.remote.toolkit.reflect.Reflections;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 6:29 AM
 */
public class RemoteProxyFactory implements InvocationHandler {

    private final RemoteInvocationHandler stub;
    private final Class<?>[] proxiedInterfaces;
    private Object proxy;


    public RemoteProxyFactory(RemoteInvocationHandler stub, Class<?>... proxiedInterfaces) {
        this.stub = stub;
        this.proxiedInterfaces = proxiedInterfaces;
    }

    public Object getProxy(){
        proxy = Proxy.newProxyInstance(Reflections.getContextClassLoader(), proxiedInterfaces, this);
        return proxy;
    }

    public Object getProxy(ClassLoader classloader){
        proxy = Proxy.newProxyInstance(classloader, proxiedInterfaces, this);
        return proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RemoteInvocation invocation = new RemoteInvocation(method.getName(), method.getParameterTypes(), args);
        return stub.invoke(invocation);
    }

}