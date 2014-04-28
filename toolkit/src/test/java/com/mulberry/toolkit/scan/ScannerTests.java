/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.toolkit.scan;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.*;
import org.junit.Assert;
import org.junit.Test;

import javax.jws.WebService;
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
public class ScannerTests {

    @Test
    public void scanGwtCompatible() throws Exception {
        Scanner scanner = new AnnotationScanner(ImmutableSet.of("com.google.common.collect"), GwtCompatible.class);
        Collection<Class<?>> classes = scanner.scan();
        Assert.assertNotNull(classes);
        Set<Class<?>> subSet = ImmutableSet.of(Lists.class, Maps.class);
        Assert.assertTrue(classes.containsAll(subSet));
    }

    @Test
    public void scanTestClass() throws Exception {
        Scanner scanner = new AnnotationScanner(ImmutableSet.of("com.mulberry.toolkit.scan"), GwtCompatible.class, WebService.class);
        Collection<Class<?>> classes = scanner.scan();
        Assert.assertNotNull(classes);

        Assert.assertTrue(classes.containsAll(ImmutableSet.of(ScannerTests.class, World.class)));
    }

    @GwtCompatible
    public class Hello {

    }

    @WebService
    @GwtCompatible
    public static class World {

    }

    @GwtCompatible
    class HelloWorld {

    }

    @GwtCompatible
    static class WorldHello {

    }
}