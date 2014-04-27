/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.httpinvoker;

import com.mulberry.toolkit.reflect.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 8:25 PM
 */
public class MultiServiceHttpInvokerServlet extends HttpServlet {

    private static final long serialVersionUID = 4096020903260070242L;

    private static final String SERVICE_CLASS_NAME = "service.";
    private static final Map<String, HttpRequestHandler> handlerMap = new ConcurrentHashMap<String, HttpRequestHandler>();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            Enumeration<?> e = config.getInitParameterNames();
            while (e.hasMoreElements()) {
                String key = e.nextElement().toString();
                if (key.startsWith(SERVICE_CLASS_NAME)) {
                    String serviceClass = config.getInitParameter(key);
                    Class<?> clazz = Reflections.classForName(serviceClass);
                    if (clazz != null) {
                        HttpInvokerRequestHandler handler = new HttpInvokerRequestHandler();
                        handler.setServiceInstance(clazz.newInstance());
                        handlerMap.put(getServiceName(key), handler);
                    }else{
                        logger.warn("Can't load class " + serviceClass);
                    }
                }else{
                    logger.debug("Ingore init parameter: " + key);
                }
            }
            if(logger.isDebugEnabled()){
                for(String serviceName : handlerMap.keySet()){
                    logger.debug("Bind service: " + serviceName);
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private String getServiceName(String key) {
        return "/" + key.substring(SERVICE_CLASS_NAME.length());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        try {
            if (DefaultSerDes.CONTENT_TYPE_SERIALIZED_OBJECT.equals(request.getContentType())) {
                String serviceName = request.getServletPath();

                HttpRequestHandler handler = handlerMap.get(serviceName);
                if (handler != null) {
                    handler.handle(request, response);
                } else {
                    response.sendError(404, "Can't found service: " + serviceName);
                }
            } else {
                response.sendError(500, "HttpInvoker only support content type: "
                        + DefaultSerDes.CONTENT_TYPE_SERIALIZED_OBJECT);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}