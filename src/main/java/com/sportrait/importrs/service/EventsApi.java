package com.sportrait.importrs.service;

import ch.unartig.controller.Client;
import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.studioserver.model.GenericLevel;
import ch.unartig.studioserver.model.Photographer;
import ch.unartig.studioserver.model.SportsEvent;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
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
            _logger.debug("Loading events for : [" + photographer.getFullName() + "]");
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
                .map(eventCategory -> convertToEventCategoriesDTO(eventCategory, eventDTO))
                .collect(Collectors.toList())
        );
        eventDTO.setStatus(Event.StatusEnum.ONLINE); // what to do here?

        return eventDTO;
    }

    private EventCategory convertToEventCategoriesDTO(ch.unartig.studioserver.model.EventCategory eventCategory, Event eventDTO) {
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
        GenericLevelDAO glDao = new GenericLevelDAO();
        // todo : administrator of an event?? = creating Photographer?
        SportsEvent sportsEvent = convertFromSportsEventDTO(eventDto);
        _logger.debug("going to persist new SportsEvent ");
        glDao.saveOrUpdate(sportsEvent);
        // commit explicitly?
        HibernateUtil.commitTransaction(); // needed! do not deprecate commits ...
        _logger.info("Commmited new event to DB");
        // converting model event to DTO as return value (to get newly created IDs included for response):
        return Response.ok().entity(convertToEventsDTO(sportsEvent)).build();
    }

    /**
     * DTO -> Model Transformer
     * @param eventDTO
     * @return
     */
    private ch.unartig.studioserver.model.SportsEvent convertFromSportsEventDTO(Event eventDTO) {
        ch.unartig.studioserver.model.SportsEvent event = new ch.unartig.studioserver.model.SportsEvent(eventDTO.getNavTitle(), eventDTO.getTitle(), eventDTO.getDescription());

        event.setEventDate(eventDTO.getDate());
        event.setWeblink(eventDTO.getOrganizerUrl());
        event.setEventCategories(eventDTO.getEventCategories()
                .stream()
                .map(eventCategoryDTO -> convertFromEventCategoryDTO(eventCategoryDTO,event))
                .collect(Collectors.toList()));
        event.setEventLocation(eventDTO.getZipCode(), eventDTO.getCity(), ""); // category (this is not eventCategory!) not needed

        return event;
    }

    /**
     * DTO -> Model Transformer
     * @param eventCategoryDTO
     * @param event
     * @return
     */
    private ch.unartig.studioserver.model.EventCategory convertFromEventCategoryDTO(EventCategory eventCategoryDTO, SportsEvent event) {
        ch.unartig.studioserver.model.EventCategory eventCategory =
                new ch.unartig.studioserver.model.EventCategory(eventCategoryDTO.getTitle(),event);
        eventCategory.setDescription(eventCategoryDTO.getDescription());
        // eventCategoryDTO.getStatus(); // ??
        return eventCategory;
    }

    @Path("/{eventId}")
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEvent(@PathParam("eventId") long eventId) {
        _logger.info("GET /events/" + eventId);
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        _logger.info("authenticated user : [" + client.getUsername() + "]");
        GenericLevelDAO glDao = new GenericLevelDAO();
        SportsEvent event = glDao.get(eventId, SportsEvent.class);
        if (event != null) {
            return Response.ok()
                    .entity(convertToEventsDTO(event))
                    .build();
        } else {
            return Response.status(404,"Event not found").build();
        }
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
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        _logger.info("authenticated user : [" + client.getUsername() + "]");
        GenericLevelDAO glDao = new GenericLevelDAO();
        GenericLevel level = null;
        try {
            level = glDao.get((long) eventId);
            if (level==null) {
                return Response.status(404,"Ressource not found").build();
            }
        } catch (Exception e) {
            return Response.status(500,"Something went wrong ...").build();
        }

        _logger.info("... of type :" + level.getLevelType());
        if (level.isSportsEventLevel()) {
            HibernateUtil.beginTransaction();
            try {
                level.deleteLevel(); // call specific implementation of delete method for this level
                glDao.delete(level.getGenericLevelId());
                HibernateUtil.commitTransaction();
            } catch (UAPersistenceException e) {
                HibernateUtil.rollbackTransaction();
                throw new UAPersistenceException("rolling back, cannot delete Level");
            } finally {
                HibernateUtil.finishTransaction();
            }
            _logger.info("done deleting level : " + eventId);
            return Response.ok().entity("event deleted - authenticated user : [" + client.getUsername() + "]").build();
        }
        else {
            return Response.status(405,"given eventId does not identify an event - method not allowed").build();
        }
    }


    @Path("/{eventId}/eventCategories")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addEventCategory(EventCategory eventCategoryDto, @PathParam("eventId") long eventId){
        GenericLevelDAO glDao = new GenericLevelDAO();
        if (eventCategoryDto.getId()!=null) {
            _logger.info("POST /api/import/events/{eventId}/eventCategories");
            _logger.info("creating new eventCategory from eventCategoryDto ");
        } else {
            _logger.info("POST /api/import/events/{eventId}/eventCategories");
            _logger.info("no eventCategoryDto");
        }

        SportsEvent event = glDao.get(eventId,SportsEvent.class);
        if (event != null) {
            event.getEventCategories().add(convertFromEventCategoryDTO(eventCategoryDto,event));
            glDao.saveOrUpdate(event);
            HibernateUtil.commitTransaction();
            _logger.info("committed new eventCategory for event ["+eventId+"] to DB");
        } else {
            return Response.status(404,"no event ressource identified by given eventId").build();
        }

        return  Response.ok().entity(convertToEventsDTO(event)).build();
    }
}
