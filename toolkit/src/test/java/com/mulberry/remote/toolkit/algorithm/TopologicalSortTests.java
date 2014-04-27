/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.toolkit.algorithm;

import com.mulberry.remote.toolkit.algorithm.TopologicalSort.Node;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 6:15 PM
 */
public class TopologicalSortTests {

    @Test
    public void run(){
        Node a = new Node("A");
        Node b = new Node("B");
        Node c = new Node("C");
        Node d = new Node("D");
        Node e = new Node("E");
        b.needs(a);
        c.needs(a);
        d.needs(b);
        b.needs(e);
        c.needs(e);
        d.needs(c);

        TopologicalSort sort = new TopologicalSort(Arrays.asList(a, b, c, d, e));
        List<Node> result = sort.sort();
        Assert.assertEquals(result.size(), 5);
        Assert.assertEquals(a, result.get(0));
        Assert.assertEquals(e, result.get(1));
        Assert.assertEquals(b, result.get(2));
        Assert.assertEquals(c, result.get(3));
        Assert.assertEquals(d, result.get(4));
    }


}
