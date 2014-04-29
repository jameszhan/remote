/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.remote.io;

import com.mulberry.athena.toolkit.reflect.Reflections;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Proxy;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 10:47 PM
 */
public class ConfigurableObjectInputStream extends ObjectInputStream {

    private final ClassLoader classLoader;

    public ConfigurableObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
        super(in);
        this.classLoader = classLoader;
    }

    protected Class<?> resolveClass(ObjectStreamClass classDesc) throws IOException, ClassNotFoundException {
        try {
            if (this.classLoader != null) {
                // Use the specified ClassLoader to resolve local classes.
                return Reflections.classForName(classDesc.getName(), this.classLoader);
            }
            else {
                // Use the default ClassLoader...
                return super.resolveClass(classDesc);
            }
        }
        catch (ClassNotFoundException ex) {
            return resolveFallbackIfPossible(classDesc.getName(), ex);
        }
    }

    protected Class<?> resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
        if (this.classLoader != null) {
            // Use the specified ClassLoader to resolve local proxy classes.
            Class<?>[] resolvedInterfaces = new Class[interfaces.length];
            for (int i = 0; i < interfaces.length; i++) {
                try {
                    resolvedInterfaces[i] = Reflections.classForNameWithException(interfaces[i], this.classLoader);
                } catch (ClassNotFoundException ex) {
                    resolvedInterfaces[i] = resolveFallbackIfPossible(interfaces[i], ex);
                }
            }
            try {
                return Proxy.getProxyClass(this.classLoader, resolvedInterfaces);
            }
            catch (IllegalArgumentException ex) {
                throw new ClassNotFoundException(null, ex);
            }
        }
        else {
            // Use ObjectInputStream's default ClassLoader...
            try {
                return super.resolveProxyClass(interfaces);
            }
            catch (ClassNotFoundException ex) {
                Class<?>[] resolvedInterfaces = new Class[interfaces.length];
                for (int i = 0; i < interfaces.length; i++) {
                    resolvedInterfaces[i] = resolveFallbackIfPossible(interfaces[i], ex);
                }
                return Proxy.getProxyClass(getFallbackClassLoader(), resolvedInterfaces);
            }
        }
    }

    protected Class<?> resolveFallbackIfPossible(String className, ClassNotFoundException ex)
            throws IOException, ClassNotFoundException{
        throw ex;
    }

    protected ClassLoader getFallbackClassLoader() throws IOException {
        return null;
    }

}
