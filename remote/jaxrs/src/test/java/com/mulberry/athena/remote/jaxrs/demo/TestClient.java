package com.mulberry.athena.remote.jaxrs.demo;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

/*
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

public class TestClient {


    public static void main(String[] args) {
        Form form = new Form();
        form.param("name", "james");
        form.param("age", "13");


        Client c = Client.create();
        WebResource r = c.resource("http://localhost:8086/form");

        r.type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.TEXT_PLAIN_TYPE);

    }

}
*/