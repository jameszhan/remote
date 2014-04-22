package com.apple.remote.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface StandardRemote extends Remote {
    
    String callRemoteService() throws RemoteException;
    
    String echo(String message) throws RemoteException;

}
