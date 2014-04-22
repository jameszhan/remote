package com.apple.remote.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import com.apple.remote.HelloService;

public class RmiClient {

	public static void main(String[] args) throws RemoteException, NotBoundException {
		
		CharSequence cs = RmiServiceRegistrar.lookup("String", CharSequence.class);
		System.out.println(cs.toString());
		System.out.println(cs.getClass());
		System.out.println(cs.length());
		
		HelloService hs = RmiServiceRegistrar.lookup("Hello", HelloService.class);
		String echo = hs.echo("Hello World!");
		System.out.println(echo);

	}

}
