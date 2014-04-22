package com.apple.remote.rmi;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import com.apple.remote.RemoteInvocation;

public class RmiInvocationWrapper implements RmiInvocationHandler {

	private Object wrappedObject;

	public RmiInvocationWrapper(Object wrappedObject) throws RemoteException {
		this.wrappedObject = wrappedObject;
	}

	@Override
	public Class<?>[] getInterfaces() {
		return wrappedObject.getClass().getInterfaces();
	}

	@Override
	public Object invoke(RemoteInvocation invocation) throws RemoteException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		return invocation.invoke(wrappedObject);
	}

}
