/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.remote.jaxws.demo;

import com.google.common.collect.ImmutableSet;
import com.mulberry.remote.jaxws.HttpServerWsPublisher;
import com.mulberry.remote.jaxws.JaxWsClient;
import com.mulberry.remote.jaxws.JaxWsServicePublisher;
import com.mulberry.remote.jaxws.JaxWsUtils;
import com.mulberry.toolkit.xml.XMLs;
import org.junit.Assert;
import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceRef;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 11:54 PM
 */
public class CalculatorTests {

    private static JaxWsServicePublisher publisher = new HttpServerWsPublisher();

    @WebServiceRef(wsdlLocation="http://localhost:8080/CalculatorService?wsdl", name="CalculatorImplService")
    private static Service clientService;

    private static URL wsdlDocumentUrl;
    private static String namespace = "http://demo.jaxws.remote.mulberry.com/";
    private static String serviceName = "CalculatorImplService";
    private static String portName = "CalculatorImplPort";

    @BeforeClass
    public static void start() throws Exception {
        publisher.setPackages(ImmutableSet.of("com.mulberry.remote.jaxws"));
        publisher.start();
        publisher.publish();

        wsdlDocumentUrl = new URL("http://localhost:8080/CalculatorService?wsdl");
    }

    @AfterClass
    public static void stop(){
        publisher.stop();
    }

    @Test
    public void proxy() throws Exception{
        Calculator calculator = JaxWsClient.getProxy(wsdlDocumentUrl, namespace, serviceName, portName, Calculator.class);
        Assert.assertEquals(3, calculator.add(1, 2));
        Assert.assertEquals(-1, calculator.sub(1, 2));
        Assert.assertEquals(3, calculator.listOperations().length);
    }

    @Test
    public void service() throws Exception{
        Service	service = JaxWsUtils.createJaxWsService(wsdlDocumentUrl, new QName(namespace, serviceName));
        Calculator calculator = service.getPort(new QName(namespace, portName), Calculator.class);

        Assert.assertEquals(3, calculator.add(1, 2));
        Assert.assertEquals(-1, calculator.sub(1, 2));
        Assert.assertEquals(3, calculator.listOperations().length);
    }


    @Test
    public void dispatch() throws Exception{
        Dispatch<Source> dispatch = JaxWsUtils.createDispatch(wsdlDocumentUrl, new QName(namespace, serviceName),
                new QName(namespace, portName));
        StringWriter out = new StringWriter();

        String addRequest = buildRequest("addOperation", 1, 2);
        Source src = dispatch.invoke(new StreamSource(new StringReader(addRequest)));
        XMLs.transform(src, new StreamResult(out));
        Assert.assertEquals(expectedRespone("addOperationResponse", "addResponse", 3), out.toString());

        out = new StringWriter();
        String subRequest = buildRequest("subOperation", 3, 2);
        src = dispatch.invoke(new StreamSource(new StringReader(subRequest)));
        XMLs.transform(src, new StreamResult(out));
        Assert.assertEquals(expectedRespone("subOperationResponse", "subResponse", 1), out.toString());

        out = new StringWriter();
        String descRequest = buildRequest("desc", "<op_name>add</op_name>");
        src = dispatch.invoke(new StreamSource(new StringReader(descRequest)));
        XMLs.transform(src, new StreamResult(out));
        System.out.println(out.toString());

        out = new StringWriter();
        String listRequest = buildRequest("list", "");
        src = dispatch.invoke(new StreamSource(new StringReader(listRequest)));
        XMLs.transform(src, new StreamResult(out));
        System.out.println(out.toString());
    }

    @Test
    public void soapClient() throws Exception {
        SOAPConnection conn = SOAPConnectionFactory.newInstance().createConnection();
        MessageFactory factory = MessageFactory.newInstance();

        String doc = buildRequest("addOperation", 1, 8);
        SOAPMessage request = factory.createMessage();
        SOAPBody body = request.getSOAPBody();
        body.addDocument(XMLs.of(doc));

        request.getMimeHeaders().addHeader("mime", "application/soap+xml");
        request.getMimeHeaders().addHeader("source", "This is from MIME Header");
        request.saveChanges();

        SOAPMessage resp = conn.call(request, "http://localhost:8080/CalculatorService");

        Assert.assertTrue(XMLs.toString(resp.getSOAPPart()).contains("9"));

        resp.writeTo(System.out);
    }

    private String buildRequest(String opName, int op1, int op2){
        StringBuilder sb = new StringBuilder();
            sb.append("<oprand1>").append(op1).append("</oprand1>")
            .append("<oprand2>").append(op2).append("</oprand2>");
        return buildRequest(opName, sb.toString());
    }

    private String buildRequest(String root, String content){
        StringBuilder sb = new StringBuilder();
        sb.append("<tns:").append(root).append(" xmlns:tns=")
                .append('"').append(namespace).append('"').append(">")
                .append(content)
                .append("</tns:").append(root).append(">");
        return sb.toString();
    }

    private String expectedRespone(String root, String key, int ret){
        StringBuilder sb = new StringBuilder();
        sb.append("<ns2:").append(root).append(" xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns2=")
                .append('"').append(namespace).append('"').append(">").append("\n")
                .append("<").append(key).append(">").append(ret).append("</").append(key).append(">").append("\n")
                .append("</ns2:").append(root).append(">").append("\n");
        return sb.toString();
    }

}
