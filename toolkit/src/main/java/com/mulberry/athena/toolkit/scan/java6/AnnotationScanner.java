package com.mulberry.athena.toolkit.scan.java6;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.mulberry.athena.toolkit.base.Consumer;
import com.mulberry.athena.toolkit.base.Consumers;
import com.mulberry.athena.toolkit.scan.DefaultClassVisitor;
import org.objectweb.asm.ClassReader;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 2:47 AM
 */
public class AnnotationScanner extends AbstractScanner {

    private final Collection<Class<? extends Annotation>> annotations;

    @SafeVarargs
    public AnnotationScanner(Set<String> packages, Class<? extends Annotation>... annotations) {
        this(null, packages, null, Arrays.asList(annotations));
    }

    public AnnotationScanner(Set<String> packages, Collection<Class<? extends Annotation>> annotations) {
        this(null, packages, null, annotations);
    }

    public AnnotationScanner(URLClassLoader classLoader, Set<String> packages, Collection<Class<? extends Annotation>> annotations) {
        this(classLoader, packages, null, annotations);
    }

    protected AnnotationScanner(URLClassLoader classLoader, Set<String> packages, String pattern, final Collection<Class<? extends Annotation>> annotations) {
        super(classLoader, packages, new PathPatternPredicate(pattern));
        this.annotations = annotations;
    }

    @Override
    protected Consumer<InputStream> buildConsumer() {
        return new Consumer<InputStream>() {
            @Override public void accept(InputStream in) {
                try {
                    new ClassReader(in).accept(new DefaultClassVisitor(
                            new AnnotatedClassInfoPredicate(annotations),
                            Consumers.collect(acceptedClassNames)), 0);
                } catch (IOException e) {
                    LOGGER.warn("Can't read the class stream", e);
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
