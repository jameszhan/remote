package com.mulberry.athena.aop.framework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import com.mulberry.athena.toolkit.reflect.Reflections;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;

public class JdkDynamicAopProxy implements InvocationHandler, AopProxy {
	
	private final Object target;
	private final List<Advice> adviceList;
	
	public JdkDynamicAopProxy(Object target, List<Advice> adviceList) {
		super();
		this.target = target;
		this.adviceList = adviceList;
	}
	
	public Object getProxy() {
		return getProxy(Reflections.getContextClassLoader());
	}
	
	public Object getProxy(ClassLoader classLoader) {
		return Proxy.newProxyInstance(classLoader, target.getClass().getInterfaces(), this);
	}


	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {		
		List<MethodInterceptor> chain = createInterceptorChain();
		ReflectiveMethodInvocation invocation = new ReflectiveMethodInvocation(proxy, target, method, args, chain);
		return invocation.proceed();
	}
	
	private List<MethodInterceptor> createInterceptorChain(){
		List<MethodInterceptor> chain = new ArrayList<MethodInterceptor>();
		for(Advice advice : adviceList){
			if(advice instanceof MethodInterceptor){
				chain.add((MethodInterceptor) advice);
			}else if(advice instanceof BeforeAdvice){
				chain.add(new BeforeAdviceInterceptor((BeforeAdvice) advice));
			}else if(advice instanceof AfterAdvice){
				chain.add(new AfterAdviceInterceptor((AfterAdvice) advice));
			}else{
				throw new UnsupportedOperationException(advice.getClass().getCanonicalName());
			}
		}
		return chain;
	}

}
