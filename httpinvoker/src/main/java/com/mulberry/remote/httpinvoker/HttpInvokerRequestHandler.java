/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.httpinvoker;

import com.mulberry.remote.RemoteInvocation;
import com.mulberry.remote.RemoteInvocationResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 6:27 PM
 */

public class HttpInvokerRequestHandler extends HttpInvokerOptions implements HttpRequestHandler{

    private Object serviceInstance;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            RemoteInvocation invocation = readRemoteInvocation(request);
            RemoteInvocationResult result = invokeAndCreateResult(invocation, serviceInstance);
            writeRemoteInvocationResult(request, response, result);
        } catch (ClassNotFoundException ex) {
            throw new IOException("Class not found during deserialization", ex);
        }
    }

    protected RemoteInvocation readRemoteInvocation(HttpServletRequest request) throws IOException, ClassNotFoundException {
        Object obj = readObject(request.getInputStream());
        if (!(obj instanceof RemoteInvocation)) {
            throw new RemoteException("Deserialized object needs to be assignable to type [" + RemoteInvocation.class.getName() + "]: " + obj);
        }
        return (RemoteInvocation) obj;
    }

    protected RemoteInvocationResult invokeAndCreateResult(RemoteInvocation invocation, Object targetObject) {
        try {
            Object value = invocation.invoke(targetObject);
            return new RemoteInvocationResult(value);
        } catch (Throwable ex) {
            if(ex instanceof InvocationTargetException){
                ex = ((InvocationTargetException) ex).getTargetException();
            }
            return new RemoteInvocationResult(ex);
        }
    }

    protected void writeRemoteInvocationResult(HttpServletRequest request, HttpServletResponse response, RemoteInvocationResult result) throws IOException {
        response.setContentType(getContentType());
        ByteArrayOutputStream baos = serialize(result);
        baos.writeTo(response.getOutputStream());
    }

    public Object getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(Object serviceInstance) {
        this.serviceInstance = serviceInstance;
    }


}
