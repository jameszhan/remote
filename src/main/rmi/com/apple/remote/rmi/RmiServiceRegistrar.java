package com.apple.remote.rmi;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;
import com.apple.remote.RemoteProxyFactory;

public abstract class RmiServiceRegistrar {

	private final static Logger LOGGER = Logger.getLogger(RmiServiceRegistrar.class);
	private static Registry s_registry;

	static {
		try {
			s_registry = getRegistry();
		} catch (RemoteException e) {
			throw new IllegalStateException(e);
		}
	}

	private RmiServiceRegistrar() {
	}
	
	public static Object lookup(String name) throws RemoteException, NotBoundException {
		Remote stub = s_registry.lookup(name);
		if(stub instanceof RmiInvocationHandler){
			return getProxy((RmiInvocationHandler)stub);
		}
		return stub;		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T lookup(String name, Class<T> ifc) throws RemoteException, NotBoundException {
		Object stub = lookup(name);		
		return (T) stub;		
	}
	
	public static void bind(String name, Object obj) throws RemoteException, AlreadyBoundException {
		s_registry.bind(name, getRemoteObject(obj));
	}

	public static void unbind(String name) throws RemoteException, NotBoundException{
		s_registry.unbind(name);
	}
	
	public static void rebind(String name, Object obj) throws RemoteException {
		s_registry.rebind(name, getRemoteObject(obj));	
	}
	
	private static Remote getRemoteObject(Object obj) throws RemoteException{
		Remote remote = null;
		if (obj instanceof Remote) {			
			remote = (Remote) obj;
		} else {
			remote = new RmiInvocationWrapper(obj);				
		}	
		if(!(remote instanceof UnicastRemoteObject)){
			UnicastRemoteObject.exportObject(remote);
		}	
		return remote;
	}

	protected static Registry getRegistry() throws RemoteException {

		int registryPort = Registry.REGISTRY_PORT;
		try {
			registryPort = Integer.parseInt(System.getProperty("RMI_REGISTRY_PORT"));
		} catch (NumberFormatException e) {
			// ignore.
		}

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Looking for RMI registry at port '" + registryPort + "'");
		}
		try {
			// Retrieve existing registry.
			Registry reg = LocateRegistry.getRegistry("localhost", registryPort);
			// test the registy is successful.
			reg.list();
			return reg;
		} catch (RemoteException ex) {
			LOGGER.debug("RMI registry access threw exception", ex);
			LOGGER.info("Could not detect RMI registry - creating new one");
			// Assume no registry found -> create new one.
			return LocateRegistry.createRegistry(registryPort);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getProxy(RmiInvocationHandler stub) throws RemoteException {
		return (T) new RemoteProxyFactory(stub, stub.getInterfaces()).getProxy();		
	}

}


