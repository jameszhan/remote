package com.apple.remote.httpinvoker;

import java.io.IOException;
import com.apple.remote.HelloService;

public class HttpInvokerClient {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		HttpInvokerProxy invoker = new HttpInvokerProxy();
		invoker.setServiceUrl("http://localhost:8080/httpinvoker");

		HelloService hs = invoker.getProxy(HelloService.class);
		String resp = hs.echo("Hello World!");
		System.out.println(resp);
	}
}
