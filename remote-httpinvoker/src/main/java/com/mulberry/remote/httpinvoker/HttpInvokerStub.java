/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.httpinvoker;

import com.mulberry.remote.RemoteInvocation;
import com.mulberry.remote.RemoteInvocationHandler;
import com.mulberry.remote.RemoteInvocationResult;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.util.zip.GZIPInputStream;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 6:37 PM
 */
public class HttpInvokerStub extends DefaultSerDes implements RemoteInvocationHandler {

    private final Logger logger = Logger.getLogger(this.getClass());
    protected final String serviceUrl;

    public HttpInvokerStub(String serviceUrl) {
        super();
        this.serviceUrl = serviceUrl;
    }

    @Override
    public RemoteInvocationResult invoke(RemoteInvocation invocation) throws Exception {
        ByteArrayOutputStream baos = serialize(invocation);
        if (logger.isDebugEnabled()) {
            logger.debug("Sending HTTP invoker request for service at [" + getServiceUrl() +
                    "], with size " + baos.size());
        }
        return executeRequest(baos);
    }

    private RemoteInvocationResult executeRequest(ByteArrayOutputStream baos) throws Exception {
        HttpURLConnection conn = openConnection(serviceUrl);
        prepareConnection(conn, baos.size());
        writeRequestBody(conn, baos);
        validateResponse(conn);
        InputStream responseBody = readResponseBody(conn);

        return doReadRemoteInvocationResult(responseBody);
    }

    protected HttpURLConnection openConnection(String url) throws IOException {
        URLConnection conn = new URL(url).openConnection();
        if (!(conn instanceof HttpURLConnection)) {
            throw new IOException("Service URL [" + url + "] is not an HTTP URL");
        }
        return (HttpURLConnection) conn;
    }

    protected void writeRequestBody(HttpURLConnection conn, ByteArrayOutputStream baos) throws IOException {
        baos.writeTo(conn.getOutputStream());
    }

    protected RemoteInvocationResult doReadRemoteInvocationResult(InputStream in) throws IOException, ClassNotFoundException {
        Object obj = readObject(in);
        if (!(obj instanceof RemoteInvocationResult)) {
            throw new RemoteException("Deserialized object needs to be assignable to type [" + RemoteInvocationResult.class.getName() + "]: " + obj);
        }
        return (RemoteInvocationResult) obj;
    }

    protected InputStream readResponseBody(HttpURLConnection con) throws IOException {
        if (isGzipResponse(con)) {
            // GZIP response found - need to unzip.
            return new GZIPInputStream(con.getInputStream());
        } else {
            // Plain response found.
            return con.getInputStream();
        }
    }
    protected void prepareConnection(HttpURLConnection conn, int contentLength) throws IOException {
        conn.setDoOutput(true);
        conn.setRequestMethod(HTTP_METHOD_POST);
        conn.setRequestProperty(HTTP_HEADER_CONTENT_TYPE, contentType);
        conn.setRequestProperty(HTTP_HEADER_CONTENT_LENGTH, Integer.toString(contentLength));

        if (acceptGzipEncoding) {
            conn.setRequestProperty(HTTP_HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
        }
    }

    protected boolean isGzipResponse(HttpURLConnection con) {
        String encodingHeader = con.getHeaderField(HTTP_HEADER_CONTENT_ENCODING);
        return (encodingHeader != null && encodingHeader.toLowerCase().contains(ENCODING_GZIP));
    }

    protected void validateResponse(HttpURLConnection conn) throws IOException {
        if (conn.getResponseCode() >= 300) {
            throw new IOException("Did not receive successful HTTP response: status code = " + conn.getResponseCode()
                    + ", status message = [" + conn.getResponseMessage() + "]");
        }
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

}
