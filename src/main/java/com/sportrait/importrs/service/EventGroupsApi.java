package com.sportrait.importrs.service;

import ch.unartig.controller.Client;
import com.sportrait.importrs.Secured;
import com.sportrait.importrs.model.EventCategory;
import com.sportrait.importrs.model.EventGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// class for the EventGroups API - all verbs implemented below
@Path("/eventGroups")
public class EventGroupsApi {

    // inject request context to read out client - client to be put to request context in auth filter
    @Context
    ContainerRequestContext requestContext;
    private final Logger _logger = LogManager.getLogger(getClass().getName());


    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response listEventGroups(){

        _logger.info("GET /eventGroups");
        return  Response.ok().entity("not implemented").build();

    }

    @POST
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response addEventGroup(EventGroup eventGroupDto){
        if (eventGroupDto.getId()==null) {
            _logger.info("POST /api/import/eventGroups");
            _logger.info("creating new eventGroup from eventGroupsDto ");
        }
        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter
        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }

    @Path("/{eventGroupId}")
    @DELETE
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteEventCategory(@PathParam("eventGroupId") int eventGroupId){

        _logger.info("DELETE /eventCategories/"+eventGroupId);
        // load event category
        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter

        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }
}
