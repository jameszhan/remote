/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 6:29 AM
 */
public interface RemoteInvocationHandler {

    Object invoke(RemoteInvocation invocation) throws RemoteException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException;

}
