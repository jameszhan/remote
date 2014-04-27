/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.httpinvoker;

import com.mulberry.remote.toolkit.http.HttpConstants;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 6:24 PM
 */
public class HttpInvokerOptions implements HttpConstants {

    public static final String CONTENT_TYPE_SERIALIZED_OBJECT = "application/x-java-serialized-object";

    protected static final int SERIALIZED_BYTE_ARRAY_INITIAL_SIZE = 1024;

    protected String contentType = CONTENT_TYPE_SERIALIZED_OBJECT;
    protected boolean acceptGzipEncoding = true;

    protected ByteArrayOutputStream serialize(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(SERIALIZED_BYTE_ARRAY_INITIAL_SIZE);
        writeObject(obj, baos);
        return baos;
    }

    protected final void writeObject(Object obj, OutputStream os) throws IOException {
        ObjectOutputStream oos = createObjectOutputStream(os);
        try {
            oos.writeObject(obj);
            oos.flush();
        } finally {
            oos.close();
        }
    }

    protected final Object readObject(InputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = createObjectInputStream(in);
        try {
            return ois.readObject();
        } finally {
            ois.close();
        }
    }

    protected ObjectInputStream createObjectInputStream(InputStream in) throws IOException {
        return new ObjectInputStream(in);
    }

    protected ObjectOutputStream createObjectOutputStream(OutputStream os) throws IOException {
        return new ObjectOutputStream(os);
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isAcceptGzipEncoding() {
        return acceptGzipEncoding;
    }

    public void setAcceptGzipEncoding(boolean acceptGzipEncoding) {
        this.acceptGzipEncoding = acceptGzipEncoding;
    }
}
