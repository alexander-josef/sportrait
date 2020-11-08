package com.sportrait.importrs.service;

import com.sportrait.importrs.model.Album;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// class for the eventCategories API - all verbs implemented below
@Path("/eventCategories/{eventCategoryId}/albums")
public class EventCategoriesApi {
    private final Logger _logger = Logger.getLogger(getClass().getName());

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAlbum(@PathParam("eventCategoryId") int eventCategoryId, Album album){
        _logger.info("POSt /api/import/eventCategories/{eventCategoryId}/albums");
        _logger.info("for eventCategoryId : " + eventCategoryId);
        _logger.debug(album);

        album.setId((long) 99);

        return  Response.ok().entity(album).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAlbum(@PathParam("eventCategoryId") int eventCategoryId, Album album){


        return  Response.ok().entity("not implemented").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlbums(@PathParam("eventCategoryId") int eventCategoryId, Album album){


        return  Response.ok().entity("not implemented").build();
    }

}
