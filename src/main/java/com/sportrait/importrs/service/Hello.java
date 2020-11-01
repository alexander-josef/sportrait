package com.sportrait.importrs.service;

import ch.unartig.studioserver.model.Event;
import ch.unartig.studioserver.model.SportsEvent;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;
import org.apache.log4j.Logger;

import javax.print.attribute.standard.Media;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("")
public class Hello {
    private final Logger _logger = Logger.getLogger(getClass().getName());
    GenericLevelDAO glDao = new GenericLevelDAO();

    @Path("/ping")
    @GET
    public Response getPing(){
        _logger.info("get /api/import/ping");
        return  Response.ok().entity("Service Online").build();
    }

    @Path("/events/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEvent(@PathParam("id") int id){
        _logger.info("get /api/import/event");
        _logger.info("for ID : " + id);

        SportsEvent event = (SportsEvent) glDao.load((long) id, SportsEvent.class);

        return  Response.ok().entity(event).build();
    }

}
