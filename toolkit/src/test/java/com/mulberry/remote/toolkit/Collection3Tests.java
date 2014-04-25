/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.toolkit;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.mulberry.remote.toolkit.base.Collections3;
import com.mulberry.remote.toolkit.base.Consumer;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/23/14
 *         Time: 2:40 AM
 */
public class Collection3Tests {

    @Test
    public void reduce() {
        int ret = Collections3.reduce(ImmutableList.of(1l, 2l, 3l), 10, new Function<Integer, Function<Long, Integer>>() {
            public Function<Long, Integer> apply(final Integer memo) {
                return new Function<Long, Integer>() {
                    public Integer apply(Long el) {
                        return memo + el.intValue();
                    }
                };
            }
        });
        Assert.assertEquals(ret, 16);

        ret = Collections3.reduce(ImmutableList.of(1l, 2l, 3l), new Function<Integer, Function<Long, Integer>>() {
            public Function<Long, Integer> apply(final Integer memo) {
                return new Function<Long, Integer>() {
                    public Integer apply(Long el) {
                        int a = memo == null ? 0 : memo, b = el == null ? 0 : el.intValue();
                        return a + b;  //To change body of implemented methods use File | Settings | File Templates.
                    }
                };
            }
        });
        Assert.assertEquals(ret, 6);
    }

    @Test
    public void batchExecute() {
        final AtomicInteger count = new AtomicInteger();
        List<Integer> list = ImmutableList.of(1, 2, 3, 5, 6, 7, 8, 9);
        Collections3.batchExecute(list, 3, new Consumer<List<Integer>>() {
            public void accept(List<Integer> sum) {
                Assert.assertTrue(sum.size() <= 3);
                count.addAndGet(sum.size());
            }
        });
        Assert.assertEquals(count.get(), list.size());
    }

}
