/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.remote.rmi;

import com.google.common.collect.Sets;
import com.mulberry.athena.remote.RemoteInvocation;
import com.mulberry.athena.remote.RemoteInvocationResult;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 8:34 AM
 */
public class RmiInvocationWrapper implements RmiInvocationHandler {

    private Object wrappedObject;

    public RmiInvocationWrapper(Object wrappedObject) throws RemoteException {
        this.wrappedObject = wrappedObject;
    }

    @Override
    public Set<Class<?>> getInterfaces() {
        return Sets.newHashSet(wrappedObject.getClass().getInterfaces());
    }

    @Override
    public RemoteInvocationResult invoke(RemoteInvocation invocation) {
        try {
            Object value = invocation.invoke(wrappedObject);
            return new RemoteInvocationResult(value);
        } catch (Throwable t) {
            if(t instanceof InvocationTargetException){
                t = ((InvocationTargetException) t).getTargetException();
            }
            return new RemoteInvocationResult(t);
        }
    }
}