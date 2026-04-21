package com.smartcampus.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/ping")
public class PingResource {

    @GET
    public String ping() {
        return "SmartCampus API is running!";
    }
}