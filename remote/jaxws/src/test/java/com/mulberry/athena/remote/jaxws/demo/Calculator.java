/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.remote.jaxws.demo;

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
 *         Time: 11:49 PM
 */
@WebService(name="Calculator", serviceName="CalculatorService")
@SOAPBinding(style= SOAPBinding.Style.DOCUMENT, use= SOAPBinding.Use.LITERAL, parameterStyle= SOAPBinding.ParameterStyle.WRAPPED)
public interface Calculator {

    @WebResult(name="addResponse", partName="methodPart")
    @WebMethod(operationName="addOperation")
    int add(
            @WebParam(name="oprand1", mode= WebParam.Mode.IN, partName="oprandPart") int a,
            @WebParam(name="oprand2", mode= WebParam.Mode.IN, partName="oprandPart") int b
    );

    @WebResult(name="subResponse", partName="methodPart")
    @WebMethod(operationName="subOperation")
    int sub(
            @WebParam(name="oprand1", mode= WebParam.Mode.IN, partName="oprandPart") int a,
            @WebParam(name="oprand2", mode= WebParam.Mode.IN, partName="oprandPart") int b
    );

    @WebResult(name="divResponse", partName="methodPart")
    @WebMethod(operationName="divOperation")
    int div(
            @WebParam(name="oprand1", mode= WebParam.Mode.IN, partName="oprandPart") int a,
            @WebParam(name="oprand2", mode= WebParam.Mode.IN, partName="oprandPart") int b
    ) throws ArithmeticException;

    @WebResult(header=true, name="descResponse", partName="descPart")
    @WebMethod(operationName="desc")
    String operationDescription(
            @WebParam(name="op_name", partName="op_name_part", mode= WebParam.Mode.IN) String operationName
    );

    @WebResult(header=true, name="listResponse", partName="listPart")
    @WebMethod(operationName="list")
    String[] listOperations();

}