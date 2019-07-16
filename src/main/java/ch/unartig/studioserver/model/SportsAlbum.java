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
 * Revision 1.2  2007/04/17 11:03:27  alex
 * dynamic pager added
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
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
 * Revision 1.4  2006/04/06 18:31:22  alex
 * display fixed for sports album
 *
 * Revision 1.3  2006/03/20 17:20:37  alex
 * ui improvements, sportsalbum
 *
 * Revision 1.2  2006/03/20 15:45:33  alex
 * first check in for new sports album logic and db changes
 *
 * Revision 1.1  2006/03/20 15:33:32  alex
 * first check in for new sports album logic and db changes
 *
 ****************************************************************/
package ch.unartig.studioserver.model;

import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.beans.AbstractAlbumBean;
import ch.unartig.studioserver.beans.SportsAlbumBean;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.io.InputStream;

/**
 * this album object that can be made persistent in the db is searchable by "Startnumber" and 'etappe'
 * @author Alexander Josef, 2006
 */

@Entity
@DiscriminatorValue("SPORTSALBUM")
public class SportsAlbum extends Album implements java.io.Serializable {

    /**
     * This is called via introspection from the admin action when a new SportsEvent is created.
     * @param navTitle
     * @param longTitle
     * @param description
     */
    public SportsAlbum(String navTitle, String longTitle, String description)
    {
        setNavTitle(navTitle);
        setLongTitle(longTitle);
        setDescription(description);
    }

    public SportsAlbum()
    {
    }

    /**
     * overridden for SportsAlbum; this calls the specialized action for sports albums
     * @return URL for sports Album action
     */
    public String getIndexNavLink()
    {
        return getActionString() +"?page=1&startNumber=&etappe="+getGenericLevelId().toString();
    }

    public String getActionStringPart()
    {
        String actionStringPart = "/sportsAlbum/";
        return actionStringPart;
    }

    /**
     *
     * @return
     */
    @Override
    public AbstractAlbumBean getAlbumBean() {

        SportsAlbumBean sportsAlbumBean = new SportsAlbumBean();
        _logger.debug("creating new SportsAlbumBean");
        // todo: check in EventCategoryAction how to instantiate a SportsAlbumBean --> should be part of SportsAlbumBean constructor
        sportsAlbumBean.setEventCategory(getEventCategory());
        sportsAlbumBean.setSportsEvent((SportsEvent)getEvent());
        sportsAlbumBean.setEventCategories(((SportsEvent) getEvent()).getEventCategories());

        //return new SportsAlbumBean(eventCategory,event);
        return sportsAlbumBean;

    }

    public boolean isSportsAlbumLevel()
    {
        return true;
    }

    /**
     * Method extracts zip archive to file storage provider
     * @param fileInputStream InputStream that contains an Zip archive with Photos
     */
    void extractPhotosFromArchive(InputStream fileInputStream) throws UnartigException
    {
        Registry.getFileStorageProvider().putFilesFromArchive(this,fileInputStream);
    }




}
