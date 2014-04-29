package com.google.jersey.client;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class RESTful {
 
    public static void main(String[] args) {
        Client client = Client.create();
        WebResource webResource = client.resource("http://localhost:8080/axis2/services/rest/echo");
        System.out.println(webResource.get(String.class));
    }
    
   
}

