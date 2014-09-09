package com.mulberry.athena.remote.jaxrs.demo.entity;

import com.google.common.collect.ImmutableSet;
import com.mulberry.athena.toolkit.reflect.Reflections;

import java.util.*;

public class Party {

	private final static Set<String> RESERVED_KEYS = ImmutableSet.of("company", "university", "nativePlace");
	private final static Set<String> CONTACTS_KEYS = ImmutableSet.of("email", "tel", "cellphone", "addr");

	private final static Party PARTY = new Party();

	private Map<String, Person> storage = new HashMap<String, Person>();

	private Party() {
		Person james = createPerson("james", "Vanceinfo", "JXNU", "Jiangxi");
		james.setContact(createContact("zhiqiangzhan@gmail.com", "13798520139", "Shenzhen"));
		james.getContact().put("QQ", "28927352");
		Person peter = createPerson("peter", "Microsoft", "JXNU", "Guangdong");
		peter.setContact(createContact("peter@gmail.com", "15998520139", "Shenzhen"));
		peter.getContact().put("QQ", "28927353");
		Person serena = createPerson("serena", "Apple", "JXNU", "Hubei");
		serena.setContact(createContact("serena@gmail.com", "15998520158", "Shenzhen"));
		serena.getContact().put("QQ", "28927356");

		createPerson("cyber", "HP", "JXNU", "Guangdong");
		createPerson("hannah", "Sun", "JXNU", "Hubei");
		createPerson("vensent", "Vanceinfo", "JXNU", "Hubei");
	}

	public static Party singleton() {
		return PARTY;
	}
	
	public boolean exists(Person p)
	{
		return storage.containsKey(p.getName());
	}

	public Person put(String key, Person value) {
		return storage.put(key, value);
	}

	public Person remove(Object key) {
		return storage.remove(key);
	}

	public Person getPersonByName(String name) {
		return storage.get(name);
	}

	public List<Person> listAll() {
		List<Person> list = new ArrayList<Person>();
		list.addAll(storage.values());
		return list;
	}

	public List<Person> getPersons(String category, String searchKey) {
		return getPersons(storage.values(), category, searchKey);
	}

	public List<Person> getPersons(Collection<Person> targetPersons, String category, String searchKey) {
		List<Person> persons = new ArrayList<Person>();
		for (Person p : targetPersons) {
			String item = null;
            if (RESERVED_KEYS.contains(category)){
				item = (String) Reflections.getFieldValue(p, category);
			} else if (CONTACTS_KEYS.contains(category) && p.getContact() != null) {
				item = (String) Reflections.getFieldValue(p.getContact(), category);
			} else {
				if (p.getContact() != null) {
					item = p.getContact().getContacts().get(category);
				}
			}
			if (item != null && item.contains(searchKey)) {
				persons.add(p);
			}
		}
		return persons;
	}
	
	public void update(Person target, Person replace) 
		throws IllegalArgumentException, IllegalAccessException  {
		String name = target.getName();
        Reflections.copy(replace, target);
		target.setName(name);
	}
	
	Person createPerson(String name, String company, String university, String nativePlace) {
		Person person = new Person(name);
		person.setCompany(company);		
		person.setNativePlace(nativePlace);
		person.setUniversity(university);
		storage.put(person.getName(), person);
		return person;
	}

	Contact createContact(String email, String cellphone, String addr) {
		Contact contact = new Contact();
		contact.setAddr(addr);
		contact.setCellphone(cellphone);
		contact.setEmail(email);
		return contact;
	}

}
