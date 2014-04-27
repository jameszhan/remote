/*
 * Copyright 1999-2014 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.toolkit.reflect;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.sf.cglib.beans.BeanCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
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

    public static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                ClassLoader cl = null;
                try {
                    cl = Thread.currentThread().getContextClassLoader();
                } catch (SecurityException ex) {
                    LOGGER.warn("Can't find current classloader.", ex);
                }
                return cl;
            }
        });
    }

    public static Field getDeclaredField(Class<?> type, @Nonnull String name){
        Field f = null;
        for (; null == f && null != type;) {
            try {
                f = type.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                type = type.getSuperclass();
            }
        }
        return f;
    }

    public static <T> T getFieldValue(@Nonnull Object target, @Nonnull String name) {
        Field f = getDeclaredField(target.getClass(), name);
        if (f != null) {
            f.setAccessible(true);
            try {
                return (T)f.get(target);
            } catch (IllegalAccessException e) {
                //This will never happen.
            }
        }
        return null;
    }

    public static void setDeclaredField(@Nonnull Object obj, @Nonnull String name, Object value) {
        Field f = getDeclaredField(obj.getClass(), name);
        if (f != null){
            f.setAccessible(true);
            try {
                f.set(obj, value);
            } catch (IllegalAccessException e) {
                //This will never happen.
            }
        }
    }

    /**
     *
     * @param sourceList
     * @param supplier
     */
    public static <S, T> List<T> copyList(List<S> sourceList, Supplier<T> supplier) {
        if (sourceList == null || supplier == null) {
            return Collections.emptyList();
        }
        List<T> targetList = new ArrayList<T>();
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

    public static Class<?> classForName(String name) {
        return classForName(name, getContextClassLoader());
    }

    public static Class<?> classForName(String name, ClassLoader cl) {
        if (cl != null) {
            try {
                return Class.forName(name, false, cl);
            } catch (ClassNotFoundException ex) {
            }
        }
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException ex) {
        }
        return null;
    }

    public static Class<?> classForNameWithException(String name) throws ClassNotFoundException {
        return classForNameWithException(name, getContextClassLoader());
    }

    public static Class<?> classForNameWithException(String name, ClassLoader cl) throws ClassNotFoundException {
        if (cl != null) {
            try {
                return Class.forName(name, false, cl);
            } catch (ClassNotFoundException ex) {
            }
        }
        return Class.forName(name);
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
