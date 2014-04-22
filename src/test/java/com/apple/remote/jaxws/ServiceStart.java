package com.apple.remote.jaxws;

public class ServiceStart {
	
	public static void main(String[] args) throws Exception {
		JaxWsServiceExporter ex = new JaxWsServiceExporter();
			//new HttpServerJaxWsExporter();
		ex.export();
	}

}
