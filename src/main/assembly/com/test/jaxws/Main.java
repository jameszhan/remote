package com.test.jaxws;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Main {

	public static void main(String[] args) {
		
		HelloService hs = (HelloService) Proxy.newProxyInstance(Main.class.getClassLoader(),
			new Class<?>[]{HelloService.class}, new InvocationHandler() {
			
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					System.out.println(method.getName() + " is invoked!");
					return method.invoke(new HelloServiceImp(), args);
				}
			});
		hs.sayHello("dfasfds");
		hs.test1();
		hs.test2();
		hs.test3();
		hs.test4();
		hs.test5();
	}
	
	

}
