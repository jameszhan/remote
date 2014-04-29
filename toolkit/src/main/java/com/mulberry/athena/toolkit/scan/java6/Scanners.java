/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.toolkit.scan.java6;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.mulberry.athena.toolkit.base.Consumer;
import com.mulberry.athena.toolkit.io.URLs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/28/14
 *         Time: 11:47 PM
 */
public class Scanners {


    private static final Logger LOGGER = LoggerFactory.getLogger(com.mulberry.athena.toolkit.scan.Scanners.class);
    private static final Set<String> ZIP_FILE_FORMATS = ImmutableSet.of("jar", "zip", "war", "par", "ear");
    private static final Predicate<File> IS_ZIP_FILE = new ExtensionPredicate(ZIP_FILE_FORMATS);

    public static Collection<Class<?>> annotatedBy(String pkg, Class<? extends Annotation>... annotations) throws IOException {
        return new AnnotationScanner(ImmutableSet.of(pkg), annotations).scan();
    }

    public static Collection<Class<?>> interfacedBy(String pkg, Class<?>... intefaces) throws IOException{
        return new InterfaceScanner(ImmutableSet.of(pkg), intefaces).scan();
    }

    public static void scan(URLClassLoader classLoader, Set<String> packages, final Predicate<String> predicate, Consumer<InputStream> consumer) throws IOException{
        for (String p : packages) {
            final String packageName = p.replace(".", "/");
            Enumeration<URL> e = classLoader.getResources(packageName);
            while (e.hasMoreElements()) {
                URL url = e.nextElement();
                try {
                    if (URLs.isJarURL(url)) {
                        scanJar(new File(URLs.extractJarFileURL(url).toURI()), new Predicate<String>() {
                            @Override public boolean apply(@Nullable String input) {
                                return input != null && input.startsWith(packageName) && predicate.apply(input);
                            }
                        }, consumer);
                    } else {
                        scan(new File(url.toURI()), predicate, consumer);
                    }
                } catch (URISyntaxException ex){
                    LOGGER.warn("URL ERROR: " + url, ex);
                }
            }
        }
    }


    public static void scan(final File file, Predicate<String> predicate, Consumer<InputStream> consumer) throws IOException {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (final File child : children) {
                    scan(child, predicate, consumer);
                }
            }

        } else if(IS_ZIP_FILE.apply(file)) {
            try {
                scanJar(file, predicate, consumer);
            } catch (IOException ex) {
                LOGGER.warn("IO error when scanning jar file " + file, ex);
            }
        } else {
            if (predicate.apply(file.getCanonicalPath())) {
                consumer.accept(new FileInputStream(file));
            }
        }
    }

    public static void scanJar(final File file, Predicate<String> predicate, Consumer<InputStream> consumer) throws IOException {
        JarInputStream jarIn = null;
        try {
            jarIn = new JarInputStream(new FileInputStream(file));
            JarEntry e = jarIn.getNextJarEntry();
            while (e != null) {
                if (!e.isDirectory() && predicate.apply(e.getName())) {
                    consumer.accept(jarIn);
                }
                jarIn.closeEntry();
                e = jarIn.getNextJarEntry();
            }
        } finally {
            if (jarIn != null) {
                jarIn.close();
            }
        }
    }

    private static class ExtensionPredicate implements Predicate<File> {
        private final Set<String> extensions;

        private ExtensionPredicate(String extension) {
            this.extensions = ImmutableSet.of(extension);
        }

        private ExtensionPredicate(Set<String> extensions) {
            this.extensions = checkNotNull(extensions);
        }

        @Override public boolean apply(@Nullable File input) {
            return input != null && extensions.contains(com.google.common.io.Files.getFileExtension(input.getName()));
        }
    }

}
