package com.mulberry.athena.remote.jaxrs.demo.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mulberry.athena.remote.jaxrs.demo.entity.Party;
import com.mulberry.athena.remote.jaxrs.demo.entity.Person;
import com.sun.jersey.api.view.Viewable;

@Path("/view")
public class JspResource {
	
	
	@GET
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Viewable listAll(){
		return new Viewable("/list.jsp", Party.singleton().listAll());
	}
	
	@GET
	@Path("{name}")
	@Produces( { MediaType.TEXT_HTML })
	public Viewable show(@PathParam("name") String name) {
		Person p = Party.singleton().getPersonByName(name);		
		return new Viewable("/person.jsp", p);
	}
	
	
	@GET
	@Path("{category}/{searchKey}")
	@Produces( { MediaType.TEXT_HTML })
	public Viewable search(@PathParam("category") String category, @PathParam("searchKey") String searchKey) {
		List<Person> persons = Party.singleton().getPersons(category, searchKey);
		return new Viewable("/list.jsp", persons);	
	}

}
