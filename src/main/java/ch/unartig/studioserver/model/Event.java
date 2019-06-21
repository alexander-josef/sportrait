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
 * Revision 1.3  2007/03/28 13:52:58  alex
 * edit events page
 *
 * Revision 1.2  2007/03/27 16:39:17  alex
 * refactored studioalbum into album
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.2  2007/03/14 02:41:01  alex
 * initial checkin
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.16  2006/12/05 22:51:56  alex
 * album kann jetzt freigeschaltet werden oder geschlossen sein
 *
 * Revision 1.15  2006/03/20 15:33:32  alex
 * first check in for new sports album logic and db changes
 *
 * Revision 1.14  2006/03/03 16:54:56  alex
 * minor fixes
 *
 * Revision 1.13  2006/02/16 17:13:46  alex
 * admin interface: deletion of levels works now
 *
 * Revision 1.12  2006/02/15 15:57:03  alex
 * bug [968] fixed. admin interface does that now
 *
 * Revision 1.11  2006/02/13 16:15:28  alex
 * but [968]
 *
 * Revision 1.10  2006/02/10 14:21:49  alex
 * admin tool: edit level partly ...
 *
 * Revision 1.9  2006/01/11 20:40:53  alex
 * level update form works
 *
 * Revision 1.8  2005/11/30 10:47:38  alex
 * bug fixes
 *
 * Revision 1.7  2005/11/19 11:08:20  alex
 * index navigation works, (extended GenericLevel functions)
 * wrong calculation of fixed checkout overview eliminated
 *
 * Revision 1.6  2005/11/08 10:05:02  alex
 * tree items i18n, backend
 *
 * Revision 1.5  2005/11/07 21:57:43  alex
 * admin interface refactored
 *
 * Revision 1.4  2005/11/07 17:38:26  alex
 * admin interface refactored
 *
 * Revision 1.3  2005/11/05 21:41:58  alex
 * overview und links in tree menu
 *
 * Revision 1.6  2005/10/06 18:14:23  alex
 * saving new tree_items file
 *
 * Revision 1.5  2005/10/06 14:30:09  alex
 * generating the nav tree recursivly works
 *
 * Revision 1.4  2005/10/06 11:06:33  alex
 * generating the nav tree
 *
 * Revision 1.3  2005/10/06 08:54:04  alex
 * cleaning up the model
 *
 * Revision 1.2  2005/10/04 11:36:48  alex
 * level imports
 *
 * Revision 1.1  2005/09/26 18:37:45  alex
 * *** empty log message ***
 *
 ****************************************************************/
package ch.unartig.studioserver.model;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.businesslogic.GenericLevelVisitor;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;
import ch.unartig.studioserver.persistence.DAOs.PhotographerDAO;
import org.apache.log4j.Logger;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Event class. extends generated class that represents persistent state
 * z.B. Sola 2015
 *
 * @author Alexander Josef,2005-2006
 */
@Entity
@DiscriminatorValue("EVENT")
public class Event extends GenericLevel implements java.io.Serializable {


    @Transient
    SimpleDateFormat simpleFormate = new SimpleDateFormat("dd.MM.yyyy");

    @Transient
    Logger _logger = Logger.getLogger(getClass().getName());

    @Column(columnDefinition = "DATE")
    private Date eventDate;

    private String weblink;

    /**
     * no cascade for event group here anymore after hibernate 5 migration - but exists in older mapping
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventgroupid")
    private EventGroup eventGroup;

    @OneToMany(mappedBy = "event",cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @OrderBy("genericLevelId")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Album> albums = new HashSet<>(0);


    public Event()
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
            e.printStackTrace();
        }
    }

    /**
     * Constructor is called via inspection
     * todo: replace with factory method
     * todo: still used?
     *
     * @param navTitle
     * @param longTitle
     * @param description
     */
    public Event(String navTitle, String longTitle, String description)
    {
        setNavTitle(navTitle);
        setLongTitle(longTitle);
        setDescription(description);
    }




    /**
     * concrete implementation
     *
     * @return all studio albums and albumss ???
     */
    public List listChildren()
    {
        return new ArrayList(getAlbums());
    }

