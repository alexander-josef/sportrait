package com.sportrait.importrs.service;

import ch.unartig.controller.Client;
import ch.unartig.studioserver.model.Photographer;
import ch.unartig.studioserver.model.SportsAlbum;
import ch.unartig.studioserver.model.SportsEvent;
import ch.unartig.studioserver.persistence.DAOs.EventCategoryDAO;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;
import ch.unartig.studioserver.persistence.DAOs.PhotographerDAO;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import com.sportrait.importrs.Secured;
import com.sportrait.importrs.model.Album;
import com.sportrait.importrs.model.EventCategory;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

// class for the eventCategories API - all verbs implemented below
@Path("/eventCategories/")
public class EventCategoriesApi {
    // inject request context to read out client - client to be put to request context in auth filter
    @Context
    ContainerRequestContext requestContext;
    private final Logger _logger = Logger.getLogger(getClass().getName());


    @Path("/{eventCategoryId}")
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventCategory(@PathParam("eventCategoryId") int eventCategoryId) {
        _logger.info("got eventCategoryId : [" + eventCategoryId + "]");
        EventCategoryDAO eventCategoryDAO = new EventCategoryDAO();
        // load event category
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter

        ch.unartig.studioserver.model.EventCategory eventCategory = eventCategoryDAO.getEventCategory(eventCategoryId);
        if (eventCategory != null) {
            return Response.ok().entity(convertToEventCategoryDTO(eventCategory)).build();
        } else {
            return Response.status(403, "eventCategory not found").build();
        }
    }


    static EventCategory convertToEventCategoryDTO(ch.unartig.studioserver.model.EventCategory eventCategory) {
        EventCategory eventCategoryDTO = new EventCategory();
        eventCategoryDTO.setId(eventCategory.getEventCategoryId());
        eventCategoryDTO.setTitle(eventCategory.getTitle());
        eventCategoryDTO.setDescription(eventCategory.getTitle());
        eventCategoryDTO.setStatus(eventCategory.hasPublishedPhotos() ? EventCategory.StatusEnum.PUBLISHED : EventCategory.StatusEnum.EMTPY);
        return eventCategoryDTO;
    }

    @Path("/{eventCategoryId}")
    @PUT
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEventCategory(@PathParam("eventCategoryId") int eventCategoryId, EventCategory eventCategoryDto) {

        _logger.info("PUT /eventCategories/" + eventCategoryId);
        // load event category
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter

        return Response.ok().entity("not implemented - authenticated user : [" + client.getUsername() + "]").build();
    }

    @Path("/{eventCategoryId}")
    @DELETE
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteEventCategory(@PathParam("eventCategoryId") long eventCategoryId) {
        EventCategoryDAO eventCategoryDAO = new EventCategoryDAO();
        GenericLevelDAO genericLevelDAO = new GenericLevelDAO();
        _logger.info("DELETE /eventCategories/" + eventCategoryId);
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        // load event category - check for albums
        ch.unartig.studioserver.model.EventCategory category = eventCategoryDAO.load(eventCategoryId);
        if (category == null) {
            return Response.status(403, "eventCategory not found").build();
        }
        int numberOfAlbums = category.getAlbums().size();
        if (numberOfAlbums != 0) {
            return Response.status(404, "EventCategory still contains [" + numberOfAlbums + "] Album(s). Delete albums first.").build();
            // todo : extend with a parameter "force=true" to also delete albums
        } else {
            // event must also be updated and saved since it has the eventCategories as an indexed collection
            // (think about the overhead in case this operation needs to be more efficient)
            // eventCategory will be deleted as a cascaded operation from saving the event:
            // eventCategoryDAO.delete(category);
            SportsEvent event = category.getEvent();
            event.getEventCategories().remove(category);
            genericLevelDAO.saveOrUpdate(event);
            HibernateUtil.commitTransaction();

            return Response.noContent().build();
        }


    }

    @Path("/{eventCategoryId}/albums")
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAlbumsForEventCategory(@PathParam("eventCategoryId") long eventCategoryId) {

        _logger.info("GET /eventCategories/{eventCategoryId}/albums" + eventCategoryId);
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        EventCategoryDAO dao = new EventCategoryDAO();
        ch.unartig.studioserver.model.EventCategory eventCategory = dao.getEventCategory(eventCategoryId);

        if (eventCategory == null) {
            return Response.status(403,"Event Category does not exist").build();
        }

        return Response.ok().entity(eventCategory.getAlbums()
                .stream()
                .map(AlbumsApi::convertToAlbumDTO)
                .collect(Collectors.toList()))
                .build();
    }

    /**
     * This API method will create / update an album for an eventCategory.
     * NOTE: the current implementation only uses 1 album per eventCategory!
     *
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
