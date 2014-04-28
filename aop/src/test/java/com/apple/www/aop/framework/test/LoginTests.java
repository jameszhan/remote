package com.apple.www.aop.framework.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.mulberry.toolkit.reflect.Reflections;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apple.www.aop.framework.AfterAdvice;
import com.apple.www.aop.framework.BeforeAdvice;
import com.apple.www.aop.framework.JdkDynamicAopProxy;

import static org.junit.Assert.*;

public class LoginTests {
	private final static Logger LOGGER = LoggerFactory.getLogger(LoginTests.class);
	private final static List<Advice> ADVICE_LIST = new ArrayList<Advice>();
	private final static String STATUS_SUCCESS = "Login Successful.";
	//private final static String STATUS_FAILURE = "Login Failure.";

	private LoginService loginService;

	@Before
	public void setup() {
		loginService = (LoginService) new JdkDynamicAopProxy(new LoginServiceImp(), ADVICE_LIST)
			.getProxy(Reflections.getContextClassLoader());
	}

	@Test
	public void loginSuccessful() {
		assertTrue(loginService.login("James", "123456"));
		loginService.logout("James");
		loginService.logout("james");
	}

	@BeforeClass
	public static void init() {
		ADVICE_LIST.add(new AfterAdvice() {
			public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
				if (STATUS_SUCCESS.equals(returnValue)) {
					LOGGER.info("After Advice: Login Successful.\n\n\n");
				} else {
					LOGGER.info("After Advice: Login Failure.\n\n\n");
				}
			}
		});

		ADVICE_LIST.add(new BeforeAdvice() {
			public void before(Method method, Object[] args, Object target) throws Throwable {
				LOGGER.info("Before Advice Method invoke!");
			}
		});
		
		ADVICE_LIST.add(new AfterAdvice() {
			public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
				LOGGER.info("After Advice 2.");
			}
		});

		ADVICE_LIST.add(new MethodInterceptor() {
			public Object invoke(MethodInvocation mi) throws Throwable {
				if (!mi.getMethod().getName().contains("logout") || !"james".equals(mi.getArguments()[0])) {
					return mi.proceed();
				} else {
					LOGGER.info("Can't logout james.");
					return false;
				}
			}
		});

		ADVICE_LIST.add(new MethodInterceptor() {
			public Object invoke(MethodInvocation mi) throws Throwable {
				LOGGER.info("Round Method Begin invoke.");
				Object ret = mi.proceed();
				LOGGER.info("Round Method End invoke.");
				return ret;
			}
		});

		ADVICE_LIST.add(new MethodInterceptor() {
			public Object invoke(MethodInvocation mi) throws Throwable {
				LOGGER.info("interceptor3 begin: " + mi.getMethod().getName());
				Object ret = mi.proceed();
				LOGGER.info("interceptor3 end: " + mi.getMethod().getName());
				return ret;
			}
		});
		
		ADVICE_LIST.add(new AfterAdvice() {
			public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
				LOGGER.info("After Advice 3.");
			}
		});
		
		ADVICE_LIST.add(new BeforeAdvice() {
			public void before(Method method, Object[] args, Object target) throws Throwable {
				LOGGER.info("Before Advice 2");
			}
		});

	}

	@AfterClass
	public static void destroy() {
		ADVICE_LIST.clear();
	}

}
