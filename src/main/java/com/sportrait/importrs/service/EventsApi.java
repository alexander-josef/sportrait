package com.sportrait.importrs.service;

import ch.unartig.controller.Client;
import com.sportrait.importrs.Secured;
import com.sportrait.importrs.model.Event;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class EventsApi {
    // inject request context to read out client - client to be put to request context in auth filter
    @Context
    ContainerRequestContext requestContext;
    private final Logger _logger = Logger.getLogger(getClass().getName());


    /**
     * Todo think about visibility - only logged in user probably
     * @return
     */
    @Path("")
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response listEvents(){

        _logger.info("GET /eventGroups");
        return  Response.ok().entity("not implemented").build();

    }

    @Path("")
    @POST
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response addEvent(Event eventDto){
        if (eventDto.getId()==null) {
            _logger.info("POST /api/import/events");
            _logger.info("creating new event from eventDto ");
        }
        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter
        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }

    @Path("/{eventId}")
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEvent(@PathParam("eventId") int eventId){

        _logger.info("GET /events/"+eventId);
        // load event category
        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter

        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }


    @Path("/{eventId}")
    @PUT
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEvent(@PathParam("eventId") int eventId){

        _logger.info("PUT /events/"+eventId);
        // load event category
        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter

        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }


    @Path("/{eventId}")
    @DELETE
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteEvent(@PathParam("eventId") int eventId){

        _logger.info("DELETE /events/"+eventId);
        // load event category
        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter

        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }
}


