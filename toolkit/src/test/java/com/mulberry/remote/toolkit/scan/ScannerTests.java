/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.toolkit.scan;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import com.google.common.io.Files;
import com.google.common.reflect.ClassPath;
import com.mulberry.remote.toolkit.base.Consumer;
import com.mulberry.remote.toolkit.reflect.Reflections;
import junit.framework.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/24/14
 *         Time: 9:43 AM
 */
public class ScannerTests {

    @Test
    public void scanGwtCompatible() throws Exception {
        Scanner scanner = new DefaultScanner(ImmutableSet.of("com.google.common.collect"), GwtCompatible.class);
        Collection<Class<?>> classes = scanner.scan();
        Assert.assertTrue(classes.containsAll(ImmutableSet.of(Lists.class, Maps.class)));
    }
}