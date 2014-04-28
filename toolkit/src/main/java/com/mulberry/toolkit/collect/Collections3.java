/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.toolkit.collect;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Range;
import com.mulberry.toolkit.base.Consumer;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/23/14
 *         Time: 2:22 AM
 */
public final class Collections3 {
    private Collections3(){}

    /**
     * Take random item form specified array.
     *
     * @param ary
     * @param <T>
     * @return
     */
    public static <T> T sample(T... ary){
        int rand = (int)(ary.length * Math.random());
        return ary[rand];
    }

    /**
     * Get randInt in [start, end)
     *
     * @param start
     * @param end
     * @return
     */
    public static int randInt(int start, int end){
        return (int)(start + Math.random() * (end - start));
    }

    /**
     * Get randLong in [start, end)
     *
     * @param start
     * @param end
     * @return
     */
    public static long randLong(long start, long end){
        return (long)(start + Math.random() * (end - start));
    }

    /**
     * Finding out the [min, max] range in given list.
     *
     * @param c
     * @param min
     * @param max
     * @param defaultValue
     * @param function
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T extends Comparable<T>, E> Range<T> rangeFor(Collection<E> c, Function<E, T> function,
                                                                 T min, T max, T defaultValue) {
        if (c != null && c.size() > 0) {
            for (E e : c) {
                T val = function.apply(e);
                if (val != null && max.compareTo(val) < 0) {
                    max = val;
                }
                if (val != null && min.compareTo(val) > 0) {
                    min = val;
                }
            }
            return Range.closed(min, max);
        }
        return Range.closed(defaultValue, defaultValue);
    }

    public static <F, T> Collection<T> collect(Collection<F> fromCollection, Function<? super F, T> function) {
        return map(fromCollection, function);
    }

    public static <F, T> Collection<T> map(Collection<F> fromCollection, Function<? super F, T> function) {
        return Collections2.transform(fromCollection, function);
    }


    public static <T, R> R reduce(Iterable<T> c, Function<R, Function<T, R>> injector){
        return reduce(c, null, injector);
    }


    public static <T, R> R reduce(Iterable<T> c, R initValue, Function<R, Function<T, R>> injector){
        if (c == null){
            return initValue;
        }
        R ret = initValue;
        for (T e : c){
            ret = injector.apply(ret).apply(e);
        }
        return ret;
    }

    public static <T> void batchExecute(List<T> c, int batchSize, Consumer<List<T>> consumer){
        if (c == null || batchSize <= 0) {
            return;
        }
        int len = c.size();
        for (int i = 0; i < len; i += batchSize) {
            int j = len < i + batchSize ? len : i + batchSize;
            List<T> subList = c.subList(i, j);
            consumer.accept(subList);
        }
    }

}
