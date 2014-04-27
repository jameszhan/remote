/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 6:28 AM
 */
public class RemoteInvocationResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Object value;
    private Throwable exception;

    public RemoteInvocationResult(Object value) {
        this.value = value;
    }

    public RemoteInvocationResult(Throwable exception) {
        this.exception = exception;
    }

    public boolean hasException() {
        return (this.exception != null);
    }

    public boolean hasInvocationTargetException() {
        return (this.exception instanceof InvocationTargetException);
    }

    public Object getValue() {
        if (exception != null) {
            throw new IllegalStateException("There is some exception while remote invocation.", exception);
        }
        return value;
    }
}
