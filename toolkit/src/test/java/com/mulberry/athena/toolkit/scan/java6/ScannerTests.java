/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.toolkit.scan.java6;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mulberry.athena.toolkit.scan.Scanner;
import org.junit.Assert;
import org.junit.Test;

import javax.jws.WebService;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/29/14
 *         Time: 10:12 AM
 */
@WebService
public class ScannerTests implements Cloneable {

    @Test
    public void scanGwtCompatible() throws Exception {
        Scanner scanner = new AnnotationScanner(ImmutableSet.of("com.google.common.collect"), GwtCompatible.class);
        Collection<Class<?>> classes = scanner.scan();
        Assert.assertNotNull(classes);
        Set<Class<?>> subSet = ImmutableSet.of(Lists.class, Maps.class);
        Assert.assertTrue(classes.containsAll(subSet));
    }

    @Test
    public void scanAnnotations() throws Exception {
        Scanner scanner = new AnnotationScanner(ImmutableSet.of("com.mulberry.athena.toolkit.scan"), GwtCompatible.class, WebService.class);
        Collection<Class<?>> classes = scanner.scan();
        Assert.assertNotNull(classes);
        Assert.assertFalse(classes.isEmpty());

        Assert.assertTrue(classes.containsAll(ImmutableSet.of(ScannerTests.class, World.class)));
        Assert.assertFalse(classes.contains(Hello.class));
        Assert.assertFalse(classes.contains(HelloWorld.class));
        Assert.assertFalse(classes.contains(WorldHello.class));
    }

    @Test
    public void scanInterfaces() throws Exception {
        Scanner scanner = new InterfaceScanner(ImmutableSet.of("com.mulberry.athena.toolkit.scan"), Serializable.class, Cloneable.class);
        Collection<Class<?>> classes = scanner.scan();
        Assert.assertNotNull(classes);
        Assert.assertFalse(classes.isEmpty());

        Assert.assertTrue(classes.containsAll(ImmutableSet.of(ScannerTests.class, World.class)));
        Assert.assertFalse(classes.contains(Hello.class));
        Assert.assertFalse(classes.contains(HelloWorld.class));
        Assert.assertFalse(classes.contains(WorldHello.class));
    }

    @Test
    public void annotatedBy() throws Exception {
        Collection<Class<?>> classes = Scanners.annotatedBy("com.mulberry.athena.toolkit.scan", GwtCompatible.class, WebService.class);
        Assert.assertNotNull(classes);
        Assert.assertFalse(classes.isEmpty());

        Assert.assertTrue(classes.containsAll(ImmutableSet.of(ScannerTests.class, World.class)));
        Assert.assertFalse(classes.contains(Hello.class));
        Assert.assertFalse(classes.contains(HelloWorld.class));
        Assert.assertFalse(classes.contains(WorldHello.class));
    }

    @Test
    public void interfacedBy() throws Exception {
        Collection<Class<?>> classes = Scanners.interfacedBy("com.mulberry.athena.toolkit.scan", Serializable.class, Cloneable.class);
        Assert.assertNotNull(classes);
        Assert.assertFalse(classes.isEmpty());

        Assert.assertTrue(classes.containsAll(ImmutableSet.of(ScannerTests.class, World.class)));
        Assert.assertFalse(classes.contains(Hello.class));
        Assert.assertFalse(classes.contains(HelloWorld.class));
        Assert.assertFalse(classes.contains(WorldHello.class));
    }

    @GwtCompatible public class Hello implements Serializable, Cloneable {}

    @WebService @GwtCompatible public static class World implements Cloneable {}

    @GwtCompatible class HelloWorld implements Serializable {}

    @GwtCompatible static class WorldHello implements Cloneable {}
}