package com.apple.remote.jaxws.client;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;

import javax.xml.ws.Service;

import com.apple.remote.jaxws.JaxWsUtils;

public class HelloServiceClient {

	
	public static void main(String[] args) throws MalformedURLException, InterruptedException, ExecutionException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {

		URL wsdlUrl = new URL("http://localhost:8080/ws/HelloService?wsdl");
		QName servieName = new QName("http://jaxws.remote.apple.com/", "HelloService");
		QName portName = new QName("http://jaxws.remote.apple.com/", "HelloServicePort");
		
		Service svc = JaxWsUtils.createService(wsdlUrl, servieName);
	//	svc.addPort(portName, SOAPBinding.SOAP12HTTP_BINDING, "http://localhost:8080/ws/HelloService");
		
		Dispatch<Source> dispath = svc.createDispatch(portName, Source.class, Service.Mode.PAYLOAD);
		
	//	String content = "<username xmlns=\"http://jaxws.remote.apple.com/\">James</username>";		
		String content = "<getUser xmlns=\"http://jaxws.remote.apple.com/\"><username>James</username></getUser>";
		Reader reader = new StringReader(content);
		Source input = new StreamSource(reader);
		Source output = dispath.invoke(input);
		
		StringWriter writer = new StringWriter();		
		StreamResult result = new StreamResult(writer);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.transform(output, result);


		
		System.out.println(writer);

	//	HelloServcePort hcp = svc.getPort(portName, HelloServcePort.class);

	/*	hcp.sayHelloAsync("James", new AsyncHandler<String>() {
			@Override
			public void handleResponse(Response<String> res) {			
				try {
					System.out.println(res.get());
				} catch (InterruptedException e) {					
					e.printStackTrace();
				} catch (ExecutionException e) {				
					e.printStackTrace();
				}
			}
		});*/
		
	/*	Response<String> res1 = hcp.sayHelloAsync("James");
		Response<String> res2 = hcp.sayHelloAsync("Peter");
		Response<String> res3 = hcp.sayHelloAsync("Serena");

		System.out.println(res1.get());
		System.out.println(res2.get());
		System.out.println(res3.get());*/

	}

	
}
