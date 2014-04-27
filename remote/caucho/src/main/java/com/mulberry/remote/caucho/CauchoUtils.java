/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.caucho;

import com.caucho.hessian.server.HessianSkeleton;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 10:32 PM
 */
public abstract class CauchoUtils {

    private CauchoUtils() {}

    public HessianSkeleton getSkeleton(Object target, Class<?> ifc){
        return new HessianSkeleton(target, ifc);
    }
}

