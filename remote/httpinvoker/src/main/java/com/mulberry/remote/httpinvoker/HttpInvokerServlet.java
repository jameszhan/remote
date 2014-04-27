/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.httpinvoker;

import com.mulberry.toolkit.reflect.Reflections;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 6:30 PM
 */
public class HttpInvokerServlet extends HttpServlet {

    private static final long serialVersionUID = 4096020903260070242L;

    private static final String SERVICE_CLASS_NAME = "service-class";
    private static final String REQUEST_HANDLER_CLASS = "request-handler-class";

    private HttpRequestHandler handler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try{
            String requestHandlerClass = config.getInitParameter(REQUEST_HANDLER_CLASS);
            if (requestHandlerClass != null) {
                Class<?> requestHandlerClazz = Reflections.classForName(requestHandlerClass);
                handler = (HttpRequestHandler) requestHandlerClazz.newInstance();
            } else {
                handler = new HttpInvokerRequestHandler();
            }

            String serviceClass = config.getInitParameter(SERVICE_CLASS_NAME);
            Class<?> clazz = Reflections.classForName(serviceClass);
            handler.setServiceInstance(clazz.newInstance());
        }catch(Exception e){
            throw new ServletException(e);
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            handler.handle(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

}