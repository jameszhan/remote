/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.remote.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 9:19 AM
 */
public class StandardRemoteImpl extends UnicastRemoteObject implements StandardRemote {

    private static final long serialVersionUID = 1L;

    protected StandardRemoteImpl() throws RemoteException {
        super();
    }

    @Override
    public String callRemoteService() throws RemoteException {
        String str = "Call Standard Remote Service.";
        System.out.println(str);
        return str;
    }

    @Override
    public String echo(String message) throws RemoteException {
        String str = "Standard Remote Service Echo: " + message + ".";
        System.out.println(message);
        return str;
    }

}
