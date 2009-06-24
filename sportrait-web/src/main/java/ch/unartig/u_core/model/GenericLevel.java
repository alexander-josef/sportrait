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
 * Revision 1.3  2007/05/07 11:44:54  alex
 * getphotos cannot be used!!!1
 *
 * Revision 1.2  2007/03/27 16:39:17  alex
 * refactored studioalbum into album
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.4  2007/03/14 03:18:36  alex
 * no more price segment
 *
 * Revision 1.3  2007/03/14 02:41:01  alex
 * initial checkin
 *
 * Revision 1.2  2007/03/12 18:57:02  alex
 * product types for albums
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.37  2006/12/06 18:42:08  alex
 * no js necessary for basic shopping cart functionality
 *
 * Revision 1.36  2006/12/05 22:51:56  alex
 * album kann jetzt freigeschaltet werden oder geschlossen sein
 *
 * Revision 1.35  2006/11/12 13:32:49  alex
 * dynamic album ads
 *
 * Revision 1.34  2006/11/12 11:58:47  alex
 * dynamic album ads
 *
 * Revision 1.33  2006/11/10 15:55:30  alex
 * dynamic album ads
 *
 * Revision 1.32  2006/11/10 14:24:13  alex
 * dynamic priceinfo
 *
 * Revision 1.31  2006/04/29 23:32:07  alex
 * many sola features, bugs, hibernate config
 *
 * Revision 1.30  2006/04/06 18:31:22  alex
 * display fixed for sports album
 *
 * Revision 1.29  2006/03/20 15:33:32  alex
 * first check in for new sports album logic and db changes
 *
 * Revision 1.28  2006/03/03 16:54:56  alex
 * minor fixes
 *
 * Revision 1.27  2006/02/28 14:57:46  alex
 * added more resources (email for order confirmation), small fixes
 *
 * Revision 1.26  2006/02/23 14:37:42  alex
 * admin tool: new category works now
 *
 * Revision 1.25  2006/02/16 17:13:46  alex
 * admin interface: deletion of levels works now
 *
 * Revision 1.24  2006/02/15 15:57:03  alex
 * bug [968] fixed. admin interface does that now
 *
 * Revision 1.23  2006/02/13 16:15:28  alex
 * but [968]
 *
 * Revision 1.22  2006/02/10 14:21:49  alex
 * admin tool: edit level partly ...
 *
 * Revision 1.21  2006/02/08 18:04:50  alex
 * first steps for album type configuration
 *
 * Revision 1.20  2006/01/24 15:38:35  alex
 * file ending is now .html
 *
 * Revision 1.19  2006/01/11 20:40:53  alex
 * level update form works
 *
 * Revision 1.18  2005/11/30 10:47:38  alex
 * bug fixes
 *
 * Revision 1.17  2005/11/19 20:32:32  alex
 * price segments inserted
 *
 * Revision 1.16  2005/11/19 11:08:20  alex
 * index navigation works, (extended GenericLevel functions)
 * wrong calculation of fixed checkout overview eliminated
 *
 * Revision 1.15  2005/11/08 10:05:02  alex
 * tree items i18n, backend
 *
 * Revision 1.14  2005/11/08 09:33:45  alex
 * languages
 *
 * Revision 1.13  2005/11/07 21:57:43  alex
 * admin interface refactored
 *
 * Revision 1.12  2005/11/07 17:38:26  alex
 * admin interface refactored
 *
 * Revision 1.11  2005/11/06 21:43:22  alex
 * overview, admin menu, index-photo upload
 *
 * Revision 1.10  2005/11/05 21:41:58  alex
 * overview und links in tree menu
 *
 * Revision 1.9  2005/11/04 17:12:18  alex
 * tree refactoring
 *
 * Revision 1.8  2005/10/06 21:30:51  alex
 * saving new tree_items file
 *
 * Revision 1.7  2005/10/06 18:14:23  alex
 * saving new tree_items file
 *
 * Revision 1.6  2005/10/06 14:30:09  alex
 * generating the nav tree recursivly works
 *
 * Revision 1.5  2005/10/06 11:06:33  alex
 * generating the nav tree
 *
 * Revision 1.4  2005/10/06 08:54:04  alex
 * cleaning up the model
 *
 * Revision 1.3  2005/10/04 11:36:48  alex
 * level imports
 *
 * Revision 1.2  2005/10/03 14:50:25  alex
 * first daos
 *
 * Revision 1.1  2005/09/26 18:37:45  alex
 * *** empty log message ***
 *
 ****************************************************************/
