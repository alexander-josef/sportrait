/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 08.10.2007$
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
 ****************************************************************/
package ch.unartig.sportrait.zk.windows;

import ch.unartig.controller.Client;
import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.sportrait.zk.components.ZAlbumComponent;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.model.*;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;
import ch.unartig.studioserver.persistence.DAOs.PhotographerDAO;
import ch.unartig.studioserver.persistence.DAOs.UserProfileDAO;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * ZK Window class for the administration tab window
 */
public class SportraitAdministrationWindow extends Window
{
    private Logger _logger = Logger.getLogger(getClass().getName());

    private Client client;
    private Listbox albumListbox;
    private Div albumListDiv;
    private Div albumEditDiv;
    private ZAlbumComponent zAlbum; // custom album component


    private UserProfile userProfile;
    private Photographer photographer; // the logged in photographer
    private Photographer newPhotographer; // used to create a new photographer profile by the admin

    private SportsEvent newEvent; // the empty, new event on this admin window. will be persisted to the db
    private String eventZipCode;
    private String eventCity;
    private String eventCategory;
    private Tabbox tabbox;
    private List<SportsEvent> events; // events relevant for this administration window - all events in case of a global admin, or only the photographer's events

    public SportraitAdministrationWindow()
    {
        this.client = (Client) Executions.getCurrent().getDesktop().getSession().getAttribute(Registry._SESSION_CLIENT_NAME);
        photographer = client.getPhotographer();
        userProfile = photographer.getUserProfile();
//        create an instance for a new sportsEvent
        newEvent = new SportsEvent();

        GenericLevelDAO glDao = new GenericLevelDAO();
        _logger.debug("Loading events : " + events);
        // store events as a field - to be reused by the event administration window. re-load in every case
        if (photographer.isAdmin()) {
            //noinspection unchecked
            setEvents(glDao.listGenericLevel(SportsEvent.class)); // events are reloaded - but problems getting albums from cache?
        } else {
            setEvents(glDao.listEventsWithAlbums(photographer));
        }



    }

    /**
     */
    public void onCreate()
    {
        _logger.debug("Creating admin window ... onCreate");
        albumListbox = (Listbox) Path.getComponent("/sportraitAdministrationWindow/photographerAlbums");
        albumListDiv = (Div) Path.getComponent("/sportraitAdministrationWindow/albumListDiv");
        albumEditDiv = (Div) Path.getComponent("/sportraitAdministrationWindow/albumEditDiv");
        tabbox = (Tabbox) getFellow("tbox");


        zAlbum = (ZAlbumComponent) Path.getComponent("/sportraitAdministrationWindow/zAlbumComponent");
        zAlbum.setClient(client);
//        page = Executions.getCurrent().getDesktop().getPage("sportraitAdministrationWindow");
        String selectedTab = Executions.getCurrent().getParameter("tab");

        _logger.debug("selectedTab = " + selectedTab);
        if (selectedTab != null && getFellow(selectedTab) != null && getFellow(selectedTab) instanceof Tab)
        {
            tabbox.setSelectedTab((Tab) getFellow(selectedTab));
        }
        getPage();
        createAlbumListbox();
    }

    /**
     * create the grid and the entries with all applicable albums
     * <h3>check history of this method on case grouping by event groups is necessary again</h3>
     * @throws UAPersistenceException
     */
    private void createAlbumListbox()
    {
        // first clear the listbox:
        albumListbox.getItems().clear();

        List<SportsEvent> localEvents;
        localEvents = events;

        _logger.debug("photographer = " + photographer);
        if (localEvents != null)
        {
            _logger.debug("number of events : " + localEvents.size());
            for (Event eachEvent : localEvents)
            {
                // this can be problematic - shouldn't we list the event categories?
                // Or at least indicate which categegory is associated with an album
                // having albums here might be a good and flexible option - it's the photographer's view, not the consumer's view
                _logger.debug("Loading albums for event (in case of admin user, all albums will be loaded)");
                List<Album> albums = eachEvent.getPhotographerAlbums(photographer);
                _logger.debug("number of albums: " + albums.size());
                if (albums.size()>0) {
                    try
                    {
                        _logger.debug("adding event row for event["+eachEvent.getGenericLevelId()+"]");
                        appendEventRow(eachEvent);

                        for (Album album : albums)
                        {
                            _logger.debug("Loading album [" + album.getGenericLevelId().toString() + "]");
                            appendAlbumRow(album);
                        }
                    } catch (Exception e)
                    {

                        _logger.info("Unknown problem that occurs with loading albums for an event .... ", e);
                    }
                }

            }
        } else
        {
            _logger.info("no evens retrieved for photographer [" + photographer.getPhotographerId()+ "]");
            // _logger.info("null events for eventGroup : [" + eventGroup + "]");
        }
        // }

    }

