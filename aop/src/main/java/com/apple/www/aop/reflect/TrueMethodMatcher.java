package com.apple.www.aop.reflect;

import java.lang.reflect.Method;

public class TrueMethodMatcher implements MethodMatcher {

	public static final TrueMethodMatcher INSTANCE = new TrueMethodMatcher();

	private TrueMethodMatcher() {
	}

	public boolean isRuntime() {
		return false;
	}

	public boolean matches(Method method, Class<?> targetClass) {
		return true;
	}

	public boolean matches(Method method, Class<?> targetClass, Object[] args) {
		// Should never be invoked as isRuntime returns false.
		throw new UnsupportedOperationException();
	}

	private Object readResolve() {
		return INSTANCE;
	}

	public String toString() {
		return "MethodMatcher.TRUE";
	}

}
