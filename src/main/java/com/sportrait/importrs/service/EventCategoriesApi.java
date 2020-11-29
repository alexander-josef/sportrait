package com.sportrait.importrs.service;

import ch.unartig.controller.Client;
import ch.unartig.studioserver.model.Photographer;
import ch.unartig.studioserver.model.SportsAlbum;
import ch.unartig.studioserver.model.SportsEvent;
import ch.unartig.studioserver.persistence.DAOs.EventCategoryDAO;
import ch.unartig.studioserver.persistence.DAOs.PhotographerDAO;
import com.sportrait.importrs.Secured;
import com.sportrait.importrs.model.Album;
import com.sportrait.importrs.model.EventCategory;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// class for the eventCategories API - all verbs implemented below
@Path("/eventCategories/")
public class EventCategoriesApi {
    // inject request context to read out client - client to be put to request context in auth filter
    @Context
    ContainerRequestContext requestContext;
    private final Logger _logger = Logger.getLogger(getClass().getName());



    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addEventCategory(EventCategory eventCategoryDto){
        if (eventCategoryDto.getId()!=null) {
            _logger.info("POST /api/import/eventCategories");
            _logger.info("creating new eventCategory from eventCategoryDto ");
        } else {
            _logger.info("POST /api/import/eventCategories");
            _logger.info("no eventCategoryDto");
        }

        return  Response.ok().entity("not implemented").build();
    }

    @Path("/{eventCategoryId}")
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventCategory(@PathParam("eventCategoryId") int eventCategoryId){

        _logger.info("got eventCategoryId : ["+eventCategoryId+"]");
        // load event category
        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter

        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }

    @Path("/{eventCategoryId}")
    @PUT
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEventCategory(@PathParam("eventCategoryId") int eventCategoryId, EventCategory eventCategoryDto){

        _logger.info("PUT /eventCategories/"+eventCategoryId);
        // load event category
        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter

        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }

    @Path("/{eventCategoryId}")
    @DELETE
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteEventCategory(@PathParam("eventCategoryId") int eventCategoryId){

        _logger.info("DELETE /eventCategories/"+eventCategoryId);
        // load event category
        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter

        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }

    @Path("/{eventCategoryId}/albums")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAlbumsForEventCategory(@PathParam("eventCategoryId") int eventCategoryId){

        // TODO implement

        return  Response.ok().entity("not implemented").build();
    }

    /**
     * This API method will create / update an album for an eventCategory.
     * NOTE: the current implementation only uses 1 album per eventCategory!
     * @param eventCategoryId
     * @param albumDto
     * @return
     */
    @Path("/{eventCategoryId}/albums")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAlbum(@PathParam("eventCategoryId") long eventCategoryId, Album albumDto) {
        _logger.info("POST /api/import/eventCategories/{eventCategoryId}/albums");
        _logger.info("for eventCategoryId : " + eventCategoryId);
        _logger.debug(albumDto);
        SportsAlbum sportsAlbum;
        try {
            EventCategoryDAO dao = new EventCategoryDAO();
            ch.unartig.studioserver.model.EventCategory eventCategory = dao.load(eventCategoryId);
            SportsEvent event = eventCategory.getEvent();
            PhotographerDAO photographerDAO = new PhotographerDAO();
            photographerDAO.load((long) 1);
            // Todo: authenticated user! - hardcoded user with id 1 -> admin user
            Photographer photographer = photographerDAO.load((long) 1);
            String photosS3Uri = albumDto.getPhotosS3Uri();
            // todo : use full s3 path in future - no logic on server side, just take the full s3 uri
            // remove everything at the beginning including 'upload/'
            String s3Path = photosS3Uri.substring(photosS3Uri.lastIndexOf("upload/"));
            _logger.info("s3 path : " + s3Path);
            sportsAlbum = event.createSportsAlbumFromTempPath(eventCategoryId, s3Path, photographer, false, albumDto.isApplyLogoOnFineImages());
        } catch (Throwable e) {
            _logger.error(e);
            return Response.serverError().entity("Error while creating album on the server").build();
        }

        albumDto.setId(sportsAlbum.getGenericLevelId());
        albumDto.setTitle(sportsAlbum.getLongTitle());
        albumDto.setStatus(Album.StatusEnum.IMPORTING);

        // todo : return ID - think about REST endpoint that delivers status information of album (importing, published, ...)
        return Response.ok().entity(albumDto).build();
    }

}
