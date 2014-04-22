package com.test.jaxws;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SoapSample {
	
	public static void main(String[] args) throws UnsupportedOperationException, SOAPException, ParserConfigurationException, SAXException, IOException, TransformerException {
		SOAPConnection conn = SOAPConnectionFactory.newInstance().createConnection();
		String doc = "<tns:greet xmlns:tns=\"http://jaxws.test.com/\"><arg0>" + "DA GOUD" + "</arg0></tns:greet>";
		
		MessageFactory factory = MessageFactory.newInstance();

		SOAPMessage request = factory.createMessage();
		
		SOAPBody body = request.getSOAPBody();
		body.addDocument(parseXml(doc));
		request.saveChanges();

		SOAPMessage resp = conn.call(request, "http://localhost:8888/HelloService");
		
		SOAPElement elem = (SOAPElement) resp.getSOAPBody().getChildElements().next();

		System.out.println(xml2String(elem));
	}
	
	private static Document parseXml(String doc) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new InputSource(new StringReader(doc)));
	} 
	
	private static String xml2String(Node doc) throws TransformerException {

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");

		StringWriter sw = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(sw));
		return sw.toString();
	}
	

}
