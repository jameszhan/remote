package com.mulberry.athena.remote.jaxrs.demo;

import java.io.File;
import java.net.URL;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.representation.Form;

@Path("/")
public class Default {

	@Context
	UriInfo uriInfo;

	@GET
	@Path("users/{username: [A-Z][a-zA-Z]*}")
	public String getUser(@PathParam("username") String username) {
		return username;
	}
	
	@GET
	@Path("wadl")
	public Response wadl() {
		URL url;
		try {
			url = new URL("http://localhost:8086/application.wadl");
			return Response.ok(url.openStream(), MediaType.TEXT_XML_TYPE).build();
		} catch (Exception e) {		
			throw new WebApplicationException(e, 500);
		}
	
	}
	
	

	@GET
	@Path("images/{image}")
	@Produces("images/*")
	public Response getImage(@PathParam("image") String image) {
		File f = new File(image);
		if (!f.exists()) {
			throw new WebApplicationException(404);
		}
		String mt = new MimetypesFileTypeMap().getContentType(f);

		return Response.ok(f, mt).build();
	}

	@GET
	@Path("/rsc")
	public Response doGet() {
		
		for (String key : uriInfo.getQueryParameters().keySet())
			System.out.println("key: " + key + " value: " + uriInfo.getQueryParameters().getFirst(key));
		String repString = uriInfo.getQueryParameters().getFirst("rep");
		int rep = repString != null ?Integer.parseInt(repString) : 0;

		String help = "<pre>For example, http://localhost:/rsc?rep=1\n" + "Valid Representations:\n" + "\t0 - StringRepresentation of this message\n"
				+ "\t1 - StringRepresentation\n" + "\t2 - FormURLEncodedRepresentation\n" + "\t3 - DataSourceRepresentation\n</pre>";
		Response r = null;
		System.out.println("rep: " + rep);
		switch (rep) {
		case 0:
			r = Response.ok(help, "text/html").header("resource-header", "text/plain").build();
			break;
		case 1:
			r = Response.ok(getStringRep(), "text/plain").header("resource-header", "text/plain").build();
			break;
		case 2:
			r = Response.ok(getFormURLEncodedRep(), "application/x-www-form-urlencoded").header("resource-header", "application/x-www-form-urlencoded").build();
			break;
		case 3:
			r = Response.ok(getImageRep(), "image/jpeg").header("resource-header", "image/*").build();
			break;
		default:
			r = Response.ok(help, "text/plain").build();
			break;
		}

		return r;
	}

	public String getStringRep() {
		return "representation: StringRepresentation: \n\n";
	}

	public Form getFormURLEncodedRep() {
		Form urlProps = new Form();
		urlProps.add("representation", "FormURLEncodedRepresentation");
		urlProps.add("name", "Master Duke");
		urlProps.add("sex", "male");	
		return urlProps;
	}

	public DataSource getImageRep() {
		URL jpgURL = this.getClass().getResource("java.jpg");
		return new FileDataSource(jpgURL.getFile());
	}

	/*
	 * @Path("roles") public String getRole(@Context SecurityContext sc){
	 * if(sc.isUserInRole("admin")){ return "admin"; }else{ return "guest"; } }
	 */

}
