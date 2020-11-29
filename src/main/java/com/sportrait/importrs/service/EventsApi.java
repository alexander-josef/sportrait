package com.sportrait.importrs.service;

import ch.unartig.controller.Client;
import ch.unartig.studioserver.model.Photographer;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;
import com.sportrait.importrs.Secured;
import com.sportrait.importrs.model.Event;
import com.sportrait.importrs.model.EventCategory;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

// class for the events API - all verbs implemented below
@Path("/events/")
public class EventsApi {
    // inject request context to read out client - client to be put to request context in auth filter
    @Context
    ContainerRequestContext requestContext;
    private final Logger _logger = Logger.getLogger(getClass().getName());


    /**
     * Todo think about visibility - only logged in user probably
     *
     * @return
     */
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response listEvents() {
        _logger.info("GET /events");
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        GenericLevelDAO glDao = new GenericLevelDAO();
        Photographer photographer = client.getPhotographer();
        List<ch.unartig.studioserver.model.SportsEvent> events;
        // store events as a field - to be reused by the event administration window. re-load in every case
        if (photographer.isAdmin()) {
            _logger.debug("Loading events for admin");
            events = glDao.listGenericLevel(ch.unartig.studioserver.model.SportsEvent.class); // events are reloaded - but problems getting albums from cache?
        } else {
            _logger.debug("Loading events for : [" + photographer.getFullName()+"]");
            events = glDao.listEventsWithAlbums(photographer); // why only with albums? --> photographer / user is only connected with album. event no connected with user
        }
        return Response.ok()
                .entity(events
                        .stream()
                        .map(this::convertToEventsDTO)
                        .collect(Collectors.toList()))
                .build();

    }

    private Event convertToEventsDTO(ch.unartig.studioserver.model.SportsEvent event) {
        Event eventDTO = new Event();
        eventDTO.setId(event.getGenericLevelId());
        eventDTO.setTitle(event.getLongTitle());
        eventDTO.setDate(event.getEventDate());
        eventDTO.setNavTitle(event.getNavTitle());
        eventDTO.setDescription(event.getDescription());
        // eventDTO.setZipCode();
        // eventDTO.setCity();
        eventDTO.setOrganizerUrl(event.getWeblink());
        eventDTO.setEventCategories(event.getEventCategories()
                .stream()
                .map(this::convertToEventCategoriesDTO)
                .collect(Collectors.toList())
        );
        eventDTO.setStatus(Event.StatusEnum.ONLINE); // what to do here?

        return eventDTO;
    }

    private EventCategory convertToEventCategoriesDTO(ch.unartig.studioserver.model.EventCategory eventCategory) {
        EventCategory eventCategoryDTO = new EventCategory();
        eventCategoryDTO.setId(eventCategory.getEventCategoryId());
        eventCategoryDTO.setTitle(eventCategory.getTitle());
        eventCategoryDTO.setDescription(eventCategory.getDescription());
        eventCategoryDTO.setStatus(EventCategory.StatusEnum.ONLINE);
        return eventCategoryDTO;
    }

    @POST
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response addEvent(Event eventDto) {
        if (eventDto.getId() == null) {
            _logger.info("POST /api/import/events");
            _logger.info("creating new event from eventDto ");
        }
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        return Response.ok().entity("not implemented - authenticated user : [" + client.getUsername() + "]").build();
    }

    @Path("/{eventId}")
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEvent(@PathParam("eventId") int eventId) {

        _logger.info("GET /events/" + eventId);
        // load event category
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter

        return Response.ok().entity("not implemented - authenticated user : [" + client.getUsername() + "]").build();
    }


    @Path("/{eventId}")
    @PUT
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEvent(@PathParam("eventId") int eventId) {

        _logger.info("PUT /events/" + eventId);
        // load event category
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter

        return Response.ok().entity("not implemented - authenticated user : [" + client.getUsername() + "]").build();
    }


    @Path("/{eventId}")
    @DELETE
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteEvent(@PathParam("eventId") int eventId) {

        _logger.info("DELETE /events/" + eventId);
        // load event category
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter

        return Response.ok().entity("not implemented - authenticated user : [" + client.getUsername() + "]").build();
    }
}


