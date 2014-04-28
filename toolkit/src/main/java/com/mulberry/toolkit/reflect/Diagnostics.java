/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.toolkit.reflect;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/28/14
 *         Time: 1:41 PM
 */
public final class Diagnostics {
    private Diagnostics(){}

    public static Set<URL> listClassPath(ClassLoader cl){
        Set<URL> urls = Sets.newHashSet();
        ClassLoader current = cl;
        while(current != null) {
            if (cl instanceof URLClassLoader) {
                urls.addAll(Arrays.asList(((URLClassLoader) cl).getURLs()));
            }
            current = current.getParent();
        }
        return urls;
    }

    public static URL which(Class<?> clazz) {
        return which(clazz, Reflections.getContextClassLoader());
    }

    public static URL which(Class<?> clazz, ClassLoader cl) {
        return cl.getResource(clazz.getCanonicalName().replace(".", "/") + ".class");
    }
}
