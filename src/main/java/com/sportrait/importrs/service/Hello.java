package com.sportrait.importrs.service;

import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/ping")
public class Hello {
    private final Logger _logger = Logger.getLogger(getClass().getName());

    @GET
    public Response getPing(){
        _logger.info("get /api/import/ping");
        return  Response.ok().entity("Service Online").build();
    }
}
