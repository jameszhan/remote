package com.apple.test.jaxws.sample;

import javax.jws.WebMethod;
import javax.xml.ws.Endpoint;

/**
 * Created with IntelliJ IDEA.
 * User: James
 * Date: 13-3-20
 * Time: PM11:17
 * To change this template use File | Settings | File Templates.
 */
public interface HelloService {


    @WebMethod(operationName = "hello")
    public String sayHello(String name) throws Exception;



}
