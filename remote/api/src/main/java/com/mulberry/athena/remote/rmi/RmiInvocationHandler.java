/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.remote.rmi;

import com.mulberry.athena.remote.RemoteInvocationHandler;
import com.mulberry.athena.remote.ServiceInterfacesAware;

import java.rmi.Remote;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 6:45 AM
 */
public interface RmiInvocationHandler extends RemoteInvocationHandler, ServiceInterfacesAware, Remote {

}