    private void appendAlbumRow(final Album album)
    {
        Listitem albumItem;
        albumItem = new Listitem("-  " + album.getLongTitle() + " (Fotos:" + album.getNumberOfPhotos() + ")", album);
        if (album.getPublish())
        {
            albumItem.setSclass("published");
        } else
        {
            albumItem.setSclass("unpublished");
        }
        // add anonymous inner for event listening
        albumItem.addEventListener(Events.ON_CLICK, new EventListener()
        {
            /**
             * On click load the album edit screen.
             * Use the album of this method to display the album to edit
             * @param event fired event
             * @throws Exception
             */
            public void onEvent(org.zkoss.zk.ui.event.Event event) throws Exception
            {
                // adjust the visible divs, hide the list, show the edit screen:
                // todo : why hand over id here? will lead to re-load of album and event and cause non-unique object exception when saving events
                zAlbum.renderAlbumConfiguration(album);
                albumListDiv.setVisible(false);
                albumEditDiv.setVisible(true);
            }
        });
        albumListbox.appendChild(albumItem);
    }


    private void appendEventRow(Event event)
    {
        Listitem eventItem = new Listitem(event.getEventDateDisplay() + "," + event.getEventGroup().getCity() + "," + event.getLongTitle(), event);
        // todo : this doesn't work .... check html code to see what's wrong ...
        eventItem.setStyle("font-size:1.5em;font-weight:bold");
        albumListbox.appendChild(eventItem);
    }

    public void saveUserProfile() throws UAPersistenceException
    {


        UserProfileDAO userProfileDao = new UserProfileDAO();

        _logger.debug("SportraitAdministrationWindow.save");
        _logger.debug("userProfile.getUserProfileId().getClass().getName() = " + userProfile.getUserProfileId().getClass().getName());
        _logger.debug("userProfile = " + userProfile);
        _logger.debug("userProfile.getFirstName() = " + userProfile.getFirstName());
        _logger.debug("client.getUserProfile().getFirstName() = " + client.getUserProfile().getFirstName());
        try
        {
            _logger.debug("saving userprofile .....");
            // merge ??
            userProfileDao.saveOrUpdate(userProfile);
        } catch (HibernateException e)
        {
            _logger.error("Error saving userProfile, rolling back", e);
            HibernateUtil.rollbackTransaction();
            throw new RuntimeException("Can not save user Profile", e);

        } catch (Exception e)
        {
            _logger.error("unknown exception", e);
        }
    }


    /**
     * The user pressed the bulk-upload button.
     */
    public void bulkUploadEvents()
    {
        try
        {
            Media media = Fileupload.get();

            if (media != null)
            {
                // charset : ISO-8859-1, UTF-8 ?
                BufferedReader in;
                if (!media.isBinary())
                {
                    in = new BufferedReader(media.getReaderData());
                } else
                {
                    throw new RuntimeException("Expected file type is text. Found binary file, stopping.");
                }
                bulkImportFromStream(in);
            }

        } catch (IOException e)
        {
            _logger.error("Error parsing event line", e);
            throw new RuntimeException("Error reading line for bulk upload", e);

        }
    }

