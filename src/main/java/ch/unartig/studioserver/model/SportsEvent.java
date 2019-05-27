/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since 10.03.2006$
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
 * Revision 1.9  2007/06/03 21:35:21  alex
 * Bug #1234 : Ordnung eventcategory: wird nun nach als liste gefuehrt, ordnung wird eingehalten
 *
 * Revision 1.8  2007/05/07 15:32:22  alex
 * id for digital negative, 400 x 600 Foto
 *
 * Revision 1.7  2007/05/03 16:05:14  alex
 * startnummernsuche works
 *
 * Revision 1.6  2007/05/02 14:27:55  alex
 * Uploading refactored, included fine-path server import for sportrait
 *
 * Revision 1.5  2007/04/17 11:03:27  alex
 * dynamic pager added
 *
 * Revision 1.4  2007/03/30 20:39:26  alex
 * check in
 *
 * Revision 1.3  2007/03/28 14:42:52  alex
 * home page most recent events are working
 *
 * Revision 1.2  2007/03/28 13:52:58  alex
 * edit events page
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.1  2006/03/20 15:33:32  alex
 * first check in for new sports album logic and db changes
 *
 ****************************************************************/
package ch.unartig.studioserver.model;

import ch.unartig.controller.Client;
import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.businesslogic.Uploader;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;
import ch.unartig.studioserver.persistence.DAOs.PhotographerDAO;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

/**
 * @author Alexander Josef, 2006
 */

@Entity
@DiscriminatorValue("SPORTSEVENT")
public class SportsEvent extends Event implements java.io.Serializable {

    @Transient
    Logger _logger = Logger.getLogger(getClass().getName());

