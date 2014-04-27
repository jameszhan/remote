/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.httpinvoker;

import com.mulberry.remote.HelloService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 6:33 PM
 */
public class RmiRequestHandler implements HttpRequestHandler {

    private Object serviceInstance;

    @Override
    public void setServiceInstance(Object serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(request.getInputStream());
        try{
            Object o = ois.readObject();
            String result;
            if (o instanceof String && serviceInstance instanceof HelloService) {
                result = ((HelloService)serviceInstance).echo((String) o);
            } else {
                result = "not support request type: " + o.getClass().getCanonicalName();
            }
            ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
            oos.writeObject(result);
        } catch (Exception ex){
            throw new IOException(ex);
        }
    }

}
