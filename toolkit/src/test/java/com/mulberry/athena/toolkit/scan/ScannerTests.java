/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.toolkit.scan;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import javax.jws.WebService;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/24/14
 *         Time: 9:43 AM
 */
@WebService
public class ScannerTests implements Cloneable {

    @Test
    public void scanGwtCompatible() throws Exception {
        Scanner scanner = new AnnotatedScanner(ImmutableSet.of("com.google.common.collect"), GwtCompatible.class);
        Collection<Class<?>> classes = scanner.scan();
        Assert.assertNotNull(classes);
        Set<Class<?>> subSet = ImmutableSet.of(Lists.class, Maps.class);
        Assert.assertTrue(classes.containsAll(subSet));
    }

    @Test
    public void scanAnnotations() throws Exception {
        Scanner scanner = new AnnotatedScanner(ImmutableSet.of("com.mulberry.athena.toolkit.scan"), GwtCompatible.class, WebService.class);
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
        Scanner scanner = new InterfacedScanner(ImmutableSet.of("com.mulberry.athena.toolkit.scan"), Serializable.class, Cloneable.class);
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

    @Test
    public void walkClassPath() throws IOException {

    }

    @Test
    public void locatedBy() throws Exception {
        Collection<Path> paths = Scanners.matchedBy("META-INF", "glob:**/pom.xml");
        Assert.assertNotNull(paths);
        Assert.assertFalse(paths.isEmpty());

        paths = Scanners.matchedBy("com/google", "glob:**com/google/**/xml/*.class");
        Assert.assertNotNull(paths);
        Assert.assertFalse(paths.isEmpty());
        Assert.assertEquals(2, paths.size());

        paths = Scanners.matchedBy("log4j.properties", "regex:.+");
        Assert.assertNotNull(paths);
        Assert.assertFalse(paths.isEmpty());
        Assert.assertTrue(paths.size() > 0);
    }


    public static String toString(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte[] buf = new byte[1024];
        int len = in.read(buf);
        while (len >= 0) {
            sb.append(new String(buf, 0, len));
            len = in.read(buf);
        }
        return sb.toString();
    }


    @GwtCompatible public class Hello implements Serializable, Cloneable {}

    @WebService @GwtCompatible public static class World implements Cloneable {}

    @GwtCompatible class HelloWorld implements Serializable {}

    @GwtCompatible static class WorldHello implements Cloneable {}
}