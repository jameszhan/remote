package com.apple.remote.jaxws;

import javax.jws.WebService;
//import javax.jws.soap.SOAPBinding;
//import javax.jws.soap.SOAPBinding.ParameterStyle;

@WebService(serviceName = "ComputingCenter", targetNamespace = "http://remote.apple.com")
//@SOAPBinding(parameterStyle=ParameterStyle.BARE)
public class ComputingCenter {

	public int compute(String op, int... oprands) {
		int result = 0;
		OPTYPE optype = OPTYPE.valueOf(op);
		switch (optype) {
		case sub:
			for(int i = 0; i < oprands.length; i++){
				if(i == 0){
					result = oprands[i];
				}else{
					result -= oprands[i];
				}				
			}
			break;
		case mul:
			result = 1;
			for(double oprand : oprands){
				result *= oprand;
			}	
			break;
		case div:			
			for(int i = 0; i < oprands.length; i++){
				if(i == 0){
					result = oprands[i];
				}else{
					result /= oprands[i];
				}				
			}
			break;
		default:
			for(double oprand : oprands){
				result += oprand;
			}
			break;
		}

		return result;
	}

	public static enum OPTYPE {
		add, sub, mul, div
	}

}
