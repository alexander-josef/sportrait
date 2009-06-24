/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since Sep 22, 2005$
 *
 * Copyright (c) 2005 unartig AG  --  All rights reserved
 *
 * STATUS  :
 *    $Revision$, $State$, $Name$
 *
 *    $Author$, $Locker$
 *    $Date$
 *
 *************************************************
 * $Log$
 * Revision 1.2  2007/03/28 13:52:58  alex
 * edit events page
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.17  2006/03/03 16:54:56  alex
 * minor fixes
 *
 * Revision 1.16  2006/02/16 17:13:46  alex
 * admin interface: deletion of levels works now
 *
 * Revision 1.15  2006/02/15 15:57:03  alex
 * bug [968] fixed. admin interface does that now
 *
 * Revision 1.14  2006/02/13 16:15:28  alex
 * but [968]
 *
 * Revision 1.13  2006/02/10 14:21:49  alex
 * admin tool: edit level partly ...
 *
 * Revision 1.12  2005/11/30 10:47:38  alex
 * bug fixes
 *
 * Revision 1.11  2005/11/19 11:08:20  alex
 * index navigation works, (extended GenericLevel functions)
 * wrong calculation of fixed checkout overview eliminated
 *
 * Revision 1.10  2005/11/08 10:05:02  alex
 * tree items i18n, backend
 *
 * Revision 1.9  2005/11/07 21:57:43  alex
 * admin interface refactored
 *
 * Revision 1.8  2005/11/07 17:38:26  alex
 * admin interface refactored
 *
 * Revision 1.7  2005/11/06 21:43:22  alex
 * overview, admin menu, index-photo upload
 *
 * Revision 1.6  2005/11/05 21:41:58  alex
 * overview und links in tree menu
 *
 * Revision 1.5  2005/10/06 18:14:23  alex
 * saving new tree_items file
 *
 * Revision 1.4  2005/10/06 14:30:09  alex
 * generating the nav tree recursivly works
 *
 * Revision 1.3  2005/10/06 11:06:33  alex
 * generating the nav tree
 *
 * Revision 1.2  2005/10/06 08:54:04  alex
 * cleaning up the model
 *
 * Revision 1.1  2005/09/26 18:37:45  alex
 * *** empty log message ***
 *
 ****************************************************************/
package ch.unartig.u_core.model;

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.u_core.presentation.GenericLevelVisitor;
import ch.unartig.u_core.persistence.DAOs.GenericLevelDAO;
import ch.unartig.u_core.persistence.DAOs.PhotographerDAO;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EventGroup extends GeneratedEventGroup
{
    Logger _logger = Logger.getLogger(getClass().getName());

    public EventGroup()
    {
    }

    public void accept(GenericLevelVisitor visitor)
    {
        try
        {
            visitor.visit(this);
        } catch (UAPersistenceException e)
        {
            _logger.error("visitor threw exception", e);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public EventGroup(String navTitle, String longTitle, String description)
    {
        setNavTitle(navTitle);
        setLongTitle(longTitle);
        setDescription(description);
    }

    public StringBuffer composeTreeItem()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * return a list of events
     *
     * @return list of events
     */
    public List listChildrenForNavTree()
    {
        return new ArrayList(getEvents());
    }

    public List listChildren()
    {
        return new ArrayList(getEvents());
    }

    public Class getParentClazz()
    {
        return Category.class;
    }

    public String getLevelType()
    {
        return "EventGroup";
    }

    /**
     * @param category
     */
    public void setParentLevel(GenericLevel category)
    {
        Category c = (Category) category;
        setCategory(c);
        c.getEventGroups().add(this);
    }

    public GenericLevel getParentLevel()
    {
        return getCategory();
    }

    public String[] getIndexNavEntry()
    {
        return new String[]{getIndexNavLink(), getNavTitle()};
    }

    public String getEventDateDisplay()
    {
        return "";  //To change body of implemented methods use File | Settings | File Templates.
    }


    /**
     * @param langSuffix
     * @return item scope settings
     */
    protected String getItemScopeSettings(String langSuffix)
    {
        String retVal;
        if (getIsPrivate() != null && getIsPrivate().booleanValue())
        {
            retVal = "{'s0':'node_protected','s12':'node_protected'," + "'s8':'node_protected','s4':'node_preotected_selected'," + "'s12':'node_protected_selected'," + "'s64':'node_protected_hover'," + "'s72':'node_protected_hover'," + "'s76':'node_protected_selected_hover'," + "'s4':'node_protected_selected'," + "'s68':'node_protected_selected_hover'}";
        } else
        {
            retVal = "null";
        }

        return retVal;
    }

    public boolean isEventGroupLevel()
    {
        return true;
    }

    /**
     * delete implementation for event group. no action needed; go through all events and delete
     */
    public void deleteLevel() throws UAPersistenceException
    {
        _logger.debug("EventGroup.deleteLevel");
        for (Iterator iterator = getEvents().iterator(); iterator.hasNext();)
        {
            Event event = (Event) iterator.next();
            event.deleteLevel();
        }
    }

    /**
     * Creation method to construct by loaction
     *
     * @param zipCode  Zip Code
     * @param city     City
     * @param category the category as string; is probably not needed here since there is no hierachical relation between the location and the sports-category
     * @return the newly create EventGroup instance
     */
    public static EventGroup constructByLocation(String zipCode, String city, String category)
    {
        EventGroup retVal = new EventGroup();
        retVal.setCity(city);
        retVal.setZipcode(zipCode);
        return retVal;
    }


    /**
     * 
     * @param photographer
     * @return
     */
    public List getEventsWithAlbums(Photographer photographer)
    {
        GenericLevelDAO glDao = new GenericLevelDAO();
        try
        {
            if (!photographer.isAdmin())
            {
                _logger.debug("Albums for non-admin");
                return glDao.listEventsWithAlbums(this, photographer);
            } else
            {
                // if role is admin
                // return all events (with at least one album)
                _logger.debug("All Albums for admin");
                return glDao.listEventsWithAlbums(this);
            }
        } catch (UAPersistenceException e)
        {
            _logger.error("error getting photogpher events", e);
            throw new RuntimeException("Error getting events for photographer",e);
        }
    }

    /**
     * Used in view; be cautious when using this! has the photographer already be loaded?? In the client handling, for example?
     *
     * @param photographerId Photographer Id as String
     * @return a list of Event objects for this photographer
     */
    public List getEventsWithAlbums(String photographerId) throws UAPersistenceException
    {
        PhotographerDAO photographerDao = new PhotographerDAO();
        Photographer photographer = photographerDao.load(new Long(photographerId));
        return getEventsWithAlbums(photographer);
    }


}
