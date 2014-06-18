/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.common.features;

import java.io.File;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * 虚拟机器参数设置如下：-verbose -verbose:gc
 * 设置-verbose参数是为了获取类型加载和卸载的信息
 * 设置-verbose:gc是为了获取垃圾收集的相关信息
 *
 * @author zizhi.zhzzh
 *         Date: 5/28/14
 *         Time: 8:00 PM
 */
public class OOM {
    public static void main(String[] args) {

        try {
            //准备url
            URL url = new File("test-common/target/classes").toURI().toURL();
            URL[] urls = {url};
            //获取有关类型加载的JMX接口
            ClassLoadingMXBean loadingBean = ManagementFactory.getClassLoadingMXBean();

            //用于缓存类加载器
            List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();

            while (true) {
                //加载类型并缓存类加载器实例
                ClassLoader classLoader = new URLClassLoader(urls);
                classLoaders.add(classLoader);
                classLoader.loadClass("com.mulberry.athena.common.features.OOM");

                //显示数量信息（共加载过的类型数目，当前还有效的类型数目，已经被卸载的类型数目）
                System.out.println("total: " + loadingBean.getTotalLoadedClassCount());
                System.out.println("active: " + loadingBean.getLoadedClassCount());
                System.out.println("unloaded: " + loadingBean.getUnloadedClassCount());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}