package com.apple.test.jaxws.sample;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

/**
 * Created with IntelliJ IDEA.
 * User: James
 * Date: 13-3-20
 * Time: PM11:18
 * To change this template use File | Settings | File Templates.
 */

@WebService(portName = "PORT", serviceName = "SN", name = "NAME", targetNamespace = "www.apple.com")
public class HelloServiceImp {

    public String sayHello(String name) throws Exception {
        return "HELLO " + name;
    }


    public static void main(String[] args) {
   		Endpoint endpoint = Endpoint.create(new HelloServiceImp());
   		endpoint.publish("http://localhost:8888/HelloService");
   	}

}
