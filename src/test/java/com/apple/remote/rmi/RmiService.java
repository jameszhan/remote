package com.apple.remote.rmi;

import java.rmi.RemoteException;

import com.apple.remote.HelloServiceImp;

public class RmiService {

  
    public static void main(String[] args) throws RemoteException {
	
    	RmiServiceRegistrar.rebind("String", "Hello World!");
    	RmiServiceRegistrar.rebind("Hello", new HelloServiceImp());
    }

}
