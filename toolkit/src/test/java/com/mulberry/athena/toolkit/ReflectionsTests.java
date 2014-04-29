/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.toolkit;

import com.google.common.base.Supplier;
import com.mulberry.athena.toolkit.reflect.Reflections;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/23/14
 *         Time: 12:43 AM
 */
public class ReflectionsTests {

    @Test
    public void copy(){
        A a = new A("aa", "ab"), a1 = new A("a1a", "a1b"), a2 = new A("a2a", "a2b");
        B b = new B("ba", "bc"), b1 = new B("b1a", "b1c"), b2 = new B("b2a", "b2c");
        C c = new C("ca", "cb"), c1 = new C("c1a", "c1b"), c2 = new C("c2a", "c2b");
        Reflections.copy(a, a2);
        Assert.assertEquals(a.a, a2.a);
        Assert.assertEquals(a.b, a2.b);

        Reflections.copy(a1, b1);
        Assert.assertEquals(a1.a, b1.a);

        Reflections.copy(a, c);
        Assert.assertEquals(a.a, c.a);
        Assert.assertEquals(a.b, c.b);

        Reflections.copy(b2, c2);
        Assert.assertEquals(b2.a, c2.a);

        Reflections.copy(b, c1);
        Assert.assertEquals(b.a, c1.a);
    }

    @Test
    public void copyList(){
        A a = new A("aa", "ab"), a1 = new A("a1a", "a1b"), a2 = new A("a2a", "a2b");
        List<A> sourceList = Arrays.asList(a, a1, a2);
        List<A> list = Reflections.copyList(sourceList, new Supplier<A>() {
            public A get() {
                return new A();
            }
        });
        Assert.assertEquals(list.size(), sourceList.size());
        for (int i = 0; i < sourceList.size() && i < list.size(); i++) {
            A src = sourceList.get(i), dst = list.get(i);
            Assert.assertEquals(dst.a, src.a);
            Assert.assertEquals(dst.b, src.b);
        }

        List<C> list2 = Reflections.copyList(sourceList, new Supplier<C>() {
            public C get() {
                return new C();
            }
        });
        Assert.assertEquals(list2.size(), sourceList.size());
        for (int i = 0; i < sourceList.size() && i < list2.size(); i++) {
            A src = sourceList.get(i); C dst = list2.get(i);
            Assert.assertEquals(dst.a, src.a);
            Assert.assertEquals(dst.b, src.b);
        }
    }

    static class A{
        private String a;
        private String b;

        A() {
        }

        public A(String a, String b) {
            this.a = a;
            this.b = b;
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }
    }
    static class B{
        private String a;
        private String c;

        public B(String a, String c) {
            this.a = a;
            this.c = c;
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }
    }
    static class C{
        private String a;
        private String b;

        C() {
        }

        public C(String b, String a) {
            this.b = b;
            this.a = a;
        }
        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }
    }
}
