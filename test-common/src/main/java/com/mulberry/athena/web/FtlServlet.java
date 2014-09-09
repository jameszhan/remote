package com.mulberry.athena.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import freemarker.cache.WebappTemplateLoader;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpRequestParametersHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;


public class FtlServlet extends HttpServlet {

	private static final long serialVersionUID = 9168870447130869732L;
	private Configuration freemarkerConfig;
	private TaglibFactory taglibFactory;
	private ServletContextHashModel servletContextHashModel;

	@Override
	public void init(ServletConfig config) throws ServletException {
		freemarkerConfig = new Configuration();
		freemarkerConfig.setTemplateLoader(new WebappTemplateLoader(config.getServletContext()));
		taglibFactory = new TaglibFactory(config.getServletContext());		
		servletContextHashModel = new ServletContextHashModel(new GenericServletAdapter(config.getServletContext()), 
				freemarkerConfig.getObjectWrapper());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {		
		String path = req.getServletPath();
		String viewName = path.replace(".htm", ".ftl");
		System.out.println(viewName);
		Template template = freemarkerConfig.getTemplate("WEB-INF" + viewName);
		try {
			template.process(buildModel(req, resp), resp.getWriter());
		} catch (TemplateException e) {
			e.printStackTrace();
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getFTLInstructionStack());
		}
	}
	
	private Map<String, Object> buildModel(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> model = new HashMap<String, Object>();
		model.put(FreemarkerServlet.KEY_JSP_TAGLIBS, taglibFactory);
		model.put(FreemarkerServlet.KEY_APPLICATION, servletContextHashModel);
		model.put(FreemarkerServlet.KEY_SESSION, buildSessionModel(request, response));
		model.put(FreemarkerServlet.KEY_REQUEST, new HttpRequestHashModel(request, response, 
			freemarkerConfig.getObjectWrapper()));
		model.put(FreemarkerServlet.KEY_REQUEST_PARAMETERS, new HttpRequestParametersHashModel(request));
		
		return model;
	}
	
	private HttpSessionHashModel buildSessionModel(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			return new HttpSessionHashModel(session, freemarkerConfig.getObjectWrapper());
		}
		else {
			return new HttpSessionHashModel(null, request, response, freemarkerConfig.getObjectWrapper());
		}
	}
	
	private static final class GenericServletAdapter extends GenericServlet {

		private static final long serialVersionUID = -3663628560665664523L;
		private final ServletContext servletContext;

		public GenericServletAdapter(ServletContext servletContext) {
			this.servletContext = servletContext;
		}

		public void service(ServletRequest servletRequest, ServletResponse servletResponse) {
			// no-op
		}

		public ServletContext getServletContext() {
			return this.servletContext;
		}
	}
}
