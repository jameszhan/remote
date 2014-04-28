/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.caucho;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.mulberry.toolkit.collect.PaginatedList;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 3/10/14
 *         Time: 1:42 AM
 */
public class PaginatedListTests {

    @Test
    public void defaultSerialize() throws Exception {
        PaginatedList<Quotation> paginatedList = new PaginatedList<Quotation>(Arrays.asList(
                new Quotation("status1", "code1"),
                new Quotation("status2", "code2"),
                new Quotation("status3", "code3")), 1, 2, 3);
        Assert.assertEquals(1, paginatedList.getPageSize());
        Assert.assertEquals(2, paginatedList.getTotalCount());
        Assert.assertEquals(3, paginatedList.getPageIndex());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        try {
            oos.writeObject(paginatedList);
        } finally {
            oos.close();
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        try {
            PaginatedList<Quotation> quotations = (PaginatedList<Quotation>)ois.readObject();
            Assert.assertEquals(3, quotations.size());
            Assert.assertEquals(1, quotations.getPageSize());
            Assert.assertEquals(2, quotations.getTotalCount());
            Assert.assertEquals(3, quotations.getPageIndex());
        } finally {
            ois.close();
        }
    }

    @Test
    public void hessianSerialize() throws Exception {
        PaginatedList<Quotation> paginatedList = new PaginatedList<Quotation>(Arrays.asList(
                new Quotation("status1", "code1"),
                new Quotation("status2", "code2"),
                new Quotation("status3", "code3")), 1, 2, 3);
        Assert.assertEquals(1, paginatedList.getPageSize());
        Assert.assertEquals(2, paginatedList.getTotalCount());
        Assert.assertEquals(3, paginatedList.getPageIndex());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HessianOutput oos = new HessianOutput(baos);
        try {
            oos.writeObject(paginatedList);
        } finally {
            oos.close();
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        HessianInput ois = new HessianInput(bais);
        try {
            PaginatedList<Quotation> quotations = (PaginatedList<Quotation>)ois.readObject();
            Assert.assertEquals(3, quotations.size());
            Assert.assertEquals(1, quotations.getPageSize());
            Assert.assertEquals(2, quotations.getTotalCount());
            Assert.assertEquals(3, quotations.getPageIndex());
        } finally {
            ois.close();
        }
    }


    @Test
    public void hessian2Serialize() throws Exception {
        PaginatedList<Quotation> paginatedList = new PaginatedList<Quotation>(Arrays.asList(
                new Quotation("status1", "code1"),
                new Quotation("status2", "code2"),
                new Quotation("status3", "code3")), 1, 2, 3);
        Assert.assertEquals(1, paginatedList.getPageSize());
        Assert.assertEquals(2, paginatedList.getTotalCount());
        Assert.assertEquals(3, paginatedList.getPageIndex());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Hessian2Output oos = new Hessian2Output(baos);
        try {
            oos.writeObject(paginatedList);
        } finally {
            oos.close();
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Hessian2Input ois = new Hessian2Input(bais);
        try {
            PaginatedList<Quotation> quotations = (PaginatedList<Quotation>)ois.readObject();
            Assert.assertEquals(3, quotations.size());
            Assert.assertEquals(1, quotations.getPageSize());
            Assert.assertEquals(2, quotations.getTotalCount());
            Assert.assertEquals(3, quotations.getPageIndex());
        } finally {
            ois.close();
        }
    }

    static class Quotation implements Serializable {

        private static final long serialVersionUID = 8179468290270577895L;

        private final String status, code;

        public Quotation(String status, String code) {
            this.status = status;
            this.code = code;
        }

        public String getStatus() {
            return status;
        }

        public String getCode() {
            return code;
        }


    }
}

