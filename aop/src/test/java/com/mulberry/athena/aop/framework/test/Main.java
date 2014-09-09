package com.mulberry.athena.aop.framework.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.mulberry.athena.toolkit.reflect.Reflections;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.mulberry.athena.aop.framework.AfterAdvice;
import com.mulberry.athena.aop.framework.BeforeAdvice;
import com.mulberry.athena.aop.framework.JdkDynamicAopProxy;

public class Main {

	public static void main(String[] args) {

		List<Advice> adviceList = new ArrayList<Advice>();
		adviceList.add(new AfterAdvice() {
			public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
				System.out.println(returnValue);
				System.out.println("After.");
			}
		});
		adviceList.add(new BeforeAdvice() {
			public void before(Method method, Object[] args, Object target) throws Throwable {
				System.out.println("Before.");
			}
		});
		adviceList.add(new MethodInterceptor() {
			public Object invoke(MethodInvocation mi) throws Throwable {
				System.out.println("interceptor1 begin: " + mi);
				Object ret = mi.proceed();
				System.out.println("interceptor1 end: " + mi);
				return ret;
			}
		});

		adviceList.add(new MethodInterceptor() {

			public Object invoke(MethodInvocation mi) throws Throwable {
				System.out.println("interceptor2 begin: " + mi);
				Object ret = mi.proceed();
				System.out.println("interceptor2 end: " + mi);
				return ret;
			}
		});

		adviceList.add(new MethodInterceptor() {

			public Object invoke(MethodInvocation mi) throws Throwable {
				System.out.println("interceptor3 begin: " + mi);
				Object ret = mi.proceed();
				System.out.println("interceptor3 end: " + mi);
				return ret;
			}
		});

		LoginService ls = (LoginService) new JdkDynamicAopProxy(new LoginServiceImp(), adviceList)
				.getProxy(Reflections.getContextClassLoader());

		ls.sayHello("Test.");

	}

}
