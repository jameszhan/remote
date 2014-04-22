package com.apple.remote.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class StandardRemoteImp extends UnicastRemoteObject implements StandardRemote {

    private static final long serialVersionUID = 1L;

    protected StandardRemoteImp() throws RemoteException {
	super();
    }

    @Override
    public String callRemoteService() throws RemoteException {
	String str = "Call Standard Remote Service.";
	System.out.println(str);
	return str;
    }

    @Override
    public String echo(String message) throws RemoteException {
	String str = "Standard Remote Service Echo: " + message + ".";
	System.out.println(message);
	return str;
    }

}
