/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 29.03.2007$
 *
 * Copyright (c) 2006 Alexander Josef,unartig AG; All rights reserved
 *
 * STATUS  :
 *    $Revision$, $State$, $Name$
 *
 *    $Author$, $Locker$
 *    $Date$
 *
 *************************************************
 * $Log$
 * Revision 1.3  2007/06/03 21:35:21  alex
 * Bug #1234 : Ordnung eventcategory: wird nun nach als liste gefuehrt, ordnung wird eingehalten
 *
 * Revision 1.2  2007/03/30 20:39:26  alex
 * check in
 *
 * Revision 1.1  2007/03/29 13:33:44  alex
 * working on photo upload
 *
 ****************************************************************/
package ch.unartig.studioserver.beans;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.studioserver.model.SportsEvent;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;

import java.util.Collections;
import java.util.List;

/**
 * Bean used in admin interface; containing upload related data for the view
 */
public class UploadBean
{
    private String eventCategoryId;
    private SportsEvent sportsEvent;


    /**
     * used by view to show eventcategories in case an event has already been selected. or an empty collection
     * @return
     */
    public List getEventCategories()
    {
        return sportsEvent!=null?sportsEvent.getEventCategories(): Collections.EMPTY_LIST;
    }

    /**
     * On the upload page, list the events that need to be visible
     * @return
     */
    public List getEvents()
    {
        GenericLevelDAO glDao = new GenericLevelDAO();
        try
        {
            return glDao.listGenericLevel(SportsEvent.class);
        } catch (UAPersistenceException e)
        {
            throw new RuntimeException("Exception when querying events",e);
        }
    }

    /**
     * Convenience Getter: return ID as String
     * @return Id of Sportsevent as String
     */
    public String getEventId()
    {
        if (sportsEvent != null)
        {
            return sportsEvent.getGenericLevelId().toString();
        } else
        {
            return null;
//            throw new RuntimeException("Sports Event not set! Can not return id");
        }
    }


    public void setSportsEvent(SportsEvent sportsEvent)
    {
        this.sportsEvent = sportsEvent;
    }

    public SportsEvent getSportsEvent()
    {
        return sportsEvent;
    }

    public String getEventCategoryId()
    {
        return eventCategoryId;
    }

    public void setEventCategoryId(String eventCategoryId)
    {
        this.eventCategoryId = eventCategoryId;
    }

    public void setSportsEventById(String eventId)
    {
        GenericLevelDAO glDao = new GenericLevelDAO();
        try
        {
            sportsEvent = (SportsEvent) glDao.load(new Long(eventId), SportsEvent.class);
        } catch (UAPersistenceException e)
        {
            throw new RuntimeException("Error loading Sports event : ",e);
        }
    }
}
