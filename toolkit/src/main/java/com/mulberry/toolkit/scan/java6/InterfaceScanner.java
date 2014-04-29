package com.mulberry.toolkit.scan.java6;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.mulberry.toolkit.base.Consumer;
import com.mulberry.toolkit.base.Consumers;
import com.mulberry.toolkit.scan.DefaultClassVisitor;
import org.objectweb.asm.ClassReader;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/28/14
 *         Time: 10:42 PM
 */
public class InterfaceScanner extends AbstractScanner {

    private final Collection<Class<?>> interfaces;

    public InterfaceScanner(Set<String> packages, Class<?>... interfaces) {
        this(null, packages, null, Arrays.asList(interfaces));
    }

    public InterfaceScanner(Set<String> packages, Collection<Class<?>> interfaces) {
        this(null, packages, null, interfaces);
    }

    public InterfaceScanner(URLClassLoader classLoader, Set<String> packages, Collection<Class<?>> interfaces) {
        this(classLoader, packages, null, interfaces);
    }

    public InterfaceScanner(URLClassLoader classLoader, Set<String> packages, String pattern, final Collection<Class<?>> interfaces) {
        super(classLoader, packages, new PathPatternPredicate(pattern));
        this.interfaces = interfaces;
    }

    @Override protected Consumer<InputStream> buildConsumer() {
        return new Consumer<InputStream>() {
            @Override public void accept(InputStream in) {
                try {
                    new ClassReader(in).accept(new DefaultClassVisitor(
                            new InterfaceClassInfoPredicate(interfaces),
                            Consumers.collect(acceptedClassNames)), 0);
                } catch (Exception e) {
                    LOGGER.error("Can't handle class stream", e);
                }
            }
        };
    }

    private static Set<String> getAnnotationSet(Collection<Class<?>> interfaces) {
        return FluentIterable.from(interfaces).transform(new Function<Class<?>,  String>() {
            @Override public String apply(Class<?> c) {
                return c.getName().replaceAll("\\.", "/");
            }
        }).toSet();
    }

    private static class InterfaceClassInfoPredicate implements Predicate<DefaultClassVisitor.ClassInfo> {
        private final Set<String> interfaces;

        private InterfaceClassInfoPredicate(Collection<Class<?>> interfaces) {
            this(getAnnotationSet(interfaces));
        }
        private InterfaceClassInfoPredicate(Set<String> interfaces) {
            this.interfaces = interfaces;
        }
        @Override public boolean apply(@Nullable DefaultClassVisitor.ClassInfo classInfo) {
            return classInfo != null && classInfo.isScoped() && !Collections.disjoint(classInfo.getInterfaces(), interfaces);
        }
    }
}
