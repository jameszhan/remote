package com.apple.remote;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

public interface RemoteInvocationHandler {

	Object invoke(RemoteInvocation invocation) throws RemoteException, NoSuchMethodException, 
		IllegalAccessException, InvocationTargetException;

}
