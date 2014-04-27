/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.util;

import org.apache.log4j.Logger;

import java.io.Writer;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 10:34 PM
 */
public class CommonsLogWriter extends Writer {

    private final Logger logger;

    private final StringBuffer buffer = new StringBuffer();

    public CommonsLogWriter(Logger logger) {
        this.logger = logger;
    }


    public void write(char ch) {
        if (ch == '\n' && this.buffer.length() > 0) {
            this.logger.debug(this.buffer.toString());
            this.buffer.setLength(0);
        }
        else {
            this.buffer.append((char) ch);
        }
    }

    public void write(char[] buffer, int offset, int length) {
        for (int i = 0; i < length; i++) {
            char ch = buffer[offset + i];
            if (ch == '\n' && this.buffer.length() > 0) {
                this.logger.debug(this.buffer.toString());
                this.buffer.setLength(0);
            }
            else {
                this.buffer.append((char) ch);
            }
        }
    }

    public void flush() {
    }

    public void close() {
    }

}