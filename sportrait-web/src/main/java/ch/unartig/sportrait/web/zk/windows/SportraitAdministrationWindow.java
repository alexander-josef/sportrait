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
package ch.unartig.sportrait.web.zk.windows;

import ch.unartig.u_core.controller.Client;
import ch.unartig.u_core.Registry;
import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.u_core.exceptions.UnartigException;
import ch.unartig.sportrait.web.zk.components.ZAlbumComponent;
import ch.unartig.u_core.persistence.DAOs.GenericLevelDAO;
import ch.unartig.u_core.persistence.DAOs.PhotographerDAO;
import ch.unartig.u_core.persistence.DAOs.UserProfileDAO;
import ch.unartig.u_core.persistence.util.HibernateUtil;
import ch.unartig.studioserver.model.*;
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

/**
 * ZK Window class for the administration tab window
 */
public class SportraitAdministrationWindow extends Window
{
    Logger _logger = Logger.getLogger(getClass().getName());

    private Client client;
    private Listbox albumListbox;
    private Div albumListDiv;
    private Div albumEditDiv;
    private ZAlbumComponent zAlbum;


    private UserProfile userProfile;
    private Photographer photographer; // the logged in photographer
    private Photographer newPhotographer; // used to create a new photographer profile by the admin

    private SportsEvent newEvent;
    private String eventZipCode;
    private String eventCity;
    private String eventCategory;
    private Tabbox tabbox;

    public SportraitAdministrationWindow()
    {
        this.client = (Client) Executions.getCurrent().getDesktop().getSession().getAttribute(Registry._SESSION_CLIENT_NAME);
        photographer = client.getPhotographer();
        userProfile = photographer.getUserProfile();
//        create an instance for a new userprofile
        newEvent = new SportsEvent();
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

        System.out.println("selectedTab = " + selectedTab);
        if (selectedTab != null && getFellow(selectedTab) != null && getFellow(selectedTab) instanceof Tab)
        {
            tabbox.setSelectedTab((Tab) getFellow(selectedTab));
        }
        getPage();
        createAlbumListbox();
    }

    /**
     * create the grid and the entries with all applicable albums
     *
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     */
    private void createAlbumListbox()
    {
        // first clear the listbox:
        albumListbox.getItems().clear();

        System.out.println("photographer = " + photographer);
        GenericLevelDAO levelDao = new GenericLevelDAO();
        List eventGroups;
        _logger.debug("Loading event groups");
        eventGroups = levelDao.listGenericLevel(EventGroup.class);
        for (Object eventGroup1 : eventGroups)
        {
            EventGroup eventGroup = (EventGroup) eventGroup1;
            List events = eventGroup.getEventsWithAlbums(photographer);
            _logger.debug("Loading events : " + events);
            if (events != null)
            {
                _logger.debug("number of events : " + events.size());
                for (Object event1 : events)
                {
                    try
                    {
                        Event event = (Event) event1;
                        _logger.debug("Loading albums for event");
                        List albums = event.getPhotographerAlbums(photographer);

                        _logger.debug("Loading albums for event");
                        appendEventRow(event);

                        for (Object album1 : albums)
                        {

                            Album album = (Album) album1;
                            _logger.debug("Loading album [" + album.getGenericLevelId().toString() + "]");
                            appendAlbumRow(album);
                        }
                    } catch (Exception e)
                    {

                        _logger.info("Unknown problem that occurs with loading albums for an event .... ", e);
                    }
                }
            } else
            {
                _logger.info("null events for eventGroup : [" + eventGroup + "]");
            }
        }

    }

    private void appendAlbumRow(final Album album)
    {
        Listitem albumItem;
        albumItem = new Listitem("-  " + album.getLongTitle() + " (Fotos:" + album.getNumberOfPhotos() + ")", album);
        if (album.getPublish())
        {
            albumItem.setStyle("color:green");
        } else
        {
            albumItem.setStyle("color:red");
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
                zAlbum.renderAlbumConfiguration(album.getGenericLevelId());
                albumListDiv.setVisible(false);
                albumEditDiv.setVisible(true);
            }
        });
        albumListbox.appendChild(albumItem);
    }


    private void appendEventRow(Event event)
    {
        Listitem eventItem = new Listitem(event.getEventDateDisplay() + "," + event.getEventGroup().getCity() + "," + event.getLongTitle(), event);
        // this doesn't work .... check html code to see what's wrong ...
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

        } catch (InterruptedException e)
        {
            throw new RuntimeException("Filupload interrupted", e);
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
     * User filled in the new Event screen and pressed save
     */
    public void createNewSportsEvent()
    {
        GenericLevelDAO glDao = new GenericLevelDAO();
        newEvent.setEventLocation(eventZipCode, eventCity, eventCategory);
        _logger.debug("going to persist new SportsEvent from admin screen:" + newEvent.toString());
        glDao.saveOrUpdate(newEvent);
    }

    /**
     * Used by the admin form for editing events
     * @return List of all Event s
     */
    public List getAllEvents() {
        GenericLevelDAO genericLevelDao = new GenericLevelDAO();
        if (client.isValid() && client.isAdmin()) {
            return genericLevelDao.listGenericLevel(Event.class);
        } else
        {
            _logger.info("User without admin rights wants to use user administration!");
            throw new RuntimeException("Current User has no admin rights!");
        }
    }


    /**
     * For user administration, return a list of all users
     *
     * @return If the current user has admin rights return all users
     */
    public List getAllPhotographers()
    {
        if (client.isValid())
        {
            PhotographerDAO photographerDao = new PhotographerDAO();
            return photographerDao.list();
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
        albums = (List<Album>)glDao.listAlbumsForPhotographer(photographer.getPhotographerId());
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
}
