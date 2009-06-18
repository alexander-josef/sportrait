/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 15.08.2007$
 *
 * Copyright (c) 2007 Alexander Josef,unartig AG; All rights reserved
 *
 * STATUS  :
 *    $Revision$, $State$, $Name$
 *
 *    $Author$, $Locker$
 *    $Date$
 *
 *************************************************
 * $Log$
 ****************************************************************/
package ch.unartig.studioserver.xmlRpc;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.studioserver.model.Album;
import ch.unartig.studioserver.model.Event;
import ch.unartig.studioserver.model.EventCategory;
import ch.unartig.studioserver.model.SportsEvent;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class is used by XMLRPC calls to provide sportrait-administration services to the client program
 */
public class AdminServices
{
    String[] mockEvents = {"Event1","Event2","Sola","Marathon"};

    /**
     * Get the albums for a photographer and return them as a map with the the id as key and the long name as value
     * @param photographerId Id used to look up photographer
     * @param password photographer password
     * @return A map containing the album-ids and the titles, both as strings
     */
    public Map getAlbums(String photographerId, String password)
    {
        List albums;
        Map<String, String> retVal = new HashMap<String, String>();
        GenericLevelDAO glDao = new GenericLevelDAO();
        try
        {
            albums = glDao.listAlbumsForPhotographer(new Long(photographerId));
        } catch (UAPersistenceException e)
        {
            throw new RuntimeException("Can not load albums",e);
        }

        for (Object album1 : albums)
        {
            Album album = (Album) album1;
            retVal.put(album.getGenericLevelId().toString(), album.getEvent().getLongTitle() + " : " + album.getLongTitle());
        }

        return retVal;
    }

    /**
     * List all events as a map in the form eventId:eventTitle
     * Used by the upload client
     * @param password not here
     * @param photographerId not here
     * @return A map containing id as key and title as value
     */
    public Map getEvents(String photographerId,String password)
    {
        GenericLevelDAO glDao = new GenericLevelDAO();
        // Map with <eventId,eventTitle>
        Map<String, String> retVal = new HashMap<String, String>();
        List events;

        try
        {
            events = glDao.listGenericLevel(Event.class);
        } catch (UAPersistenceException e)
        {
            throw new RuntimeException("Error querying the db for events");
        }

        for (Object eventObject : events)
        {
            Event event= (Event) eventObject;
            retVal.put(event.getGenericLevelId().toString(), event.getLongTitle());
        }

        return retVal;
    }

    /**
     * List all event categories for a given event id in a map with the id and the title; used by the upload client once an event has been selected
     * @param password todo handle somewhere else
     * @param photographerId todo handle somewhere else
     * @param eventId Id of the event to fetch categories from
     * @return A map containing id as key and title as value
     */
    public Map getEventCategories(String photographerId,String password,String eventId)
    {
        GenericLevelDAO glDao = new GenericLevelDAO();
        Map<String, String> retVal = new HashMap<String, String>();
        SportsEvent event;
        try
        {
            event = (SportsEvent)glDao.load(new Long(eventId),SportsEvent.class);
        } catch (UAPersistenceException e)
        {
            throw new RuntimeException("Error loading Event: ",e);
        } catch (Exception e)
        {
            throw new RuntimeException("Unexpected Error : ",e);
        }


        List categotyList = event.getEventCategories();
        if (categotyList!=null)
        {
            for (Object eventObject : categotyList)
            {
                EventCategory eventCategory= (EventCategory) eventObject;
                retVal.put(eventCategory.getEventCategoryId().toString(), eventCategory.getTitle());
            }
        }

        return retVal;
    }
}