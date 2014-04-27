/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.rmi;

import com.mulberry.remote.RemoteProxyFactory;
import org.apache.log4j.Logger;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 8:59 AM
 */
public class RmiServiceRegistry {
    private RmiServiceRegistry() {}

    private final static Logger LOGGER = Logger.getLogger(RmiServiceRegistry.class);
    private final static String RMI_PREFIX = "rmi://";
    private static Registry     registry;

    static {
        try {
            registry = getRegistry();
        } catch (RemoteException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Object lookup(String name) throws Exception {
        return lookup(name, null);
    }

    public static <T> T lookup(String name, Class<T> ifc) throws Exception {
        if(null == name){
            return null;
        }
        Remote stub = null;
        if(name.startsWith(RMI_PREFIX)){
            stub = Naming.lookup(name);
        }else{
            if(registry != null){
                stub = registry.lookup(name);
            }
        }
        if(stub instanceof RmiInvocationHandler){
            return getProxy((RmiInvocationHandler)stub, ifc);
        }
        return (T) stub;
    }

    public static void bind(String serviceName, Object serviceInstance) throws RemoteException, AlreadyBoundException {
        registry.bind(serviceName, getRemoteObject(serviceInstance));
    }

    public static void unbind(String name) throws RemoteException, NotBoundException{
        registry.unbind(name);
    }

    public static void rebind(String name, Object obj) throws RemoteException {
        registry.rebind(name, getRemoteObject(obj));
    }

    public static String[] list() throws RemoteException {
        return registry.list();
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

        int registryPort;
        try {
            registryPort = Integer.parseInt(System.getProperty("RMI_REGISTRY_PORT"));
        } catch (NumberFormatException e) {
            registryPort = Registry.REGISTRY_PORT;
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
    private static <T> T getProxy(RmiInvocationHandler stub, Class<T> ifc) throws Exception {
        Set<Class<?>> ifcs = stub.getInterfaces();
        if(ifc != null){
            ifcs.add(ifc);
        }
        return (T) new RemoteProxyFactory(stub, ifcs.toArray(new Class<?>[ifcs.size()])).getProxy();
    }
}
