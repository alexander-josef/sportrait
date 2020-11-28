package com.sportrait.importrs.service;

import org.apache.log4j.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;

@Path("/photographers/")
public class PhotographersApi {

    // inject request context to read out client - client to be put to request context in auth filter
    @Context
    ContainerRequestContext requestContext;
    private final Logger _logger = Logger.getLogger(getClass().getName());
}
