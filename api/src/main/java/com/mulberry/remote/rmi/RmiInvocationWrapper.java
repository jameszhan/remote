/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.rmi;

import com.mulberry.remote.RemoteInvocation;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

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
    public Class<?>[] getInterfaces() {
        return wrappedObject.getClass().getInterfaces();
    }

    @Override
    public Object invoke(RemoteInvocation invocation) throws RemoteException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invocation.invoke(wrappedObject);
    }

}