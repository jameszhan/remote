/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.toolkit.compile;

import junit.framework.Assert;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 6/18/14
 *         Time: 5:34 PM
 */
public class DynamicCompilerTests {

    public static void main(String[] args) throws Exception {
        DynamicCompiler dynamicCompiler = new DynamicCompiler();
        CompiledResult compiledResult = dynamicCompiler.compile("com.mulberry.athena.World",
                "package com.mulberry.athena;" +
                        "public class World {" +
                        "   public int add(int a, int b){" +
                        "       return a + b;" +
                        "   }" +
                        "}");
        if (compiledResult.isSuccess()) {
            Class<?> clazz = compiledResult.getClazz();
            Method method = clazz.getMethod("add", int.class, int.class);
            Object target = clazz.newInstance();
            Assert.assertEquals(3, method.invoke(target, 1, 2));
        } else {
            System.out.println(compiledResult.getErrorMessage());
        }
        Assert.assertNotNull(dynamicCompiler.classLoader.loadClass("com.mulberry.athena.World"));
    }
}
