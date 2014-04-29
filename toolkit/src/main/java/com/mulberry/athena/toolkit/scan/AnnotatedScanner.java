package com.mulberry.athena.toolkit.scan;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.mulberry.athena.toolkit.base.Consumer;
import com.mulberry.athena.toolkit.base.Consumers;
import org.objectweb.asm.ClassReader;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 2:47 AM
 */
public class AnnotatedScanner extends AbstractScanner {

    private final Collection<Class<? extends Annotation>> annotations;

    @SafeVarargs
    public AnnotatedScanner(Set<String> packages, Class<? extends Annotation>... annotations) {
        this(packages, Arrays.asList(annotations));
    }

    public AnnotatedScanner(Set<String> packages, Collection<Class<? extends Annotation>> annotations) {
        this(null, packages, annotations);
    }

    public AnnotatedScanner(URLClassLoader classLoader, Set<String> packages, Collection<Class<? extends Annotation>> annotations) {
        this(classLoader, packages, ALL_CLASS_PATTERN, annotations);
    }

    protected AnnotatedScanner(URLClassLoader classLoader, Set<String> packages, String pattern, final Collection<Class<? extends Annotation>> annotations) {
        super(classLoader, packages, new Scanners.PathMatcherPredicate(pattern));
        this.annotations = annotations;
    }

    @Override
    protected Consumer<Path> buildConsumer() {
        return new Consumer<Path>() {
            @Override public void accept(Path path) {
                try {
                    new ClassReader(Files.newInputStream(path)).accept(new DefaultClassVisitor(
                            new AnnotatedClassInfoPredicate(annotations),
                            Consumers.collect(acceptedClassNames)), 0);
                } catch (Exception e) {
                    LOGGER.error("Can't handle class: " + path, e);
                }
            }
        };
    }

    private static Set<String> getAnnotationSet(Collection<Class<? extends Annotation>> annotations) {
        return FluentIterable.from(annotations).transform(new Function<Class<? extends Annotation>,  String>() {
            @Override public String apply(Class<? extends Annotation> c) {
                return "L" + c.getName().replaceAll("\\.", "/") + ";";
            }
        }).toSet();
    }

    private static class AnnotatedClassInfoPredicate implements Predicate<DefaultClassVisitor.ClassInfo> {
        private final Set<String> annotations;
        private AnnotatedClassInfoPredicate(Collection<Class<? extends Annotation>> annotations) {
            this(getAnnotationSet(annotations));
        }
        private AnnotatedClassInfoPredicate(Set<String> annotations) {
            this.annotations = annotations;
        }
        @Override public boolean apply(@Nullable DefaultClassVisitor.ClassInfo classInfo) {
            return classInfo != null && classInfo.isScoped() && !Collections.disjoint(classInfo.getAnnotations(), annotations);
        }
    }


}
