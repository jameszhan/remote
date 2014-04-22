package com.apple.remote.jaxws;

import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

public class JaxWsPortPoxyFactory extends LocalJaxWsServiceFactory {

	private Object portStub;

	private String portName;
	private QName portQName;
	private Service jaxWsService;
	private Class<?> serviceInterface;
	private String username;
	private String password;
	private String endpointAddress;
	private boolean maintainSession;
	private boolean useSoapAction;
	private String soapActionUri;
	private Map<String, Object> customProperties;

	public void init() {
		if (portStub == null) {
			Service serviceToUse = getJaxWsService();
			if (serviceToUse == null) {
				serviceToUse = createJaxWsService();
			}
			portQName = getQName(portName != null ? portName : serviceInterface.getName());
			Object stub = (portName != null ? serviceToUse.getPort(this.portQName, serviceInterface) : 
				serviceToUse.getPort(serviceInterface));
			preparePortStub(stub);
			portStub = stub;			
		}
	}

	protected void preparePortStub(Object stub) {
		Map<String, Object> stubProperties = new HashMap<String, Object>();
		String username = getUsername();
		if (username != null) {
			stubProperties.put(BindingProvider.USERNAME_PROPERTY, username);
		}
		String password = getPassword();
		if (password != null) {
			stubProperties.put(BindingProvider.PASSWORD_PROPERTY, password);
		}
		String endpointAddress = getEndpointAddress();
		if (endpointAddress != null) {
			stubProperties.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
		}
		if (isMaintainSession()) {
			stubProperties.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, Boolean.TRUE);
		}
		if (isUseSoapAction()) {
			stubProperties.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
		}
		String soapActionUri = getSoapActionUri();
		if (soapActionUri != null) {
			stubProperties.put(BindingProvider.SOAPACTION_URI_PROPERTY, soapActionUri);
		}
		stubProperties.putAll(getCustomProperties());
		if (!stubProperties.isEmpty()) {
			if (!(stub instanceof BindingProvider)) {
				throw new IllegalStateException("Port stub of class [" + stub.getClass().getName()
						+ "] is not a customizable JAX-WS stub: it does not implement interface [javax.xml.ws.BindingProvider]");
			}
			((BindingProvider) stub).getRequestContext().putAll(stubProperties);
		}
	}

	public Object getProxy() {
		return portStub;
	}

	private Service getJaxWsService() {
		return jaxWsService;
	}

	protected QName getQName(String name) {
		return (getNamespaceUri() != null ? new QName(getNamespaceUri(), name) : new QName(name));
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEndpointAddress() {
		return endpointAddress;
	}

	public void setEndpointAddress(String endpointAddress) {
		this.endpointAddress = endpointAddress;
	}

	public boolean isMaintainSession() {
		return maintainSession;
	}

	public void setMaintainSession(boolean maintainSession) {
		this.maintainSession = maintainSession;
	}

	public boolean isUseSoapAction() {
		return useSoapAction;
	}

	public void setUseSoapAction(boolean useSoapAction) {
		this.useSoapAction = useSoapAction;
	}

	public String getSoapActionUri() {
		return soapActionUri;
	}

	public void setSoapActionUri(String soapActionUri) {
		this.soapActionUri = soapActionUri;
	}

	public void setCustomProperties(Map<String, Object> customProperties) {
		this.customProperties = customProperties;
	}

	public Map<String, Object> getCustomProperties() {
		if (this.customProperties == null) {
			this.customProperties = new HashMap<String, Object>();
		}
		return this.customProperties;
	}

	public Class<?> getServiceInterface() {
		return serviceInterface;
	}

	public void setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}
}