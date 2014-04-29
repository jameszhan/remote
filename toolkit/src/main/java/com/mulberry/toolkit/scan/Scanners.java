/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.toolkit.scan;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.mulberry.toolkit.base.Consumer;
import com.mulberry.toolkit.base.Consumers;
import com.mulberry.toolkit.io.URLs;
import com.mulberry.toolkit.reflect.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.regex.Pattern;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(Scanners.class);
    private static final Set<String> ZIP_FILE_FORMATS = ImmutableSet.of("jar", "zip", "war", "par", "ear");
    private static final Predicate<Path> IS_ZIP_FILE = new ExtensionPredicate(ZIP_FILE_FORMATS);

    public static Collection<Class<?>> annotatedBy(String pkg, Class<? extends Annotation>... annotations) throws IOException{
        return new AnnotationScanner(ImmutableSet.of(pkg), annotations).scan();
    }

    public static Collection<Class<?>> interfacedBy(String pkg, Class<?>... intefaces) throws IOException{
        return new InterfaceScanner(ImmutableSet.of(pkg), intefaces).scan();
    }


    public static void scan(URLClassLoader classLoader, Set<String> packages, final Predicate<Path> predicate, final Consumer<Path> consumer) throws IOException{
        for (String p : packages) {
            Enumeration<URL> e = classLoader.getResources(p.replace(".", "/"));
            while (e.hasMoreElements()) {
                URL url = e.nextElement();
                Path path = URLs.toPath(url, classLoader);
                Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
                    @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (predicate.apply(file)) {
                            consumer.accept(file);
                        }
                        return super.visitFile(file, attrs);
                    }
                });
            }
        }
    }

    public static void scan(Path path, String dirPattern, String extension, Consumer<Path> consumer) throws IOException {
        scan(path, new PatternPredicate(dirPattern), new ExtensionPredicate(extension), consumer);
    }

    public static void scan(String file, Predicate<String> selector, Predicate<String> predicate, Consumer<String> consumer) throws IOException {
        scan(Paths.get(file), Predicates.compose(selector, PathToString.INSTANCE),
                Predicates.compose(predicate, PathToString.INSTANCE),
                Consumers.compose(consumer, PathToString.INSTANCE));
    }

    public static void scan(File file, Predicate<File> selector, Predicate<File> predicate, Consumer<File> consumer) throws IOException {
        scan(Paths.get(file.toURI()), Predicates.compose(selector, PathToFile.INSTANCE),
                Predicates.compose(predicate, PathToFile.INSTANCE),
                Consumers.compose(consumer, PathToFile.INSTANCE));
    }

    public static Path scan(Path path, Predicate<Path> selector, Predicate<Path> predicate, Consumer<Path> consumer) throws IOException {
        return Files.walkFileTree(path, new DefaultFileVisitor(selector, predicate, consumer));
    }

    public static Path walkZipTree(@Nonnull Path zipFile, FileVisitor<Path> fileVisitor) throws IOException {
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

    private static enum PathToFile implements Function<Path, File>{
        INSTANCE;

        @Override public File apply(@Nonnull Path input) {
            return input.toFile();
        }

        @Override public String toString() {
            return "PathToFile";
        }
    }

    private static enum PathToString implements Function<Path, String>{
        INSTANCE;

        @Override public String apply(Path input) {
            return input.toString();
        }

        @Override public String toString() {
            return "PathToString";
        }
    }

    private static class ExtensionPredicate implements Predicate<Path> {
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

    private static class PatternPredicate implements Predicate<Path> {
        private final Pattern pattern;

        private PatternPredicate(String patternString) {
            StringBuilder sb = new StringBuilder();
            if (!patternString.startsWith("^")) {
                sb.append("^.*");
            }
            sb.append(patternString);
            if(!patternString.endsWith("$")) {
                sb.append(".*$");
            }
            this.pattern = Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
        }

        @Override public boolean apply(@Nullable Path input) {
            return input != null && pattern.matcher(input.toString()).find();
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
                walkZipTree(file, this);
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
