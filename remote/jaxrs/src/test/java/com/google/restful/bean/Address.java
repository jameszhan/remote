package com.google.restful.bean;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Address {
	private String city;
	private String street;
	
	public Address() {}
	
	public Address(String city, String street) {
		this.city = city;
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}
	
	
	
}


class ExternalConfigResource {
	
	public Map<String, IExternalConfigProvider> m_providerMap;
	
	public void initialize()
	{
		//initialize all the injected external config provider;
	}
	
	public String getConfig(String sourceId, String configName)
	{
		IExternalConfigProvider provider = m_providerMap.get(sourceId);
		if(provider != null){
			return provider.getConfig(configName);
		}
		return null;
	}
	
}

interface IExternalConfigProvider
{
	String getConfig(String configName);	
}