    private void bulkImportFromStream(BufferedReader in) throws IOException
    {
        String line;
        //read line by line except for comments:
        while ((line = in.readLine()) != null)
        {
            _logger.debug("parsing line as ISO-8859-1 : " + line);
            // only process non-comment lines:
            if (!line.startsWith("#"))
            {
                _logger.debug("processing line ....");
                GenericLevelDAO glDao = new GenericLevelDAO();
                HibernateUtil.beginTransaction();
                try
                {
                    SportsEvent sportsEvent = new SportsEvent(line);
                    glDao.saveOrUpdate(sportsEvent);
                    HibernateUtil.commitTransaction();
                } catch (UnartigException e)
                {
                    HibernateUtil.rollbackTransaction();
                    _logger.error("Cannot save Sports album", e);
                    throw new UAPersistenceException(e);
                }

            }
        }
        in.close();
    }

    /**
     * User filled in the new Event screen and pressed save - eventcategories will be saved as well
     */
    public void createNewSportsEvent()
    {
        GenericLevelDAO glDao = new GenericLevelDAO();
        newEvent.setEventLocation(eventZipCode, eventCity, eventCategory);
        _logger.debug("going to persist new SportsEvent from admin screen:" + newEvent.toString());
        glDao.saveOrUpdate(newEvent);
        HibernateUtil.commitTransaction();
        _logger.info("Commmited new event to DB");

        // reload the main page:
        Executions.sendRedirect("main-zul.html?tab=tab4");
    }



    /**
     * For user administration, return a list of all users
     *
     * @return If the current user has admin rights return all users
     */
    public List getAllPhotographers()
    {
        _logger.debug("calling getAllPhotographers()");
        if (client.isValid())
        {
            _logger.debug("trying to read all photographers ..");
            PhotographerDAO photographerDao = new PhotographerDAO();
            List photographers = null;
            try {
                photographers = photographerDao.list();
            } catch (UAPersistenceException e) {
                _logger.error("Error retrieving photographers",e);
            }
            _logger.debug("Returning list of photographers from DAO : " + photographers);
            return photographers;
        } else
        {
            _logger.info("User without admin rights wants to use user administration!");
            throw new RuntimeException("Current User has no admin rights!");
        }

    }

    /**
     * Return albums for this photographer
     *
     * @return List of albums
     */
    public List<Album> getPhotographerAlbums()
    {
        final List<Album> albums;
        GenericLevelDAO glDao = new GenericLevelDAO();
        albums = glDao.listAlbumsForPhotographer(photographer.getPhotographerId());
        return albums;
    }



    /**
     *
     */
    public void showList()
    {

        HibernateUtil.commitTransaction();
        HibernateUtil.beginTransaction();
        createAlbumListbox();
        albumEditDiv.setVisible(false);
        albumListDiv.setVisible(true);
    }


    /**
     * The user wants to reset the values on the page to the persisted information.
     */
    public void reset()
    {
        Executions.sendRedirect("/photographer/main-zul.html");
    }


    // getters, setters:
    ////////////////////
    public Client getClient()
    {
        return client;
    }

    public void setClient(Client client)
    {
        this.client = client;
    }

    public UserProfile getUserProfile()
    {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile)
    {
        this.userProfile = userProfile;
    }

    public SportsEvent getNewEvent()
    {
        return newEvent;
    }

    public void setNewEvent(SportsEvent newEvent)
    {
        this.newEvent = newEvent;
    }

    public String getEventZipCode()
    {
        return eventZipCode;
    }

    public void setEventZipCode(String eventZipCode)
    {
        this.eventZipCode = eventZipCode;
    }

    public String getEventCity()
    {
        return eventCity;
    }

    public void setEventCity(String eventCity)
    {
        this.eventCity = eventCity;
    }

    public String getEventCategory()
    {
        return eventCategory;
    }

    public void setEventCategory(String eventCategory)
    {
        this.eventCategory = eventCategory;
    }

    public Photographer getNewPhotographer()
    {
        return newPhotographer;
    }

    public void setNewPhotographer(Photographer newPhotographer)
    {
        this.newPhotographer = newPhotographer;
    }

    public Tabbox getTabbox()
    {
        return tabbox;
    }


    public List<SportsEvent> getEvents() {
        return events;
    }

    public void setEvents(List<SportsEvent> events) {
        this.events = events;
    }
}
