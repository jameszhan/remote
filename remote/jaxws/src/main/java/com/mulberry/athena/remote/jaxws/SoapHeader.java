/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.remote.jaxws;

import javax.xml.ws.BindingProvider;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 11:41 PM
 */
public abstract class SoapHeader {

    private String username;
    private String password;
    private String endpointAddress;
    private boolean maintainSession;
    private boolean useSoapAction;
    private String soapActionUri;
    private Map<String, Object> customProperties;

    protected void handleSoapHeader(Object portStub){

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
            if (!(portStub instanceof BindingProvider)) {
                throw new IllegalStateException("Port stub of class [" + portStub.getClass().getName()
                        + "] is not a customizable JAX-WS stub: it does not implement interface [javax.xml.ws.BindingProvider]");
            }
            ((BindingProvider) portStub).getRequestContext().putAll(stubProperties);
        }
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

    public Map<String, Object> getCustomProperties() {
        if (this.customProperties == null) {
            this.customProperties = new HashMap<String, Object>();
        }
        return this.customProperties;
    }

    public void setCustomProperties(Map<String, Object> customProperties) {
        this.customProperties = customProperties;
    }

}