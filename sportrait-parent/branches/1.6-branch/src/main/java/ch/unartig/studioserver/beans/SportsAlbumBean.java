/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since 14.03.2006$
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
 * Revision 1.6  2007/06/03 21:35:21  alex
 * Bug #1234 : Ordnung eventcategory: wird nun nach als liste gefuehrt, ordnung wird eingehalten
 *
 * Revision 1.5  2007/05/05 08:20:52  alex
 * index navigation , improve style
 *
 * Revision 1.4  2007/05/03 10:02:22  alex
 * startnummernsuche works
 *
 * Revision 1.3  2007/04/17 20:56:01  alex
 * display, albumpage works
 *
 * Revision 1.2  2007/04/17 11:03:27  alex
 * dynamic pager added
 *
 * Revision 1.1  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.6  2006/05/01 12:43:48  alex
 * fix for album reload for sports and event album
 *
 * Revision 1.5  2006/04/29 23:32:07  alex
 * many sola features, bugs, hibernate config
 *
 * Revision 1.4  2006/04/19 21:31:53  alex
 * session will be restored with album-bean (i.e. for bookmarked urls or so...)
 *
 * Revision 1.3  2006/04/06 18:31:22  alex
 * display fixed for sports album
 *
 * Revision 1.2  2006/03/21 17:17:03  alex
 * sportsalbum changes, empty etappe now works
 *
 * Revision 1.1  2006/03/20 15:33:33  alex
 * first check in for new sports album logic and db changes
 *
 ****************************************************************/
package ch.unartig.studioserver.beans;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.businesslogic.SessionHelper;
import ch.unartig.studioserver.model.*;
import ch.unartig.studioserver.persistence.DAOs.PhotoDAO;
import org.apache.log4j.Logger;
import org.apache.struts.action.DynaActionForm;

import java.util.List;

/**
 * Bean for handling sports events
 * <br>implements the page-parameter part of the populate template from the abstract album bean
 * @author Alexander Josef, 2006
 */
public class SportsAlbumBean extends AbstractAlbumBean
{

    Logger _logger = Logger.getLogger(getClass().getName());
    /*the bib-number*/
    private String startNumber;
    /*etappe is equal to a sportsAlbum*/
    private Long etappe;
    private Long eventCategoryId; // this is needed additionally to eventCategory because of the BeanUtils.copyProperties call in EventCategoryAction!

    private boolean albumLevel;
    private boolean eventAlbum;
    private String levelId;
    private EventCategory eventCategory;
    private SportsEvent sportsEvent;
    private List eventCategories; //why a list of eventCategory ? --> search form for start numbers on album overview


    public SportsAlbumBean()
    {
    }


    /**
     * implementation of the abstract template method from abstractAlbumBean
     * <br> set page according to start-number
     */
    public void setPageBySearchParameter() {
        if (page < 1)
        {
            page = 1;
        }
    }

    /**
     * count total number of photos that have been selected for this session with the etappe and startnummer AND page
     */
    protected void setTotalNumberOfPhotosForSession()
    {
        PhotoDAO photoDao = new PhotoDAO();
        setSize(photoDao.countPhotosFor(startNumber, eventCategory));
    }

    /**
     * SportsAlbum implementation to set all the photos for the current session, considering page, startnumber and eventcategory
     * @see AbstractAlbumBean#setPhotosForCurrentSession()
     * @throws UAPersistenceException
     */
    protected void setPhotosForCurrentSession()
    {
        List photosOnPagePlusPreview;
        PhotoDAO photoDao = new PhotoDAO();
        photosOnPagePlusPreview = photoDao.listSportsPhotosOnPagePlusPreview(page, eventCategory, Registry.getItemsOnPage(),startNumber);
        _logger.debug("found : " + photosOnPagePlusPreview.size() + " photos");
        setPhotos(photosOnPagePlusPreview);
    }

    public Photo getLastPhotoInAlbumAndSelection() throws UnartigException
    {
        _logger.debug("getting last photo of album and selection for :"+this);
        PhotoDAO phDao = new PhotoDAO();
        return phDao.getLastPhotoInCategoryAndSelection(eventCategory,startNumber);
    }

    public Photo getFirstPhotoInAlbumAndSelection() throws UnartigException
    {
        _logger.debug("getting first photo of album and selection for :"+this);
        PhotoDAO phDao = new PhotoDAO();
        return phDao.getFirstPhotoInAlbumAndSelection(eventCategory,startNumber);
    }


    protected void setPageFor(Long displayPhotoId) throws UAPersistenceException
    {
        _logger.debug("setting new page number for eventCategory");
        PhotoDAO phDao = new PhotoDAO();
        page = phDao.getAlbumPageNrFor(displayPhotoId,eventCategory,getStartNumber());
        _logger.debug(" ... got page :" + page);
    }

    public String getActionString()
    {
        String actionString = "/showCategory";
        return actionString;
    }

    public String getStartNumber()
    {
        return startNumber;
    }

    public void setStartNumber(String startNumber)
    {
        this.startNumber = startNumber;
    }

    public Long getEtappe()
    {
        return etappe;
    }

    public void setEtappe(Long etappe)
    {
        this.etappe = etappe;
    }

    public boolean isAlbumLevel()
    {
        return albumLevel;
    }

    public void setAlbumLevel(boolean albumLevel)
    {
        this.albumLevel = albumLevel;
    }

    public String getLevelId()
    {
        return levelId;
    }

    public void setLevelId(String levelId)
    {
        this.levelId = levelId;
    }

    public boolean isEventAlbum()
    {
        return eventAlbum;
    }

    public void setEventAlbum(boolean eventAlbum)
    {
        this.eventAlbum = eventAlbum;
    }

    public Long getEventCategoryId()
    {
        return eventCategoryId;
    }

    public void setEventCategoryId(Long eventCategoryId)
    {
        this.eventCategoryId = eventCategoryId;
    }

    public void setEventCategory(EventCategory eventCategory)
    {
        this.eventCategory = eventCategory;
        this.eventCategoryId = eventCategory.getEventCategoryId(); // set eventCategoryId if eventCategory is set (eventCategoryId needed for form parameter operations)
    }


    public EventCategory getEventCategory()
    {
        return eventCategory;
    }

    public void setSportsEvent(SportsEvent sportsEvent)
    {
        this.sportsEvent = sportsEvent;
    }

    public SportsEvent getSportsEvent()
    {
        return sportsEvent;
    }

    public void setEventCategories(List eventCategories)
    {
        this.eventCategories = eventCategories;
    }

    /**
     * Return all categories registered for the event of this sportsalbum.
     * Make sure you really want to show all categories, also categories without photos or with offline albums only
     * fixme: still needed? probably used by jsp getter? check!
     * @return
     */
    public List getEventCategories()
    {
        return eventCategories;
    }

    /**
     * only return categories with photos. Used by jsp getter
     * @return
     */
    public List getEventCategoriesWithPhotos()
    {
        return eventCategory.getEvent().getEventCategoriesWithPhotos();
    }
}
