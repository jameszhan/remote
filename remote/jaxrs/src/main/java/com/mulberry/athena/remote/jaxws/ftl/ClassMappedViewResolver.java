package com.mulberry.athena.remote.jaxws.ftl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

@FtlAssemble
public class ClassMappedViewResolver implements ViewResolver {

	private final static Logger LOGGER = LoggerFactory.getLogger(ClassMappedViewResolver.class);

    private final Cache<Class<?>, String> viewMappings = CacheBuilder.newBuilder().build();

	private @Context UriInfo ui;

	@Override
	public int getOrder() {
		return 10;
	}

	@Override
	public String resolveViewName(Object t, final Class<?> type, OutputStream entityStream) {
        try {
            return viewMappings.get(type, new Callable<String>() {
                @Override public String call() throws Exception {
                    return type.getSimpleName().toLowerCase() + ".ftl";
                }
            });
        } catch (ExecutionException e) {
            LOGGER.info(String.format("Can't find view for request %s!", ui.getRequestUri()), e);
            return null;
        }
	}



}
