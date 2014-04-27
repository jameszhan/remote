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
import com.mulberry.toolkit.reflect.Reflections;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class DefaultScanner implements Scanner {
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultScanner.class);
    private final URLClassLoader classLoader;
    private final Set<String> packages;
    private final Predicate<Path> predicate;
    private final Set<String> annotations;
    private Set<String> acceptedClassNames;

    private Consumer<Path> consumer;

    public DefaultScanner(Set<String> packages, Class<? extends Annotation>... annotations) {
        this(null, packages, null, Arrays.asList(annotations));
    }

    public DefaultScanner(Set<String> packages, Collection<Class<? extends Annotation>> annotations) {
        this(null, packages, null, annotations);
    }

    public DefaultScanner(URLClassLoader classLoader, Set<String> packages, Collection<Class<? extends Annotation>> annotations) {
        this(classLoader, packages, null, annotations);
    }

    public DefaultScanner(URLClassLoader classLoader, Set<String> packages, String pattern, Collection<Class<? extends Annotation>> annotations) {
        if (classLoader != null) {
            this.classLoader = classLoader;
        } else {
            this.classLoader = (URLClassLoader)Reflections.getContextClassLoader();
        }
        this.packages = packages;
        this.annotations = getAnnotationSet(annotations);
        this.predicate = new PathPatternPredicate(pattern);
        this.consumer = new Consumer<Path>() {
            @Override public void accept(Path path) {
                try {
                    new ClassReader(Files.newInputStream(path)).accept(new AnnotatedClassVisitor(), 0);
                } catch (Exception e) {
                    LOGGER.error("Can't handle class: " + path, e);
                }
            }
        };
        this.acceptedClassNames = new HashSet<String>();
    }

    @Override public Collection<Class<?>> scan() throws IOException {
        Scanners.scan(classLoader, packages, predicate, consumer);
        return FluentIterable.from(acceptedClassNames).transform(new LoadClassFunction(classLoader)).toList();
    }

    private Set<String> getAnnotationSet(Collection<Class<? extends Annotation>> annotations) {
        return FluentIterable.from(annotations).transform(new Function<Class<? extends Annotation>,  String>() {
            @Override public String apply(Class<? extends Annotation> c) {
                return "L" + c.getName().replaceAll("\\.", "/") + ";";
            }
        }).toSet();
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

    private final class AnnotatedClassVisitor extends ClassVisitor {
        /**
         * The name of the visited class.
         */
        private volatile String className;
        /**
         * True if the class has the correct scope
         */
        private volatile boolean isScoped;
        /**
         * True if the class has the correct declared annotations
         */
        private volatile boolean isAnnotated;

        private AnnotatedClassVisitor() {
            super(Opcodes.ASM4);
        }

        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            className = name;
            isScoped = (access & Opcodes.ACC_PUBLIC) != 0;
            isAnnotated = false;
        }

        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            isAnnotated |= annotations.contains(desc);
            return null;
        }

        public void visitInnerClass(String name, String outerName, String innerName, int access) {
            // If the name of the class that was visited is equal to the name of this visited inner class then
            // this access field needs to be used for checking the scope of the inner class
            if (className.equals(name)) {
                isScoped = (access & Opcodes.ACC_PUBLIC) != 0;

                // Inner classes need to be statically scoped
                isScoped &= (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
            }
        }

        public void visitEnd() {
            if (isScoped && isAnnotated) {
                // Correctly scoped and annotated add to the set of matching classes.
                acceptedClassNames.add(className.replace("/", "."));
            }
        }

    }

}
