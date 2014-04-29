package com.mulberry.athena.remote.jaxrs.demo.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.mulberry.athena.remote.jaxrs.demo.entity.Party;
import com.mulberry.athena.remote.jaxrs.demo.entity.Person;
import com.mulberry.athena.remote.jaxrs.demo.entity.PersonList;
import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Path("/")
public class PartyResource {
	
	private Party party = Party.singleton();
	
	@GET
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Person> listAll(){
		return party.listAll();
	}
	
	@GET
	@Produces( { MediaType.TEXT_PLAIN, "application/fastinfoset" })
	public PersonList listPersonList(){
		PersonList pl = new PersonList();
		pl.setList(party.listAll());
		return pl;
	}
	
	@GET
	@Path("{category}/{searchKey}")
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public List<Person> search(@PathParam("category") String category, @PathParam("searchKey") String searchKey) {
		return party.getPersons(category, searchKey);	
	}
	
	@GET
	@Path("{category}/{searchKey}")
	@Produces( { MediaType.TEXT_PLAIN, "application/fastinfoset" })
	public PersonList searchPersonList(@PathParam("category") String category, @PathParam("searchKey") String searchKey) {
		PersonList pl = new PersonList();
		pl.setList(party.getPersons(category, searchKey));
		return pl;	
	}
	
	@GET
	@Path("{name}")
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, "application/fastinfoset" })
	public Person show(@PathParam("name") String name) {
		return party.getPersonByName(name);
	}

	@GET
	@Path("company/{company}")
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public List<Person> listCompany(@PathParam("company") String company) {
		return party.getPersons("company", company);	
	}
	
	@GET
	@Path("university/{university}")
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Person> listUniversity(@PathParam("university") String university) {
		return party.getPersons("university", university);	
	}
	
	
	@GET
	@Path("nativeplace/{nativeplace}")
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public List<Person> listNativePlace(@PathParam("nativeplace") String nativeplace) {
		return party.getPersons("nativePlace", nativeplace);	
	}

	@POST	
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	public Person createPerson(Person p)
	{
		if(party.exists(p))
		{
			throw new WebApplicationException(Response.status(Status.PRECONDITION_FAILED).entity(
					p.getName() + " already existing in our system.").build());	
		}
		if(p.getName() == null)
		{
			throw new WebApplicationException(Response.status(Status.PRECONDITION_FAILED).entity(
					"Person 'name should not be null.").build());	
		}
		
		party.put(p.getName(), p);	
		return p;
	}
	
	@POST	
	@Path("batch")
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public List<Person> createPerson(List<Person> ps)
	{		
		for (Person p : ps) {
			createPerson(p);
		}		
		return party.listAll();
	}
	
	@PUT	
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	public Person updatePerson(Person replace){
		Person target = party.getPersonByName(replace.getName());
		if(target == null)
		{
			throw new WebApplicationException(Response.status(Status.PRECONDITION_FAILED).entity(
				"Can't find the person named " + replace.getName()).build());
		}
		try {
			party.update(target, replace);		
		} catch (Exception e) {			
			throw new WebApplicationException(e, 500);
		}
		return target;
	}	
	
	@PUT
	@Path("batch")
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public List<Person> updatePerson(List<Person> persons)
	{
		for(Person person : persons)
		{
			updatePerson(person);
		}
		return party.listAll();
	}	
	
	@DELETE	
	@Path("{name}")
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Consumes( { MediaType.APPLICATION_FORM_URLENCODED})
	public List<Person> deletePerson(@PathParam("name") String name)
	{
		party.remove(name);
		return party.listAll();
	}
	
	@DELETE		
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Consumes( { MediaType.APPLICATION_FORM_URLENCODED})
	public List<Person> deletePerson(@FormParam("key") String key, @FormParam("value") String value)
	{
		List<Person> persons = party.getPersons(key, value);
		for(Person p : persons)
		{
			party.remove(p.getName());
		}		
		return party.listAll();
	}
	
	

}
