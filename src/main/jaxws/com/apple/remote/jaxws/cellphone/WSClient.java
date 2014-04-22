package com.apple.remote.jaxws.cellphone;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import com.apple.remote.jaxws.JaxWsUtils;

public class WSClient {
	
	public static void main(String[] args) throws MalformedURLException {
		
		URL wsdlURL = new URL("http://webservice.webxml.com.cn/WebServices/MobileCodeWS.asmx?wsdl");
		String ns = "http://WebXml.com.cn/";
		
		Service	service = JaxWsUtils.createService(wsdlURL, new QName(ns, "MobileCodeWS"));
		MobileCodeWS ws = service.getPort(new QName(ns, "MobileCodeWSSoap"), MobileCodeWS.class);
		
		String info = ws.getMobileCodeInfo("13798520139", null);
		System.out.println(info);
		
		List<String> list = ws.getDatabaseInfo().getString();
		
		for(String it : list){
			System.out.println(it);
		}
		
		
	}

}
