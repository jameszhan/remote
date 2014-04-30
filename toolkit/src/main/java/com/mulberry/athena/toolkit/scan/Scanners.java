/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.toolkit.scan;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mulberry.athena.toolkit.base.Consumer;
import com.mulberry.athena.toolkit.base.Consumers;
import com.mulberry.athena.toolkit.io.URLs;
import com.mulberry.athena.toolkit.reflect.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.zip.ZipError;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/24/14
 *         Time: 11:25 PM
 */
public final class Scanners {

    private Scanners(){}

    private static final String ALL_FILES_PATTERN = "glob:**";
    private static final Logger LOGGER = LoggerFactory.getLogger(Scanners.class);
    private static final Set<String> ZIP_FILE_FORMATS = ImmutableSet.of("jar", "zip", "war", "par", "ear");
    private static final Predicate<Path> IS_ZIP_FILE = new ExtensionPredicate(ZIP_FILE_FORMATS);

    @SafeVarargs
    public static Collection<Class<?>> annotatedBy(String pkg, Class<? extends Annotation>... annotations) throws IOException{
        return new AnnotatedScanner(ImmutableSet.of(pkg), annotations).scan();
    }

    public static Collection<Class<?>> interfacedBy(String pkg, Class<?>... intefaces) throws IOException{
        return new InterfacedScanner(ImmutableSet.of(pkg), intefaces).scan();
    }

    public static Collection<Path> matchedBy(String resourcePrefix, String syntaxAndPattern) throws IOException {
       return matchedBy(Reflections.getContextClassLoader(), resourcePrefix, syntaxAndPattern);
    }

    public static Collection<Path> matchedBy(ClassLoader classLoader, String resourcePrefix, String syntaxAndPattern) throws IOException {
        final Set<Path> paths = Sets.newHashSet();
        walkClasspath(classLoader, resourcePrefix, new PathMatcherPredicate(syntaxAndPattern), Consumers.collect(paths));
        return paths;
    }

    public static void scan(ClassLoader classLoader, Set<String> packages, final Predicate<Path> predicate, final Consumer<Path> consumer) throws IOException {
        for (String p : packages) {
            walkClasspath(classLoader, p.replace(".", "/"), predicate, consumer);
        }
    }

    public static void scan(Path path, String filePattern, Consumer<Path> consumer) throws IOException {
        scan(path, new PathMatcherPredicate(ALL_FILES_PATTERN), new PathMatcherPredicate(filePattern), consumer);
    }

    public static void scan(Path path, String dirPattern, String extension, Consumer<Path> consumer) throws IOException {
        scan(path, new PathMatcherPredicate(dirPattern), new ExtensionPredicate(extension), consumer);
    }

    public static Path scan(Path path, Predicate<Path> selector, Predicate<Path> predicate, Consumer<Path> consumer) throws IOException {
        return Files.walkFileTree(path, new DefaultFileVisitor(selector, predicate, consumer));
    }

    public static void walkClasspath(ClassLoader classLoader, String resourcePrefix, final Predicate<Path> predicate, final Consumer<Path> consumer) throws IOException {
        Enumeration<URL> e = classLoader.getResources(resourcePrefix);
        while (e.hasMoreElements()) {
            URL url = e.nextElement();
            Path path = URLs.toPath(url, classLoader);
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (predicate.apply(file)) {
                        consumer.accept(file);
                    }
                    return super.visitFile(file, attrs);
                }
            });
        }
    }

    public static Path walkZipfile(@Nonnull Path zipFile, FileVisitor<Path> fileVisitor) throws IOException {
        FileSystem fileSystem = FileSystems.newFileSystem(zipFile, Reflections.getContextClassLoader());
        try {
            Files.walkFileTree(fileSystem.getPath("/"), fileVisitor);
        } catch (ZipError e){
            LOGGER.warn("Can't handle file: " + zipFile, e);
        } finally {
            fileSystem.close();
        }
        return zipFile;
    }

    protected static class ExtensionPredicate implements Predicate<Path> {
        private final Set<String> extensions;

        private ExtensionPredicate(String extension) {
            this.extensions = ImmutableSet.of(extension);
        }

        private ExtensionPredicate(Set<String> extensions) {
            this.extensions = checkNotNull(extensions);
        }

        @Override public boolean apply(@Nullable Path input) {
            return input != null && extensions.contains(com.google.common.io.Files.getFileExtension(input.toString()));
        }
    }

    protected static class PathMatcherPredicate implements Predicate<Path> {
        private final PathMatcher pathMatcher;

        protected PathMatcherPredicate(String syntaxAndPattern) {
            this.pathMatcher = FileSystems.getDefault().getPathMatcher(checkNotNull(syntaxAndPattern));
        }

        @Override public boolean apply(@Nullable Path input) {
            return input != null && pathMatcher.matches(input);
        }
    }

    private static class DefaultFileVisitor implements FileVisitor<Path> {

        private final Predicate<Path> dirFilter;
        private final Predicate<Path> selector;
        private final Predicate<Path> predicate;
        private final Consumer<Path> consumer;

        private DefaultFileVisitor(Predicate<Path> selector, Predicate<Path> predicate, Consumer<Path> consumer) {
            this.dirFilter = Predicates.alwaysTrue();
            this.selector = selector;
            this.predicate = predicate;
            this.consumer = consumer;
        }

        private DefaultFileVisitor(Predicate<Path> dirFilter, Predicate<Path> selector, Predicate<Path> predicate, Consumer<Path> consumer) {
            this.dirFilter = dirFilter;
            this.selector = selector;
            this.predicate = predicate;
            this.consumer = consumer;
        }

        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            if (dirFilter.apply(dir)) {
                return FileVisitResult.CONTINUE;
            } else {
                return FileVisitResult.SKIP_SUBTREE;
            }
        }

        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (IS_ZIP_FILE.apply(file)) {
                walkZipfile(file, this);
            } else {
                if (file != null && selector.apply(file.getParent()) && predicate.apply(file)) {
                    consumer.accept(file);
                }
            }
            return FileVisitResult.CONTINUE;
        }

        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            LOGGER.error("Can't visited " + file + ".", exc);
            return FileVisitResult.CONTINUE;
        }

        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }
    }
}
