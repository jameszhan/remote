package com.mulberry.athena.remote.jaxrs.demo.resources;

import java.io.File;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;


@Singleton
@Path("/static")
public class StaticResource {

    @Context
    ServletContext servletContext;

    @GET
    @Path("{path}")
    public Response getResource(@PathParam("path") String path) {
        File f = getFile(path);
        String mt = new MimetypesFileTypeMap().getContentType(f);
        return Response.ok(f, mt).build();
    }

    private File getFile(String path) {
        try {
            String[] paths = {servletContext.getRealPath(path), servletContext.getRealPath("/WEB-INF/" + path)};

            for (String p : paths) {
                File file = new File(p);
                if (file.exists()) {
                    return file;
                }
            }
            throw new WebApplicationException(404);
        } catch (Exception e) {
            throw new WebApplicationException(e, 404);
        }
    }

}
