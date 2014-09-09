package com.mulberry.athena.remote.jaxws.ftl;

import java.io.OutputStream;


@FtlAssemble
public class DefaultViewResolver implements ViewResolver {
	@Override
	public int getOrder()  {
		return 0;
	}

	@Override
	public String resolveViewName(Object t, Class<?> type, OutputStream entityStream) {
		return "default.ftl";
	}

}
