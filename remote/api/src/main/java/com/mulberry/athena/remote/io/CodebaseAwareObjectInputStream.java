/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.remote.io;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.server.RMIClassLoader;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 10:49 PM
 */
public class CodebaseAwareObjectInputStream extends ConfigurableObjectInputStream{

    private final String codebaseUrl;

    public CodebaseAwareObjectInputStream(InputStream in, String codebaseUrl) throws IOException {
        this(in, null, codebaseUrl);
    }

    public CodebaseAwareObjectInputStream(InputStream in, ClassLoader classLoader, String codebaseUrl) throws IOException {
        super(in, classLoader);
        this.codebaseUrl = codebaseUrl;
    }


    protected Class<?> resolveFallbackIfPossible(String className, ClassNotFoundException ex)
            throws IOException, ClassNotFoundException {
        // If codebaseUrl is set, try to load the class with the RMIClassLoader. Else, propagate the ClassNotFoundException.
        if (this.codebaseUrl == null) {
            throw ex;
        }
        return RMIClassLoader.loadClass(this.codebaseUrl, className);
    }

    protected ClassLoader getFallbackClassLoader() throws IOException {
        return RMIClassLoader.getClassLoader(this.codebaseUrl);
    }

}