package ch.unartig.u_core.model;

import ch.unartig.u_core.controller.Client;
import ch.unartig.u_core.exceptions.NotAuthorizedException;
import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.u_core.exceptions.UnartigException;
import ch.unartig.u_core.Registry;
import ch.unartig.u_core.presentation.AlbumType;
import ch.unartig.u_core.presentation.GenericLevelVisitor;
import ch.unartig.u_core.persistence.DAOs.PhotoDAO;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
public abstract class GenericLevel extends GeneratedGenericLevel implements Comparable
{
    Logger _logger = Logger.getLogger(getClass().getName());

    public GenericLevel()
    {
    }

    public GenericLevel(String navTitle, String longTitle, String description, String quickAccess, Boolean aPrivate, boolean publish, String privateAccessCode) {
    }

    public void accept(GenericLevelVisitor visitor)
    {

    }

    public GenericLevel(String navTitle, String longTitle, String description)
    {
        setNavTitle(navTitle);
        setLongTitle(longTitle);
        setDescription(description);

    }

    /**
     * recursivly walk through all the levels and genereate the nav tree
     *
     * @return the nav tree as string buffer
     * @param sb
     * @param langSuffix
     */
    public StringBuffer composeTreeItem(StringBuffer sb, String langSuffix)
    {
        _logger.debug("generating tree item for : " + this.getLongTitle());
        _logger.debug("this is an instance of " + this.getClass().getName());
        _logger.debug("string buffer received : " + sb);

//        StringBuffer sb = new StringBuffer();
        sb.append('[');
        // append navigation Title
        sb.append("'").append(getTreeNavTitle()).append("'").append(',');
        // append the link
        sb.append(getTreeNavLink()).append(',');
        // append the item scope settings
        sb.append(getItemScopeSettings(langSuffix)).append(',');
        List children = listChildrenForNavTree();
        _logger.debug("number of children for '" + this.getLongTitle() + "': " + children.size());
        for (int i = 0; i < children.size(); i++)
        {
            GenericLevel genericLevel = (GenericLevel) children.get(i);
            _logger.debug(".... stepping into '" + genericLevel.getLongTitle() + "'");
            _logger.debug("child is of instance  '" + genericLevel.getClass().getName() + "'");
            sb.append(genericLevel.composeTreeItem(new StringBuffer(), langSuffix));
        }

        sb.append(']');
        sb.append(',');

        _logger.debug("DONE FOR ITEM :  '" + this.getLongTitle() + "'");
        return sb;
    }


    /**
     * base implementation; uses the nav title of the gen level object<br>
     * only category differs: no treenavtitle needed for category since this is represented by an image
     *
     * @return a String to be shown in the nav menu
     */
    protected String getTreeNavTitle()
    {
        return getNavTitle();
    }

    /**
     * base implemention
     * DON'T FORGET TO INCLUDE THE "'" character
     *
     * @param langSuffix
     * @return the item scope settings as string
     */
    protected String getItemScopeSettings(String langSuffix)
    {
        return "null";
    }

    /**
     * returns children of categories and eventgroups, returns null for event and album (will not be listed in nav-tree)
     * @return a list with all children of this generic level
     */
    public abstract List listChildrenForNavTree();

    /**
     * @return
     */
    public abstract List listChildren();

    /**
     * get the parent level Class of this level
     *
     * @return the parent level of this level
     */
    public abstract Class getParentClazz();


    /**
     * DON'T FORGET TO INCLUDE THE "'" character
     *
     * @return the link string
     */
    public String getTreeNavLink()
    {
        return "'" + getIndexNavLink() + "'";
    }

    /**
     * index nav link helper method.<br>
     * configure url for action-link here
     *
     * @return string with index nav link
     */
    public String getIndexNavLink()
    {
        String url = "/overview/" + getGenericLevelId().toString() + "/" + getNavTitle() + "/show.html";
        if (getIsPrivate() != null && getIsPrivate().booleanValue())
        {
            _logger.info("accessing private level : " + this.getGenericLevelId());
            url = "/private" + url;
        }
        return url;
    }


    public String getLevelOverviewImgUrl()
    {
        return getLevelUrlPath() + Registry._LEVEL_INDEX_IMAGE_NAME;
    }

    private String getLevelUrlPath()
    {
        return Registry.getWebImagesDocumentRoot() + getGenericLevelId().toString() + "/";
    }


