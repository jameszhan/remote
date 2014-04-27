/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 9:03 AM
 */
public class HelloServiceImpl implements HelloService {

    @Override public String sayHello(String message) {
        return "Hello " + message;
    }

    @Override public String echo(String message) {
        return "HelloService echo: " + message;
    }

}
