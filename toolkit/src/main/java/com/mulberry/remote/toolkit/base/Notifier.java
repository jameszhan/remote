/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.toolkit.base;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 8:24 PM
 */
public abstract class Notifier<T> {

    private final Collection<T> listeners;

    public Notifier(Collection<T> listeners) {
        super();
        this.listeners = listeners;
    }

    public void run() {
        for(Iterator<T> itr = listeners.iterator(); itr.hasNext();){
            notify(itr.next());
        }
    }

    abstract protected void notify(T each);
}