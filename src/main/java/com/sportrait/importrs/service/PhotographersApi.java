package com.sportrait.importrs.service;

import ch.unartig.controller.Client;

import com.sportrait.importrs.Secured;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/photographers/")
public class PhotographersApi {

    // inject request context to read out client - client to be put to request context in auth filter
    @Context
    ContainerRequestContext requestContext;
    private final Logger _logger = Logger.getLogger(getClass().getName());


    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPhotographers() {
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        _logger.info("GET /albums/:albumId/products");

        return Response.ok().build();
    }
}
