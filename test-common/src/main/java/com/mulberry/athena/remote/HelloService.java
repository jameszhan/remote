/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.remote;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/27/14
 *         Time: 9:02 AM
 */
@WebService(name="HelloService")
@SOAPBinding(parameterStyle= SOAPBinding.ParameterStyle.BARE)
public interface HelloService {

    @WebMethod(operationName="helloMethod")
    @WebResult(name="helloResponse", partName="helloResponsePartName")
    String sayHello(@WebParam(name="helloRequest", partName="helloRequestPartName")String message);

    @WebMethod(operationName="echoMethod")
    @WebResult(header=true, name="echoResponse", partName="echoResponsePartName")
    String echo(@WebParam(name="echoRequest", partName="echoRequestPartName") String message);

}
