/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import com.mulberry.toolkit.reflect.Reflections;

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


    public RemoteProxyFactory(RemoteInvocationHandler stub, String... proxiedInterfaceNames) {
        this(stub, getClassesByNames(proxiedInterfaceNames));
    }


    public RemoteProxyFactory(RemoteInvocationHandler stub, Class<?>... proxiedInterfaces) {
        this.stub = stub;
        this.proxiedInterfaces = proxiedInterfaces;
    }

    public Object getProxy(){
        return Proxy.newProxyInstance(Reflections.getContextClassLoader(), proxiedInterfaces, this);
    }


    public Object getProxy(ClassLoader classloader){
        return Proxy.newProxyInstance(classloader, proxiedInterfaces, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RemoteInvocation invocation = new RemoteInvocation(method.getName(), method.getParameterTypes(), args);
        RemoteInvocationResult result = stub.invoke(invocation);
        return result.getValue();
    }

    private static Class<?>[] getClassesByNames(String... classNames){
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for(String className : classNames){
            Class<?> clazz = Reflections.classForName(className);
            if (clazz != null){
                classes.add(clazz);
            }
        }
        return classes.toArray(new Class<?>[classes.size()]);
    }

}