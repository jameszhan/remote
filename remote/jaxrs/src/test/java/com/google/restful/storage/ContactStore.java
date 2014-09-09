package com.google.restful.storage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.restful.bean.Address;
import com.google.restful.bean.Contact;

public class ContactStore {
	private static Map<String, Contact> store;
	private static ContactStore instance = null;
	private static AtomicInteger ids = new AtomicInteger();

	private ContactStore() {
		store = new HashMap<String, Contact>();
		initOneContact();
	}

	public static Map<String, Contact> getStore() {
		if (instance == null) {
			instance = new ContactStore();
		}
		return store;
	}

	private static void initOneContact() {
		Address[] addrs = { new Address("Shanghai", "Long Hua Street"), new Address("Shenzhen", "BLVD Shennan") };
		Contact c = new Contact("contact-" + ids.incrementAndGet(), "James", Arrays.asList(addrs));
		store.put(c.getId(), c);
	}
}
