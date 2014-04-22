package com.test.jaxws;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;

public class UserServiceProxy implements UserService{
	
	private HelloService hs = new HelloServiceProxy();

	@Override
	public User getUserInfo(User user) {
		
		user.info = hs.sayHello(user.name);
		
		
		/*URL wsdlUrl = null;
		try {
			wsdlUrl = new URL("http://localhost:8888/HelloService?wsdl");
		} catch (MalformedURLException e) {
		
			e.printStackTrace();
		}
		QName servieName = new QName("http://jaxws.test.com/", "HelloServiceImpService");
		QName portName = new QName("http://jaxws.test.com/", "HelloServiceImpPort");
		
		Service svc = Service.create(wsdlUrl, servieName);		

		Dispatch<Source> dispatch = svc.createDispatch(portName, Source.class, Service.Mode.PAYLOAD);
		
		
		String msg = "<tns:greet xmlns:tns=\"http://jaxws.test.com/\"><arg0>" + user.name + "</arg0></tns:greet>";
		
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
			user.info = m.group(1);
			return user;
		}*/
		
		return user;
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
	
	
	

}
