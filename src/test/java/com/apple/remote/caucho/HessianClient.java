package com.apple.remote.caucho;

import java.net.MalformedURLException;

import com.apple.remote.HelloService;
import com.caucho.hessian.client.HessianProxyFactory;

public class HessianClient {

	public static void main(String[] args) {
		String url = "http://localhost:8086/li.ws";
		HessianProxyFactory factory = new HessianProxyFactory();
		try {
			HelloService hs = (HelloService) factory.create(HelloService.class, url);
			System.out.println(hs.sayHello("James"));
			System.out.println(hs.echo("Hello James!"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
