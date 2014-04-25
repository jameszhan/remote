/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.toolkit.scan;

import com.google.common.base.*;
import com.google.common.collect.ImmutableSet;
import com.mulberry.remote.toolkit.base.Consumer;
import com.mulberry.remote.toolkit.base.Consumers;
import com.mulberry.remote.toolkit.reflect.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import java.util.zip.ZipError;

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
    private static final Predicate<Path> IS_ZIP_FILE = new Predicate<Path>(){
        @Override public boolean apply(@Nullable Path input) {
            return input != null && ZIP_FILE_FORMATS.contains(com.google.common.io.Files.getFileExtension(input.toString()));
        }
    };

    public static void scan(String file, Predicate<String> selector, Predicate<String> predicate, Consumer<String> consumer) throws IOException {
        scan(Paths.get(file),
                Predicates.compose(selector, PathToString.INSTANCE),
                Predicates.compose(predicate, PathToString.INSTANCE),
                Consumers.compose(consumer, PathToString.INSTANCE));
    }

    public static void scan(File file, Predicate<File> selector, Predicate<File> predicate, Consumer<File> consumer) throws IOException {
        scan(Paths.get(file.toURI()),
                Predicates.compose(selector, PathToFile.INSTANCE),
                Predicates.compose(predicate, PathToFile.INSTANCE),
                Consumers.compose(consumer, PathToFile.INSTANCE));
    }

    public static Path scan(Path path, Predicate<Path> selector, Predicate<Path> predicate, Consumer<Path> consumer) throws IOException {
        return Files.walkFileTree(path, new DefaultFileVisitor(selector, predicate, consumer));
    }

    public static Path walkZipTree(@Nonnull Path zipFile, FileVisitor<Path> fileVisitor) throws IOException {
        try (FileSystem fileSystem = FileSystems.newFileSystem(zipFile, Reflections.getContextClassLoader())) {
            Files.walkFileTree(fileSystem.getPath("/"), fileVisitor);
        } catch (ZipError e){
            LOGGER.warn("Can't handle file: " + zipFile, e);
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

        @Override public String apply(@Nonnull Path input) {
            return input.toString();
        }

        @Override public String toString() {
            return "PathToString";
        }
    }

    private static class DefaultFileVisitor implements FileVisitor<Path> {

        private final Predicate<Path> selector;
        private final Predicate<Path> predicate;
        private final Consumer<Path> consumer;

        private DefaultFileVisitor(Predicate<Path> selector, Predicate<Path> predicate, Consumer<Path> consumer) {
            this.selector = selector;
            this.predicate = predicate;
            this.consumer = consumer;
        }

        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            if (selector.apply(dir)) {
                return FileVisitResult.CONTINUE;
            } else {
                return FileVisitResult.SKIP_SUBTREE;
            }
        }

        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (IS_ZIP_FILE.apply(file)) {
                walkZipTree(file, this);
            } else {
                if (predicate.apply(file)) {
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
