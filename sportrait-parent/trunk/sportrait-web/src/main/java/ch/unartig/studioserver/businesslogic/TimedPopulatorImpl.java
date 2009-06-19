/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since Oct 26, 2005$
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
 * Revision 1.3  2007/05/07 11:49:23  alex
 * getphotos cannot be used!!!
 *
 * Revision 1.2  2007/03/27 16:39:17  alex
 * refactored studioalbum into album
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.14  2006/04/30 16:21:27  alex
 * removing system.outs
 *
 * Revision 1.13  2006/02/20 16:54:49  alex
 * new album nav concept works
 *
 * Revision 1.12  2006/02/10 14:21:49  alex
 * admin tool: edit level partly ...
 *
 * Revision 1.11  2006/01/27 09:30:36  alex
 * new pager implemenatation
 *
 * Revision 1.10  2005/11/28 17:52:16  alex
 * bug fixes
 *
 * Revision 1.9  2005/11/25 10:56:58  alex
 *
 * Revision 1.8  2005/11/22 19:45:46  alex
 * admin actions, configurations
 *
 * Revision 1.7  2005/11/19 16:31:43  alex
 * bookmarks of displays should work now
 *
 * Revision 1.6  2005/11/05 21:41:58  alex
 * overview und links in tree menu
 *
 * Revision 1.5  2005/11/04 23:05:00  alex
 * error ...
 *
 * Revision 1.4  2005/11/01 11:28:39  alex
 * pagination works; put logic in overview bean
 *
 * Revision 1.3  2005/10/26 20:40:12  alex
 * first view impl
 *
 * Revision 1.2  2005/10/26 15:36:44  alex
 * some fixes
 *
 * Revision 1.1  2005/10/26 14:34:32  alex
 * first version of album overview
 * new mappings in struts for the /album/** url
 *
 ****************************************************************/
package ch.unartig.studioserver.businesslogic;

import ch.unartig.u_core.exceptions.UnartigException;
import ch.unartig.u_core.Registry;
import ch.unartig.studioserver.beans.AlbumBean;
import ch.unartig.studioserver.model.Album;
import ch.unartig.studioserver.model.Photo;
import ch.unartig.u_core.persistence.DAOs.PhotoDAO;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.List;

/**
 * this populator generates pages for photos that are ordered by time; Interval: 5 Minutes
 */
public class TimedPopulatorImpl implements OverviewPopulator
{
    Logger _logger = Logger.getLogger(getClass().getName());
    public static String STRATEGY = "DB Timed Populator";

    /**
     * If hour and minutes are present, populate by time, otherwise populate by page
     *
     * @param albumBean
     * @param album
     */
    public void populate(AlbumBean albumBean, Album album) throws UnartigException {
        // if time has been given, query for page first:
        if (albumBean.getHour() != -1 && albumBean.getMinutes() != -1)
        {
            setPageByTime(albumBean);
        }
        populateByPage(albumBean);
    }


    /**
     * time has been given; query for page and set in album bean
     *
     * @param albumBean
     */
    private void setPageByTime(AlbumBean albumBean) throws UnartigException
    {
        PhotoDAO photoDao = new PhotoDAO();
        _logger.debug("[NEW] loading : starting at " + albumBean.getHour() + ":" + albumBean.getMinutes() + " , interval = " + Registry._ALBUM_TIME_INTERVAL);
        // query for first photo after the given time and calculate page
        int page = 0;
        page = photoDao.getPageNrFor(albumBean.getAlbum(), albumBean.getHour(), albumBean.getMinutes());
        _logger.debug("populating for page : " + page);
        albumBean.setPage(page);
//        populateByPage(albumBean);
    }

    /**
     * read out page from album bean and populate for the given page.
     * <br> page shall be initialized to 1 if no page is given
     *
     * @param albumBean
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     */
    private void populateByPage(AlbumBean albumBean) throws UnartigException
    {
        PhotoDAO photoDao = new PhotoDAO();
        int page = albumBean.getPage();
        if (page < 1)
        {
            _logger.debug("page number must not be smaller than 1 ... throwing exception");
            throw new UnartigException("Page number < 1 in albumbean ...");
        }
        _logger.debug("populating for page : " + page);
        List photosOnPage;
        photosOnPage = photoDao.getPhotosForPage(page, albumBean.getAlbum(), Registry.getItemsOnPage());
        _logger.debug("found : " + photosOnPage.size() + " photos");
        albumBean.setPhotos(photosOnPage);
        albumBean.setSize(albumBean.getAlbum().getNumberOfPhotos());
        albumBean.setNumberOfPages(1 + (albumBean.getSize() - 1) / albumBean.getItemsOnPage());
        _logger.debug("setting number of pages : " + albumBean.getNumberOfPages() + "; total photos : " + albumBean.getSize());
    }

    /**
     * helper to set hour and mintues for a given photo and album bean<br>
     *
     * @param photoInInterval
     * @param albumBean
     */
    public static void setCorrectIntervalTimeForPhoto(Photo photoInInterval, AlbumBean albumBean)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(photoInInterval.getPictureTakenDate());
        albumBean.setHour(Integer.valueOf(c.get(Calendar.HOUR_OF_DAY)));
        int minutes = c.get(Calendar.MINUTE);
        int minutesRounded = minutes - (minutes % Registry._ALBUM_TIME_INTERVAL);
        albumBean.setMinutes(minutesRounded);
    }


}
