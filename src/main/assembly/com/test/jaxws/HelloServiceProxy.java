package com.test.jaxws;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

public class HelloServiceProxy implements HelloService {

	@Override
	public String sayHello(String name) {
		

		URL wsdlUrl = null;
		try {
			wsdlUrl = new URL("http://localhost:8888/HelloService?wsdl");
		} catch (MalformedURLException e) {
		
			e.printStackTrace();
		}
		QName servieName = new QName("http://jaxws.test.com/", "HelloServiceImpService");
		QName portName = new QName("http://jaxws.test.com/", "HelloServiceImpPort");
		
		Service svc = Service.create(wsdlUrl, servieName);		
		MyService hs = svc.getPort(MyService.class);
		/*Dispatch<Source> dispatch = svc.createDispatch(portName, Source.class, Service.Mode.PAYLOAD);
		
		String msg = "<tns:greet xmlns:tns=\"http://jaxws.test.com/\"><arg0>" + name + "</arg0></tns:greet>";
		
		String resp = null;
		try {
			resp = callService(dispatch, msg);
		} catch (TransformerFactoryConfigurationError e) {
			
			e.printStackTrace();
		} catch (TransformerException e) {
			
			e.printStackTrace();
		}
		
		Pattern p = Pattern.compile("<return>([^<]+)</return>");
		Matcher m = p.matcher(resp);
		if(m.find()){
			return m.group(1);
		}*/
		return hs.test(name);
	}
	
	public static String callService(Dispatch<Source> dispatch, String request) throws TransformerFactoryConfigurationError, TransformerException{
		
		Reader reader = new StringReader(request);
		Source input = new StreamSource(reader);
		
		Source output = dispatch.invoke(input);
		
		StringWriter writer = new StringWriter();		
		StreamResult result = new StreamResult(writer);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.transform(output, result);
		
		return writer.toString();
	}

	@Override
	public String test1() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String test2() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String test3() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String test4() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String test5() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
