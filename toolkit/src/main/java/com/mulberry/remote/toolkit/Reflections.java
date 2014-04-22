/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.toolkit;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.sf.cglib.beans.BeanCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/22/14
 *         Time: 11:36 PM
 */
public final class Reflections {
    private Reflections(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(Reflections.class);
    private static final LoadingCache<BeanCopierKey, BeanCopier> BEAN_COPIER_CACHE = CacheBuilder.newBuilder()
            .maximumSize(1000).build(new CacheLoader<BeanCopierKey, BeanCopier>() {
        public BeanCopier load(BeanCopierKey key) throws Exception {
            LOGGER.debug("Not found {}, create it!", key);
            return BeanCopier.create(key.fromClass, key.toClass, false);
        }
    });

    /**
     *
     * @param sourceList
     * @param supplier
     */
    public static <S, T> List<T> copyList(List<S> sourceList, Supplier<T> supplier) {
        if (sourceList == null || supplier == null) {
            return Collections.emptyList();
        }
        List<T> targetList = new ArrayList<>();
        for (S source : sourceList) {
            T target = supplier.get();
            copy(source, target);
            targetList.add(target);
        }
        return targetList;
    }

    /**
     * copy properties from source to target
     *
     * @param source
     * @param target
     */
    public static <S, T> void copy(S source, T target) {
        if (source == null || target == null) {
            return;
        }
        BeanCopierKey key = new BeanCopierKey(source.getClass(), target.getClass());
        try {
            BeanCopier beanCopier = BEAN_COPIER_CACHE.get(key);
            beanCopier.copy(source, target, null);
        } catch (ExecutionException e) {
            LOGGER.error("Can't create beanCopier for {}.", key);
        }
    }


    private static class BeanCopierKey{
        public final Class<?> fromClass, toClass;

        private BeanCopierKey(Class<?> fromClass, Class<?> toClass) {
            this.fromClass = fromClass;
            this.toClass = toClass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BeanCopierKey that = (BeanCopierKey) o;

            if (fromClass != null ? !fromClass.equals(that.fromClass) : that.fromClass != null) return false;
            if (toClass != null ? !toClass.equals(that.toClass) : that.toClass != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = fromClass != null ? fromClass.hashCode() : 0;
            result = 31 * result + (toClass != null ? toClass.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return new StringBuilder().append(fromClass.getName()).append(":").append(toClass.getName()).toString();
        }
    }
}
