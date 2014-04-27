/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.caucho;

import com.caucho.hessian.io.*;
import com.caucho.hessian.server.HessianSkeleton;
import com.mulberry.remote.util.CommonsLogWriter;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 10:32 PM
 */
public class HessianSkeletonInvoker {

    protected final HessianSkeleton skeleton;
    protected final SerializerFactory serializerFactory;
    private final Logger debugLogger = Logger.getLogger(this.getClass());

    public HessianSkeletonInvoker(HessianSkeleton skeleton, SerializerFactory serializerFactory) {
        this.skeleton = skeleton;
        this.serializerFactory = serializerFactory;
    }

    public void invoke(final InputStream inputStream, final OutputStream outputStream) throws Throwable {
        InputStream isToUse = inputStream;
        OutputStream osToUse = outputStream;

        if (this.debugLogger != null && this.debugLogger.isDebugEnabled()) {
            PrintWriter debugWriter = new PrintWriter(new CommonsLogWriter(this.debugLogger));
            isToUse = new HessianDebugInputStream(inputStream, debugWriter);
            osToUse = DebugStreamFactory.createDebugOutputStream(outputStream, debugWriter);
        }

        Hessian2Input in = new Hessian2Input(isToUse);
        if (this.serializerFactory != null) {
            in.setSerializerFactory(this.serializerFactory);
        }

        int code = in.read();
        if (code != 'c') {
            throw new IOException("expected 'c' in hessian input at " + code);
        }

        AbstractHessianOutput out = null;
        int major = in.read();
        int minor = in.read();
        debugLogger.debug(String.format("major=%d, minor=%d\n", major, minor));
        if (major >= 2) {
            out = new Hessian2Output(osToUse);
        } else {
            out = new HessianOutput(osToUse);
        }
        if (this.serializerFactory != null) {
            out.setSerializerFactory(this.serializerFactory);
        }

        try {
            this.skeleton.invoke(in, out);
        } finally {
            try {
                in.close();
                isToUse.close();
            } catch (IOException ex) {
            }
            try {
                out.close();
                osToUse.close();
            } catch (IOException ex) {
            }
        }
    }

    private static class DebugStreamFactory {

        public static OutputStream createDebugOutputStream(OutputStream os, PrintWriter debug) {
            return new HessianDebugOutputStream(os, debug);
        }
    }

}
