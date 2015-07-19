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
 * Revision 1.4  2007/04/17 20:56:01  alex
 * display, albumpage works
 *
 * Revision 1.3  2007/04/17 11:03:27  alex
 * dynamic pager added
 *
 * Revision 1.2  2007/03/27 16:39:17  alex
 * refactored studioalbum into album
 *
 * Revision 1.1  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.19  2006/04/19 21:31:53  alex
 * session will be restored with album-bean (i.e. for bookmarked urls or so...)
 *
 * Revision 1.18  2006/04/06 18:31:22  alex
 * display fixed for sports album
 *
 * Revision 1.17  2006/03/20 15:33:33  alex
 * first check in for new sports album logic and db changes
 *
 * Revision 1.16  2006/03/08 17:42:26  alex
 * small fixes
 *
 * Revision 1.15  2006/03/07 09:34:16  alex
 * kosta logo and performance improvment
 *
 * Revision 1.14  2006/03/03 16:54:56  alex
 * minor fixes
 *
 * Revision 1.13  2006/02/28 14:57:46  alex
 * added more resources (email for order confirmation), small fixes
 *
 * Revision 1.12  2006/02/22 17:08:56  alex
 * jumping forward and jumping back x-pages works
 *
 * Revision 1.11  2006/02/22 14:00:51  alex
 * new album nav concept works also in display
 *
 * Revision 1.10  2006/02/20 16:54:49  alex
 * new album nav concept works
 *
 * Revision 1.9  2006/02/10 14:21:49  alex
 * admin tool: edit level partly ...
 *
 * Revision 1.8  2006/01/27 09:35:39  alex
 * new pager implemenatation
 *
 * Revision 1.7  2006/01/27 09:30:36  alex
 * new pager implemenatation
 *
 * Revision 1.6  2005/11/25 11:09:09  alex
 * removed system outs
 *
 * Revision 1.5  2005/11/25 10:56:58  alex
 *
 * Revision 1.4  2005/11/21 17:52:58  alex
 * no account action , photo order
 *
 * Revision 1.3  2005/11/19 16:31:43  alex
 * bookmarks of displays should work now
 *
 * Revision 1.2  2005/11/14 14:40:05  alex
 * back to album from shopping cart works
 *
 * Revision 1.1  2005/11/05 21:41:58  alex
 * overview und links in tree menu
 *
 * Revision 1.8  2005/11/05 16:00:41  alex
 * tiles error, more sc stuff
 *
 * Revision 1.7  2005/11/05 10:32:14  alex
 * shopping cart and minor problems, exception handling
 *
 * Revision 1.6  2005/11/02 09:10:09  alex
 * album view works
 *
 * Revision 1.5  2005/11/01 11:28:39  alex
 * pagination works; put logic in overview bean
 *
 * Revision 1.4  2005/10/27 20:21:39  alex
 * album overview
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
package ch.unartig.studioserver.beans;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.model.Album;
import ch.unartig.studioserver.persistence.DAOs.PhotoDAO;
import org.apache.log4j.Logger;

/**
 * this bean provides the album view with all necessary information
 *
 * @author Alexander Josef
 */
public class AlbumBean extends AbstractAlbumBean
{
    Logger _logger = Logger.getLogger(getClass().getName());

    // static variables

    private String photographer;
    // projectRoot is the filesystem path to the project-files starting at the web root
    private String projectRoot;

    private int firstElementOnPage;
    private int lastElementOnPage;


    /**
     * standard constructor
     */
    public AlbumBean()
    {
    }

    /**
     * Constructor; sets the album of the AlbumBean
     *
     * @param album
     */
    public AlbumBean(Album album)
    {
        this.album = album;
    }


    public String getPhotographer()
    {
        return photographer;
    }

    public void setPhotographer(String photographer)
    {
        this.photographer = photographer;
    }

    public void setMinutes(int minutes)
    {
        this.minutes = minutes;
    }

    public void setHour(Integer hour)
    {
        this.hour = hour;
    }

    public Integer getHour()
    {
        return hour;
    }

    public int getItemsOnPage()
    {
        return Registry.itemsOnPage;
    }


    public int getMinutes()
    {
        return minutes;
    }


    public String getProjectRoot()
    {
        return projectRoot;
    }


    public void setType(String type)
    {
        this.type = type;
    }

    public int getFirstElementOnPage()
    {
        return firstElementOnPage;
    }

    public void setFirstElementOnPage(int firstElementOnPage)
    {
        this.firstElementOnPage = firstElementOnPage;
    }

    public int getLastElementOnPage()
    {
        return lastElementOnPage;
    }

    public void setLastElementOnPage(int lastElementOnPage)
    {
        this.lastElementOnPage = lastElementOnPage;
    }

    public void setProjectRoot(String projectRoot)
    {
        this.projectRoot = projectRoot;
    }


    /**
     * calculate the first picture on the page based on the number of pictures that are shown on each page
     * start of list is 0!
     * example: page 1, 15 pictues on page: 1 * 15 =15 -15 = 0
     * 0 is first element
     */
    public void setFirstElementOnPage()
    {
        firstElementOnPage = page * Registry.itemsOnPage - Registry.itemsOnPage;
    }

    /**
     * last element on page;
     * start in list is 0!
     */
    public void setLastElementOnPage()
    {
        lastElementOnPage = (page * Registry.itemsOnPage) - 1;
    }


    public String toString()
    {
        return "AlbumBean{" + "photographer='" + photographer + '\'' + ", page=" + page + ", hour=" + hour + ", minutes=" + minutes + ", numberOfPages=" + numberOfPages + ", size=" + size + ", type='" + type + '\'' + ", projectRoot='" + projectRoot + '\'' + ", firstElementOnPage=" + firstElementOnPage + ", lastElementOnPage=" + lastElementOnPage + ", shoppingCart=" + shoppingCart + '}';
    }

    protected void setPhotosForCurrentSession()
    {

    }

    /**
     *
     */
    public void setPageBySearchParameter()  {
        if (page < 1 && hour >= 0 && minutes >= 0)
        {
            setPageByTime();
        } else
        {
            if (page < 1)
            {
                page = 1;
            }
        }
    }

    public String getActionString()
    {
        // implement if needed
        return null;
    }


    /**
     * time has been given; query for page and set this page in album bean
     */
    private void setPageByTime()
    {
        PhotoDAO photoDao = new PhotoDAO();
        _logger.debug("[NEW] loading : starting at " + hour + ":" + minutes );
        // query for first photo after the given time and calculate page
        page = photoDao.getPageNrFor(album, hour, minutes);
        _logger.debug("populating for page : " + page);
    }
}