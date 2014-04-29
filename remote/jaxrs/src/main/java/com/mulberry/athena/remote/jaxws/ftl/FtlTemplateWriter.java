package com.mulberry.athena.remote.jaxws.ftl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.spi.component.ComponentConstructor;
import com.sun.jersey.core.spi.component.ComponentInjector;
import com.sun.jersey.core.spi.scanning.PackageNamesScanner;
import com.sun.jersey.core.spi.scanning.Scanner;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import com.sun.jersey.spi.scanning.AnnotationScannerListener;

import freemarker.cache.WebappTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

@Produces(MediaType.TEXT_HTML)
@Provider
public class FtlTemplateWriter implements MessageBodyWriter<Object> {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(FtlTemplateWriter.class);
	
	private final static Pattern EXCLUDE_REGEX = Pattern.compile("\\.(jsp|htm|html|HTM|HTML)$", 
			Pattern.CASE_INSENSITIVE);
	
	private Set<ViewResolver> viewResolvers = new TreeSet<ViewResolver>(new ViewResolverComparator());

	private @Context ThreadLocal<HttpServletRequest> requestInvoker;
	//private @Context UriInfo uriInfo;
	
	private Configuration cfg = new Configuration();

	public FtlTemplateWriter(@Context ServletContext servletContext, @Context InjectableProviderContext ipc)
	{
		LOGGER.info("FtlTemplateWriter start to initialize!");	
		cfg.setTemplateLoader(new WebappTemplateLoader(servletContext));
		cfg.setServletContextForTemplateLoading(
				servletContext, "WEB-INF/templates");
		
		String config = servletContext.getInitParameter("FtlAssemble.resource.packages");
		String[] packages = {"com.mulberry.athena.remote.jaxws"};
		if(config != null && !config.isEmpty())	{
            packages = Splitter.on(CharMatcher.anyOf(",;:")).splitToList(config).toArray(new String[0]);
			LOGGER.debug("Got config for FtlTemplateWriter " + config);
			for(String p : packages){
				LOGGER.debug("Searching FtlAssemble resource from package: " + p);
			}
		}
		AnnotationScannerListener sl = new AnnotationScannerListener(FtlAssemble.class);
		Scanner scanner = new PackageNamesScanner(packages);
		scanner.scan(sl);
		 
		Set<Class<?>> classes = sl.getAnnotatedClasses();
		
		for(Class<?> cls : classes){			
			if(ViewResolver.class.isAssignableFrom(cls)){
				Class<ViewResolver> clazz = (Class<ViewResolver>) cls;	
				ViewResolver vr = getInstance(clazz, ipc);
				viewResolvers.add(vr);
				LOGGER.info("Find FtlAssemble for ViewResolver " + ReflectionHelper.objectToString(vr));
			}
		}
		
		LOGGER.info("FtlTemplateWriter have been initialized!");				
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) 
	{
		HttpServletRequest req = requestInvoker.get();
		String path = req.getPathInfo() != null ? req.getPathInfo() : "";
		return !(Viewable.class.isAssignableFrom(type) || EXCLUDE_REGEX.matcher(path).find());
	}

	@Override
	public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException, WebApplicationException {
		process(t, type, entityStream);			
	}
	
	private void process(Object t, Class<?> type, OutputStream entityStream) {
		String viewName = resolveViewName(t, type, entityStream);		
		try {
			Template template = cfg.getTemplate(viewName);
			Map<String, Object> rootMap = new HashMap<String, Object>();
			rootMap.put("it", t);			
			template.process(rootMap, new OutputStreamWriter(entityStream));		
		} catch (Exception e) {			
			throw new WebApplicationException(Response.status(
					Status.INTERNAL_SERVER_ERROR).tag(e.getLocalizedMessage()).build());
		}
	}
	
	public String resolveViewName(Object t, Class<?> type, OutputStream entityStream) {
		String name = null;
		for(ViewResolver r : viewResolvers) {
			name = r.resolveViewName(t, type, entityStream);
			if (name != null){
				break;
			}
		}
		return name;
	}
	
	
	private class ViewResolverComparator implements Comparator<ViewResolver>{

		@Override
		public int compare(ViewResolver o1, ViewResolver o2) {			
			return o2.getOrder() - o1.getOrder();
		}
	}

	private ViewResolver getInstance(
			Class<? extends ViewResolver> clazz, InjectableProviderContext ipc) {
		try {
			ComponentInjector<? extends ViewResolver> ci = new ComponentInjector<>(ipc, clazz);
			ComponentConstructor<? extends ViewResolver> cc = new ComponentConstructor(ipc, clazz, ci);
			return cc.getInstance();
		} catch (Exception ex) {
			throw new ContainerException("Unable to create resource component provider", ex);
		}
	}
	

}