    @OneToMany(mappedBy = "event",cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<EventCategory> eventCategories = new ArrayList<>(0);

    @OneToMany(mappedBy = "event",cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<EventRunner> eventRunners = new HashSet<EventRunner>(0);

    /**
     * this is called via introspection from the admin action when a new SportsEvent is created.
     * todo: refactor this using a common factory
     *
     * @param navTitle Navigation Title
     * @param longTitle Long title that is displayed
     * @param description A description
     */
    public SportsEvent(String navTitle, String longTitle, String description)
    {
        setNavTitle(navTitle);
        setLongTitle(longTitle);
        setDescription(description);
    }



    public SportsEvent()
    {
    }

    /**
     * from the bulk import function, a columns separated line is passed that needs to be parsed and the sportsevent constructed accordingly
     * <ul>
     * <li>read the location, load an existing one or create the new location
     * <li>read all sportsevent attributes
     * <li>read and create the categories
     * </ul>
     * Example for a line:<br/>
     * <pre>
     * in Klammern die array-indices
     * (0)laufen;(1)27.3.2007;(2)8000;(3)Z�rich;(4)Marathon Z�rich;(5)Am Marathon Z�rich nehmen heute 12'000 Sportlerinnen und Sportler teil. Er stellt f�r die Marathon-Disziplin der prominenteste Anlass in der Schweiz dar\nIm Jahre 1996 standen 100 Sportlerinnen und Sportler zum ersten mal am Start des Z�richer Marathons;(6)http://www.zurichmarathon.ch/;(7+)M 18;W 18;W 20;M 20;M 30;W 30
     * </pre>
     *
     * @param line column separated line that contains the sportsevent information
     * @throws ch.unartig.exceptions.UnartigException
     *
     */
    public SportsEvent(String line) throws UnartigException
    {
        // todo check if event already exists?
        // todo what shall we do if an event exist? ignore? warn? throw exception?
        _logger.debug("line : " + line);
        String[] parts = line.split(";", 8);
        _logger.debug("line parts : " + parts.length);
        _logger.debug("parsing : " + Arrays.toString(parts));
        // parse until the eventcategories:
        try
        {
            setEventDate(simpleFormate.parse(parts[1].trim()));
            setEventLocation(parts[2].trim(), parts[3].trim(), parts[0]);
            setLongTitle(parts[4].trim());
            setNavTitle(parts[4].trim());
            setDescription(parts[5].trim());
            setWeblink(parts[6].trim());

            // and then parse all eventCategories (if length >6, i.e. if they exist); create a set of new eventcategories for each event.
            if (parts.length > 7)
            {
                final String eventCategoriesPart = parts[7];
                setEventCategoriesAsString(eventCategoriesPart);
            }
        } catch (ParseException e)
        {
            _logger.info("Cannot create a sports event, see stack trace", e);
            throw new UnartigException("Cannot create a sports event, see stack trace", e);
        }
    }

    /**
     * given the eventcategories as string with a colon semicolon separted list of categories, add the categories to the event
     * @param categoriesString a semicolon separated string of categories
     */
    public void setEventCategoriesAsString(String categoriesString)
    {
        String[] eventCategories = categoriesString.split(";");
        _logger.debug("event categories : " + Arrays.toString(eventCategories));
        for (String eventCategory : eventCategories)
        {
            _logger.debug("adding : " + eventCategory);
            //noinspection unchecked
            getEventCategories().add(new EventCategory(eventCategory.trim(), this));
        }
    }

    public String getEventCategoriesAsString()
    {
        // DO NOT REMOVE, the ZUL page needs this getter. if necessary, implement.
        return "";
    }


    /**
     * Read the location, load an existing one or create the new location as an EventGroup
     *
     * @param zipCode Zip Code as string
     * @param city City as String
     * @param category The category navlevel property as string (i.e. laufen)
     * @throws ch.unartig.exceptions.UAPersistenceException
     *
     */
    public void setEventLocation(String zipCode, String city, String category) throws UAPersistenceException
    {
        GenericLevelDAO glDao = new GenericLevelDAO();
        EventGroup location = glDao.loadEventGroupByZipCode(zipCode);
        if (location == null)
        {
            location = EventGroup.constructByLocation(zipCode, city, category);
        }
        setEventGroup(location);
        _logger.debug("set location (eventgroup) to : " + getEventGroup());
    }

    /**
     * Overridden for SportsEvent: call SportsEvent Action
     *
     * @return URL for the SportsEvent Action
     */
    public String getIndexNavLink()
    {
        return "/sportsOverview/" + getGenericLevelId().toString() + "/" + getNavTitle() + "/show.html";
    }

    /**
     * Overriden
     *
     * @return true
     */
    public boolean isSportsEventLevel()
    {
        return true;
    }

    /**
     * convenience getter for sport events to get the location
     *
     * @return The city of this sports event
     */
    public String getLocation()
    {
        return getEventGroup().getCity();
    }

    /**
     * convenience getter to return a category of this event by id
     *
     * @param eventCategoryId Id as Long
     * @return Event Category as object
     */
    private EventCategory getEventCategoryById(Long eventCategoryId)
    {
        for (Object o : getEventCategories())
        {
            EventCategory eventCategory = (EventCategory) o;
            if (eventCategory.getEventCategoryId().equals(eventCategoryId))
            {
                return eventCategory;
            }
        }
        return null;
    }


    /**
     * Given a temporary path (or key) with 'fine' images, copy and register the photos with the db.
     * <br/> Uses a new Thread for the import process!
     *
     * @param eventCategoryId Sports Category Id as String as passed from view
     * @param tempFineImageServerPath temp path with high-res images local on the server
     * @param client Client object, containing photographer object
     * @param createThumbDisplay
     * @param applyLogoOnFineImages
     * @return true for success
     * @throws ch.unartig.exceptions.UnartigException
     */
    public boolean createSportsAlbumFromTempPath(Long eventCategoryId, String tempFineImageServerPath, Client client, Boolean createThumbDisplay, boolean applyLogoOnFineImages) throws UnartigException
    {
        SportsAlbum sportsAlbum = getOrCreateSportsAlbumFor(eventCategoryId, client.getPhotographer());
        // giving control to new thread and return.
        Thread uploader = new Uploader(tempFineImageServerPath, sportsAlbum.getGenericLevelId(), createThumbDisplay,applyLogoOnFineImages);
        uploader.start();
        return true;
    }

    /**
     * Given the inputStream from an uploaded Zip archive, unpack, copy and register the photos with the db
     * Import of very big files might cause session expiration --> all params are delivered as ID and reloaded
     * <br/> Uses a new Thread for the import process!
     *  @param eventCategoryId The Id of the category that will be used to create a new album
     * @param inputStream The archive as a stream
     * @param photographerId used to load photographer and add to sportsalbum
     * @param processImages Set true to process the images for thumbnail and display images  @return true for success
     * @param applyLogoOnFineImages Set to true if a logo shall be copied on all fine files
     * @throws ch.unartig.exceptions.UnartigException
     *
     */
    public boolean createSportsAlbumFromZipArchive(Long eventCategoryId, InputStream inputStream, String photographerId, boolean processImages, boolean applyLogoOnFineImages) throws UnartigException
    {
        PhotographerDAO photographerDAO = new PhotographerDAO();
        Photographer photographer = photographerDAO.load(Long.valueOf(photographerId));
        if (photographer ==null)
        {
            _logger.error("Problem importing album: cannot load photographer");
            throw new UnartigException("photographer == null");
        }

        SportsAlbum sportsAlbum = getOrCreateSportsAlbumFor(eventCategoryId, photographer);
        // put the photos from the archive to its correct file storage location: (no temp files)
        // this might take a long time! todo: put in separate thread?
        sportsAlbum.extractPhotosFromArchive(inputStream);

        // giving control to new thread and return.
        Thread uploader = new Uploader(null, sportsAlbum.getGenericLevelId(), processImages, applyLogoOnFineImages);
        _logger.info("Starting Uploader Thread ...");
        uploader.start();
        return true;
    }

    /**
     * Without processing the fine images, import an album into the system based on the already extracted image parameters
     * @param eventCategoryId
     * @param inputStream
     * @param client
     * @param isZipArchive
     * @return
     * @throws UnartigException
     */
    public boolean importAlbumFromImportDataOnly(Long eventCategoryId, InputStream inputStream, Client client, boolean isZipArchive) throws UnartigException
    {
        SportsAlbum sportsAlbum = getOrCreateSportsAlbumFor(eventCategoryId, client.getPhotographer());
        sportsAlbum.registerPhotosFromImportData(inputStream, isZipArchive);
        return true;
    }



    /**
     * @see SportsEvent#getOrCreateSportsAlbumFor(Long,Photographer)
     * @param eventCategoryId
     * @param photographerId
     * @return
     */
    public SportsAlbum getSportsAlbumFor(Long eventCategoryId, Long photographerId)
    {
        PhotographerDAO phDao = new PhotographerDAO();
        SportsAlbum album;
        try
        {
            album = getOrCreateSportsAlbumFor(eventCategoryId, phDao.load(photographerId));
        } catch (UAPersistenceException e)
        {
            throw new RuntimeException("can not get sports album",e);
        }
        return album;
    }

    /**
     * If Album already exists it will be loaded and returned (album defined as set of photos of a category from a photographer)
     * Transactional creation of a sports album if none exists for the given photographer and category.
     *
     *
     * @param eventCategoryId CategoryId as String
     * @param photographer Photographer who owns the new album
     * @return the newly created album
     */
    private SportsAlbum getOrCreateSportsAlbumFor(Long eventCategoryId, Photographer photographer) throws UAPersistenceException
    {
        SportsAlbum sportsAlbum;
        GenericLevelDAO glDao = new GenericLevelDAO();
        EventCategory eventCategory = getEventCategoryById(eventCategoryId);
        try
        {
            sportsAlbum = glDao.getSportsAlbumFor(photographer, eventCategory);
        } catch (UAPersistenceException e)
        {
            throw new RuntimeException("More than one Album for this photographer and category; exiting",e);
        }
        if (sportsAlbum == null)
        {
            // album does not yet exist
            // create a new album

            sportsAlbum = new SportsAlbum(eventCategory.getTitle(), eventCategory.getTitle(), getNavTitle() + "; " + eventCategory.getTitle());
            sportsAlbum.setEvent(this);
            sportsAlbum.setEventCategory(eventCategory);
            sportsAlbum.setPhotographer(photographer);
            try
            {
                HibernateUtil.beginTransaction();
                glDao.saveOrUpdate(sportsAlbum);
                HibernateUtil.commitTransaction();
            }
            catch (UAPersistenceException e)
            {
                _logger.error("Error saving sportsalbum");
                HibernateUtil.rollbackTransaction();
                throw new RuntimeException("Error saving sportsalbum",e);
            }
            finally
            {
//                HibernateUtil.finishTransaction();
            }
        }
        return sportsAlbum;
    }


    /**
     * Return a list with all event-categories that have photos attached
     *
     * @return List of EventCategory s
     */
    public List getEventCategoriesWithPhotos()
    {
        List retVal = new ArrayList(); // todo debug: cached results? probably not, many sql statements issued
        for (Object o : getEventCategories()) // --> eventcategories on event are cached
        {
            EventCategory eventCategory = (EventCategory) o;
            try
            {
                if (eventCategory.hasPublishedPhotos())
                {
                    //noinspection unchecked
                    retVal.add(eventCategory);
                }
            } catch (UnartigException e)
            {
                _logger.error("Cannot count photos of eventcategory",e);
            }
        }
        return retVal;
    }

    /**
     * convenience getter that is used in view
     * @return True or False
     */
    public Boolean getHasPhotos()
    {
        return getEventCategoriesWithPhotos().size()>0;
    }


    public List<EventCategory> getEventCategories() {
        return this.eventCategories;
    }

    public void setEventCategories(List<EventCategory> eventCategories) {
        this.eventCategories = eventCategories;
    }

    public Set<EventRunner> getEventRunners() {
        return this.eventRunners;
    }

    public void setEventRunners(Set<EventRunner> eventRunners) {
        this.eventRunners = eventRunners;
    }
}
