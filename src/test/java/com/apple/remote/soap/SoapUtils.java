package com.apple.remote.soap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.log4j.Logger;

public class SoapUtils {

	private static final Logger LOGGER = Logger.getLogger(SoapUtils.class);

	public static SOAPMessage createSampleMessage() throws SOAPException, IOException {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage message = messageFactory.createMessage();

		// Create objects for the message parts
		SOAPPart soapPart = message.getSOAPPart();
		SOAPEnvelope env = soapPart.getEnvelope();
		SOAPBody body = env.getBody();
		// Populate the body, create the main element and namespace
		SOAPElement bodyElement = body.addChildElement(env.createName("getWeatherbyCityName", "wthr", "http://WebXml.com.cn/"));
		bodyElement.addChildElement("theCityName").addTextNode("…Ó€⁄");

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(message2String(message));
		}
		// Save the message
		message.saveChanges();

		return message;
	}

	public static SOAPMessage send(SOAPMessage request, String url) throws UnsupportedOperationException, SOAPException {
		// First create the connection
		SOAPConnectionFactory connFactory = SOAPConnectionFactory.newInstance();
		SOAPConnection conn = connFactory.createConnection();

		return conn.call(request, url);

	}

	public static void main(String[] args) throws SOAPException, IOException {
		SOAPMessage request = createSampleMessage();
		String url = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx/getWeatherbyCityName";

		send(request, url);
	}

	public static String message2String(SOAPMessage msg) throws SOAPException, IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		msg.writeTo(out);
		out.flush();
		return out.toString("utf-8");
	}

}
