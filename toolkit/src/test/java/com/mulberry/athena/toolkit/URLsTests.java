/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.toolkit;

import com.mulberry.athena.toolkit.io.URLs;
import junit.framework.Assert;
import org.junit.Test;

import java.net.URL;
import java.nio.file.Path;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 1:57 AM
 */
public class URLsTests {

    @Test
    public void toPath() throws Exception {
        String jarFilePath = "/opt/var/maven/org/springframework/spring-webmvc/4.0.1.RELEASE/spring-webmvc-4.0.1.RELEASE.jar";
        String filePath = "/org/springframework";
        String fileStr = String.format("jar:file://%s!%s", jarFilePath, filePath);
        Path path = URLs.toPath(new URL(fileStr), null);
        Assert.assertEquals(path.toString(), filePath);

        path = URLs.toPath(new URL(String.format("file://%s", jarFilePath)), null);
        Assert.assertEquals(path.toString(), jarFilePath);
    }

}
