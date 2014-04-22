package com.apple.remote.soap;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

public class SoapTip {
	
	public static void main(String[] args) {

		try {

			// First create the connection
			SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection connection = soapConnFactory.createConnection();

			// Next, create the actual message
			MessageFactory messageFactory = MessageFactory.newInstance();
			SOAPMessage message = messageFactory.createMessage();

			// Create objects for the message parts
			SOAPPart soapPart = message.getSOAPPart();
			SOAPEnvelope envelope = soapPart.getEnvelope();
			SOAPBody body = envelope.getBody();
			// Populate the body
			// Create the main element and namespace
			SOAPElement bodyElement = body.addChildElement(envelope.createName("getPrice", "ns1", "urn:xmethods-BNPriceCheck"));
			// Add content
			bodyElement.addChildElement("isbn").addTextNode("0672324229");
			// Save the message
			message.saveChanges();
			// Check the input
			System.out.println("\\nREQUEST:\\n");
			message.writeTo(System.out);
			System.out.println();

			// Send the message and get a reply

			// Set the destination
			String destination = "http://services.xmethods.net:80/soap/servlet/rpcrouter";
			// Send the message
			SOAPMessage reply = connection.call(message, destination);

			System.out.println(reply);
			// Close the connection
			connection.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
