/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.jaxws.demo;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 11:51 PM
 */
@WebService(endpointInterface="com.mulberry.remote.jaxws.demo.Calculator")
public class CalculatorImpl implements Calculator {

    private Map<String, String> map = new HashMap<String, String>();
    {
        map.put("add", "add(int a, int b), sample: add(1, 2) return 3.");
        map.put("sub", "sub(int a, int b), sample: sub(4, 2) return 2.");
        map.put("div", "div(int a, int b), sample: div(8, 2) return 4.");
    }

    @Override
    public int add(int a, int b) {
        return a + b;
    }

    public int sub(int a, int b) {
        return a - b;
    }

    public int div(int a, int b) throws ArithmeticException {
        return a / b;
    }

    public String operationDescription(String operationName) {
        return map.get(operationName);
    }

    public String[] listOperations() {
        List<String> list = new ArrayList<String>();
        for(String s : map.values()){
            list.add(s);
        }
        return list.toArray(new String[list.size()]);
    }

}