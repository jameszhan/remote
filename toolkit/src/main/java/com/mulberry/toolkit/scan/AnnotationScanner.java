/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.toolkit.scan;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.mulberry.toolkit.base.Consumer;
import com.mulberry.toolkit.base.Consumers;
import com.mulberry.toolkit.reflect.Reflections;
import org.objectweb.asm.ClassReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 2:47 AM
 */
public class AnnotationScanner implements Scanner {
    private final static Logger LOGGER = LoggerFactory.getLogger(AnnotationScanner.class);
    private final URLClassLoader classLoader;
    private final Set<String> packages;
    private final Predicate<Path> predicate;
    private Set<String> acceptedClassNames;

    private Consumer<Path> consumer;

    public AnnotationScanner(Set<String> packages, Class<? extends Annotation>... annotations) {
        this(null, packages, null, Arrays.asList(annotations));
    }

    public AnnotationScanner(Set<String> packages, Collection<Class<? extends Annotation>> annotations) {
        this(null, packages, null, annotations);
    }

    public AnnotationScanner(URLClassLoader classLoader, Set<String> packages, Collection<Class<? extends Annotation>> annotations) {
        this(classLoader, packages, null, annotations);
    }

    public AnnotationScanner(URLClassLoader classLoader, Set<String> packages, String pattern, final Collection<Class<? extends Annotation>> annotations) {
        if (classLoader != null) {
            this.classLoader = classLoader;
        } else {
            this.classLoader = (URLClassLoader)Reflections.getContextClassLoader();
        }
        this.packages = packages;
        this.predicate = new PathPatternPredicate(pattern);
        this.acceptedClassNames = new HashSet<String>();
        this.consumer = new Consumer<Path>() {
            @Override public void accept(Path path) {
                try {
                    new ClassReader(Files.newInputStream(path)).accept(new DefaultClassVisitor(
                            new AnnotatedClassInfoPredicate(getAnnotationSet(annotations)),
                            Consumers.collect(acceptedClassNames)), 0);
                } catch (Exception e) {
                    LOGGER.error("Can't handle class: " + path, e);
                }
            }
        };
    }

    @Override public Collection<Class<?>> scan() throws IOException {
        Scanners.scan(classLoader, packages, predicate, consumer);
        return FluentIterable.from(acceptedClassNames).transform(new LoadClassFunction(classLoader)).toSet();
    }

    private Set<String> getAnnotationSet(Collection<Class<? extends Annotation>> annotations) {
        return FluentIterable.from(annotations).transform(new Function<Class<? extends Annotation>,  String>() {
            @Override public String apply(Class<? extends Annotation> c) {
                return "L" + c.getName().replaceAll("\\.", "/") + ";";
            }
        }).toSet();
    }

    private static class AnnotatedClassInfoPredicate implements Predicate<DefaultClassVisitor.ClassInfo> {
        private final Set<String> annotations;
        private AnnotatedClassInfoPredicate(Set<String> annotations) {
            this.annotations = annotations;
        }
        @Override public boolean apply(@Nullable DefaultClassVisitor.ClassInfo classInfo) {
            return classInfo != null && classInfo.isScoped() && !Collections.disjoint(classInfo.getAnnotations(), annotations);
        }
    }


    private static class LoadClassFunction implements Function<String, Class<?>> {
        private final ClassLoader classLoader;
        private LoadClassFunction(ClassLoader classLoader) {
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

    private static class PathPatternPredicate implements Predicate<Path> {
        private final Pattern pattern;

        private PathPatternPredicate(String patternString) {
            StringBuilder sb = new StringBuilder();
            if (patternString != null && !patternString.startsWith("^")) {
                sb.append("^.*");
            }
            if (patternString != null) {
                sb.append(patternString);
            }
            if(patternString != null && !patternString.endsWith("$")) {
                sb.append(".*$");
            }
            this.pattern = Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
        }

        @Override public boolean apply(Path file) {
            return file != null && pattern.matcher(file.toString()).find();
        }
    }

}