    /**
     * Count recursivly all photos that are contained in this level
     *
     * @return number of photos
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     */
    private int countPhotos()
    {
        PhotoDAO photoDao = new PhotoDAO();
        int photoCount = 0;

        if (this instanceof Album)
        {
            photoCount = photoDao.countPhotos((Album) this);
        } else
        {
            List children = listChildren();
            for (int i = 0; i < children.size(); i++)
            {
                GenericLevel genericLevel = (GenericLevel) children.get(i);
                photoCount += genericLevel.countPhotos();
            }
        }

        return photoCount;
    }

    /**
     * Count all photos in the album (published or non-published). Return 0 if the album is not yet published.
     * @return total number of photos of this level
     */
    public int getNumberOfPhotos() {
        _logger.debug("GenericLevel.getNumberOfPhotos going to count photos");
        return countPhotos();
    }

    /**
     * @return either "Category", "EventGroup", "Event" or "Album" as level type
     */
    public abstract String getLevelType();

    /**
     * adds a parent event to the generic level
     *
     * @param parentLevel
     */
    public abstract void setParentLevel(GenericLevel parentLevel) throws UnartigException;

    /**
     * @return the parent generic level
     */
    public abstract GenericLevel getParentLevel();

    public int compareTo(Object o)
    {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * recursivly walk up the hierarchy to compose the index nav entries<br>
     * the root element (category) has itself as the parent level
     *
     * @return List of index entries (String[displaytext,naventries])
     */
    public final List getIndexNavEntries()
    {
        GenericLevel parentLevel;
        List indexNavEntries = new ArrayList();
        if ((parentLevel = getParentLevel()) != this)
        {
            indexNavEntries.addAll(parentLevel.getIndexNavEntries());
        }


        indexNavEntries.add(getIndexNavEntry());
        return indexNavEntries;
    }

    /**
     * return an array of two strings containing the link and display text for the index nav entries of the tree item
     */
    public abstract String[] getIndexNavEntry();

    public abstract String getEventDateDisplay();

    public void setEventDate(Date eventDate)
    {
        // default implementation. do nothing
    }

    /**
     * "display" method ... convert string from view in a date
     *
     * @param eventDateDisplay
     */
    public void setEventDateDisplay(String eventDateDisplay)
    {
        // do nothing in default implementation
    }

    /**
     * @param albumType
     */
    public void setAlbumType(AlbumType albumType)
    {
        // default implementation. do nothing
    }

    /**
     * default getter returns null; overwritten in album
     *
     * @return the album type
     */
    public AlbumType getAlbumType()
    {
        return null;
    }


    /**
     * returns true if this level is a category
     *
     * @return true for level Category
     */
    public boolean isCategoryLevel()
    {
        return false;
    }

    /**
     * @return true if level is eventGroup
     */
    public boolean isEventGroupLevel()
    {
        return false;
    }

    /**
     * @return true if level is event
     */
    public boolean isEventLevel()
    {
        return false;
    }

    /**
     * @return true if level is of type SportsEventLevel
     */
    public boolean isSportsEventLevel()
    {
        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * @return true if level is album
     */
    public boolean isAlbumLevel()
    {
        return false;
    }

    /**
     * @return true if level is sportAlbum
     */
    public boolean isSportsAlbumLevel()
    {
        return false;
    }

    /**
     * method deleteLevel must be implemented for all levels
     *
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     *
     */
    public abstract void deleteLevel() throws UAPersistenceException;


    /**
     * Check write access to publish using the client object
     * @param publish
     * @param client
     */
    public void setPublish(Boolean publish, Client client) throws NotAuthorizedException
    {
        checkWriteAccessFor(client);
        setPublish(publish);
    }

    /**
     * Overridden from super to make sure no null value is returned
     * @return TRUE or FALSE; does never return null
     */
    public Boolean getPublish()
    {
        if (super.getPublish() != null)
        {
            return super.getPublish();
        } else
        {
            return false;
        }
    }

    protected void checkWriteAccessFor(Client client) throws NotAuthorizedException
    {
        if (!client.isAdmin())
        {
            throw new NotAuthorizedException("Not Administrator rights");
        }
    }


    public void toggleLevelPublishStatus(Client client) throws NotAuthorizedException
    {
        if (getPublish()!=null && getPublish())
        {
            setPublish(Boolean.FALSE,client);
        } else
        {
            setPublish(Boolean.TRUE,client);
        }
    }

    public void makeWebImagesPath()
    {
        new File(Registry.getWebImagesDocumentRoot(),getGenericLevelId().toString()).mkdirs();
    }

    public void makeFineImagesPath()
    {
        new File(Registry.getFineImagesDirectory(),getGenericLevelId().toString()).mkdirs();
    }
}
