package com.mulberry.athena.toolkit.scan;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.mulberry.athena.toolkit.base.Consumer;
import com.mulberry.athena.toolkit.base.Consumers;
import org.objectweb.asm.ClassReader;

import javax.annotation.Nullable;
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
public class InterfacedScanner extends AbstractScanner {

    private final Collection<Class<?>> interfaces;

    public InterfacedScanner(Set<String> packages, Class<?>... interfaces) {
        this(packages, Arrays.asList(interfaces));
    }

    public InterfacedScanner(Set<String> packages, Collection<Class<?>> interfaces) {
        this(null, packages, interfaces);
    }

    public InterfacedScanner(URLClassLoader classLoader, Set<String> packages, Collection<Class<?>> interfaces) {
        this(classLoader, packages, ALL_CLASS_PATTERN, interfaces);
    }

    protected InterfacedScanner(URLClassLoader classLoader, Set<String> packages, String pattern, final Collection<Class<?>> interfaces) {
        super(classLoader, packages, new Scanners.PathMatcherPredicate(pattern));
        this.interfaces = interfaces;
    }

    @Override protected Consumer<Path> buildConsumer() {
        return new Consumer<Path>() {
            @Override public void accept(Path path) {
                try {
                    new ClassReader(Files.newInputStream(path)).accept(new DefaultClassVisitor(
                            new InterfaceClassInfoPredicate(interfaces),
                            Consumers.collect(acceptedClassNames)), 0);
                } catch (Exception e) {
                    LOGGER.error("Can't handle class: " + path, e);
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
