package com.sportrait.importrs.service;

import com.sportrait.importrs.model.Album;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// class for the eventCategories API - all verbs implemented below
@Path("/eventCategories/")
public class EventCategoriesApi {
    private final Logger _logger = Logger.getLogger(getClass().getName());

    /**
     * This API method will create / update an album for an eventCategory.
     * NOTE: the current implementation only uses 1 album per eventCategory!
     * @param eventCategoryId
     * @param album
     * @return
     */
    @Path("/{eventCategoryId}/albums")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAlbum(@PathParam("eventCategoryId") int eventCategoryId, Album album){
        _logger.info("POST /api/import/eventCategories/{eventCategoryId}/albums");
        _logger.info("for eventCategoryId : " + eventCategoryId);
        _logger.debug(album);

        album.setId((long) 99);

        return  Response.ok().entity(album).build();
    }

    @Path("/{eventCategoryId}/albums")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAlbum(@PathParam("eventCategoryId") int eventCategoryId, Album album){

        if (album.getId()==null) {
            _logger.info("PUT /api/import/eventCategories/{eventCategoryId}/albums");
            _logger.info("album ID is null -> create new album");
        } else {
            _logger.info("POST /api/import/eventCategories/{eventCategoryId}/albums");
            _logger.info("album ID = :"+album.getId() +" -> update album / import for given album");
        }

        return  Response.ok().entity("not implemented").build();
    }

    @Path("/{eventCategoryId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlbums(@PathParam("eventCategoryId") int eventCategoryId){


        // load event category
        return  Response.ok().entity("not implemented").build();
    }

}