    public Class getParentClazz()
    {
        return EventGroup.class;
    }

    public String getLevelType()
    {
        return "Event";
    }


    /**
     * add a concrete parent eventgroup to event
     *
     * @param eventGroup
     */
    public void setParentLevel(GenericLevel eventGroup) throws UnartigException
    {
        try
        {
            EventGroup eg = (EventGroup) eventGroup;
            setEventGroup(eg);
            eg.getEvents().add(this);
        } catch (Throwable e)
        {
            throw new UnartigException(e);
        }
    }

    public GenericLevel getParentLevel()
    {
        return getEventGroup();
    }

    public String[] getIndexNavEntry()
    {
        return new String[]{getIndexNavLink(), getNavTitle()};
    }

    public String getEventDateDisplay()
    {
        return simpleFormate.format(getEventDate());
    }

    /**
     * Helper to return the year of the event
     * @return year from eventDate
     */
    public int getEventDateYear()
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getEventDate());

        return cal.get(Calendar.YEAR);
    }


    /**
     * overriden from generic level. only event has date at the time being.
     *
     * @param eventDate
     */
    public void setEventDate(Date eventDate)
    {
        this.eventDate = eventDate;
    }

    /**
     * @param eventDateDisplay
     */
    public void setEventDateDisplay(String eventDateDisplay)
    {
        if (eventDateDisplay == null)
        {
            setEventDate(null);
        } else
        {
            try
            {
                setEventDate(simpleFormate.parse(eventDateDisplay));
            } catch (ParseException e)
            {
                _logger.error("Cannot parse event date with simple format");
                e.printStackTrace();
            }
        }
    }

    public boolean isEventLevel()
    {
        return true;
    }

    /**
     * delete implementation for Event. no action needed for event; go through all studio albums and delete
     */
    public void deleteLevel() throws UAPersistenceException
    {
        _logger.debug("Event.deleteLevel");
        for (Object o : getAlbums())
        {
            Album album = (Album) o;
            album.deleteLevel();
        }
    }

    
    public List getSportsAlbums() throws UAPersistenceException
    {
        GenericLevelDAO glDao = new GenericLevelDAO();
        return glDao.getSportsAlbumsFor(this);
    }

    /**
     * Return all Albums for this photographer; used in view; be cautious! is the photographer already loaded into the session???
     * Admin users will get all albums regardless of their photographer id
     * @param photographerId Id as String of the Photographer
     * @return List of Photographer s
     */
    public List getPhotographerAlbums(String photographerId) throws UAPersistenceException
    {
        PhotographerDAO photographerDao = new PhotographerDAO();
        Photographer photographer = photographerDao.load(new Long(photographerId));
        return getPhotographerAlbums(photographer);
   }

    public List<Album> getPhotographerAlbums(Photographer photographer)
    {
        GenericLevelDAO glDao = new GenericLevelDAO();
        try
        {
            if (!photographer.isAdmin())
            {
                _logger.debug("loading albums only for photographer["+photographer.getPhotographerId()+"]");
                return glDao.listAlbumsForPhotographer(this, photographer);
            } else
            {
                _logger.debug("loading all albums for admin");
                return glDao.listAlbumsForPhotographer(this);
            }
        } catch (UAPersistenceException e)
        {
            _logger.error("error getting photographer albums",e);
            throw new RuntimeException("error getting photographer albums",e);
        }
     }

    public Date getEventDate() {
        return this.eventDate;
    }

    public String getWeblink() {
        return this.weblink;
    }

    public void setWeblink(String weblink) {
        this.weblink = weblink;
    }

    public EventGroup getEventGroup() {
        return this.eventGroup;
    }

    public void setEventGroup(EventGroup eventGroup) {
        this.eventGroup = eventGroup;
    }

    public Set<Album> getAlbums() {
        return this.albums;
    }

    public void setAlbums(Set<Album> albums) {
        this.albums = albums;
    }

    /**
     * toString
     * @return String
     */
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("weblink").append("='").append(getWeblink()).append("' ");
      buffer.append("eventGroup").append("='").append(getEventGroup()).append("' ");
      buffer.append("]");

      return buffer.toString();
     }
}
