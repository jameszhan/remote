/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.toolkit.base;

import com.google.common.base.*;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/25/14
 *         Time: 12:40 AM
 */
public final class Consumers {
    private Consumers(){}

    public static <T> Consumer<T> collect(Collection<T> c) {
        checkNotNull(c);
        return new CollectConsumer<T>(c);
    }

    public static <A, B> Consumer<A> compose(Consumer<B> consumer, Function<A, B> function) {
        checkNotNull(function);
        checkNotNull(consumer);
        return new CompositionConsumer<A, B>(consumer, function);
    }

    private static class CompositionConsumer<A, B> implements Consumer<A>, Serializable {
        final Consumer<B> c;
        final Function<A, ? extends B> f;

        CompositionConsumer(Consumer<B> consumer, Function<A, B> function) {
            this.c = consumer;
            this.f = function;
        }

        @Override public void accept(A a) {
            c.accept(f.apply(a));
        }

        @Override public boolean equals(@Nullable Object obj) {
            if (obj instanceof CompositionConsumer) {
                CompositionConsumer<?, ?> that = (CompositionConsumer<?, ?>) obj;
                return f.equals(that.f) && c.equals(that.c);
            }
            return false;
        }

        @Override public int hashCode() {
            return Objects.hashCode(f, c);
        }

        @Override public String toString() {
            return "Consumers.compose(" + f + ", " + c + ")";
        }

        private static final long serialVersionUID = 0;
    }

    private static class CollectConsumer<T> implements Consumer<T>, Serializable {
        final Collection<T> c;

        private CollectConsumer(Collection<T> c) {
            this.c = c;
        }

        @Override public void accept(T a) {
            c.add(a);
        }

        @Override public boolean equals(@Nullable Object obj) {
            if (obj instanceof CollectConsumer) {
                CollectConsumer<?> that = (CollectConsumer<?>) obj;
                return c.equals(that.c);
            }
            return false;
        }

        @Override public int hashCode() {
            return Objects.hashCode("collect", c);
        }

        @Override public String toString() {
            return "Consumers.collect(" + c + ")";
        }

        private static final long serialVersionUID = 0;
    }


}
