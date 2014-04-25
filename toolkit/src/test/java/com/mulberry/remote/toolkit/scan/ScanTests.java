/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.toolkit.scan;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.common.reflect.ClassPath;
import com.mulberry.remote.toolkit.base.Consumer;
import com.mulberry.remote.toolkit.reflect.Reflections;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/24/14
 *         Time: 9:43 AM
 */
public class ScanTests {
    public static void main(String[] args) throws IOException {
        //ClassPath cp = ClassPath.from(Reflections.getContextClassLoader());
        final Set<String> set = Sets.newHashSet();
        Predicate<Path> alwaysTrue = Predicates.alwaysTrue();
        Scanners.scan(Paths.get("/opt/var/maven/org/springframework"), alwaysTrue, alwaysTrue, new Consumer<Path>() {
            @Override public void accept(Path path) {
                String ext = Files.getFileExtension(path.toString());
                if (Strings.isNullOrEmpty(ext)) {
                    System.out.println(path);
                } else {
                    set.add(ext);
                }
            }
        });
        System.out.println(set);
    }

    @Test
    public void listClassPath(){
        ClassLoader classLoader = Reflections.getContextClassLoader();
        while(classLoader != null) {
            System.out.println(classLoader);
            if (classLoader instanceof URLClassLoader) {
                for (URL url :  ((URLClassLoader)classLoader).getURLs()) {
                    System.out.println(url);
                }
            }
            System.out.println();
            classLoader = classLoader.getParent();
        }

    }
}