package com.smartcampus.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/v1")
public class ApiRootResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String info() {
        return "Smart Campus API Running";
    }
}