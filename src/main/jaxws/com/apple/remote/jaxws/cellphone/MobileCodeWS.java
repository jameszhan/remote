package com.apple.remote.jaxws.cellphone;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService(name = "MobileCodeWS", targetNamespace = "http://WebXml.com.cn/")
public interface MobileCodeWS {

	@WebMethod(action = "http://WebXml.com.cn/getMobileCodeInfo")
	@WebResult(name = "getMobileCodeInfoResult", targetNamespace = "http://WebXml.com.cn/")
	public String getMobileCodeInfo(@WebParam(name = "mobileCode", targetNamespace = "http://WebXml.com.cn/") String mobileCode,
			@WebParam(name = "userID", targetNamespace = "http://WebXml.com.cn/") String userID);

	@WebMethod(action = "http://WebXml.com.cn/getDatabaseInfo")
	@WebResult(name = "getDatabaseInfoResult", targetNamespace = "http://WebXml.com.cn/")
	public ArrayOfString getDatabaseInfo();

}
