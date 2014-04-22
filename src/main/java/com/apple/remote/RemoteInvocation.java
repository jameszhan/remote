package com.apple.remote;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RemoteInvocation implements Serializable {

	private static final long serialVersionUID = 1L;

	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] arguments;

	public RemoteInvocation(String methodName, Class<?>[] parameterTypes, Object[] arguments) {
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.arguments = arguments;
	}

	public Object invoke(Object targetObject) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Method method = targetObject.getClass().getMethod(this.methodName, this.parameterTypes);
		return method.invoke(targetObject, this.arguments);
	}

	public String toString() {
		return "RemoteInvocation: method name '" + this.methodName + "'; parameter types " + this.parameterTypes;
	}

}
