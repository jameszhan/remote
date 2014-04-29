/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.toolkit.scan;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import com.mulberry.toolkit.base.Consumer;
import com.mulberry.toolkit.reflect.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/28/14
 *         Time: 10:42 PM
 */
public abstract class AbstractScanner implements Scanner {
    protected final static Logger LOGGER = LoggerFactory.getLogger(Scanner.class);
    protected final static String ALL_CLASS_PATTERN = "glob:**/*.class";

    protected final URLClassLoader classLoader;
    protected final Set<String> packages;
    protected final Predicate<Path> predicate;
    protected final Consumer<Path> consumer;

    protected final Set<String> acceptedClassNames = Sets.newConcurrentHashSet();

    protected AbstractScanner(URLClassLoader classLoader, Set<String> packages, Predicate<Path> predicate) {
        if (classLoader != null) {
            this.classLoader = classLoader;
        } else {
            this.classLoader = (URLClassLoader) Reflections.getContextClassLoader();
        }
        this.packages = packages;
        this.predicate = predicate;
        this.consumer = buildConsumer();
    }

    protected abstract Consumer<Path> buildConsumer();

    @Override public Collection<Class<?>> scan() throws IOException {
        Scanners.scan(classLoader, packages, predicate, consumer);
        return FluentIterable.from(acceptedClassNames).transform(new LoadClassFunction(classLoader)).toSet();
    }

    protected static class LoadClassFunction implements Function<String, Class<?>> {
        private final ClassLoader classLoader;
        protected LoadClassFunction(ClassLoader classLoader) {
            this.classLoader = classLoader;
        }
        @Override public Class<?> apply(String className) {
            try {
                return classLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Unknown class: " + className, e);
            }
        }
    }
}
