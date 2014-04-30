/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.common.features;

import org.junit.Assert;
import org.junit.Test;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/29/14
 *         Time: 7:39 PM
 */
public class LangTests {

    @Test
    public void changeConstants(){
        String aaaa = "aaaa";
        try {
            Field field = String.class.getDeclaredField("value");
            field.setAccessible(true);
            char[] value = (char[]) field.get(aaaa);
            Arrays.fill(value, 'b');
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertEquals("bbbb", aaaa);
        Assert.assertEquals("aaaa", "bbbb");

        System.out.println("aaaa");
    }

    @Test
    public void changeOutputStream(){
        System.setOut(new PrintStream(System.out) {
            public void println(String str) {
                super.println("bbbb");
            }
        });

        System.out.println("aaaa");
    }

    @Test
    public void changeSystem(){
        class Out{
            void println(String _){
                java.lang.System.out.println("bbbb");
            }
        }
        class System1 {
            public Out out = new Out();
        }
        System1 System = new System1();

        System.out.println("aaaa");
    }
}
