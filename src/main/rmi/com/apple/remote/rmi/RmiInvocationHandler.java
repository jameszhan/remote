package com.apple.remote.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.apple.remote.RemoteInvocationHandler;


public interface RmiInvocationHandler extends RemoteInvocationHandler, Remote {

    Class<?>[] getInterfaces() throws RemoteException;

}
