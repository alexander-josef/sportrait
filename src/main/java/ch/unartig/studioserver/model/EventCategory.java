/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 27.03.2007$
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
 * Revision 1.5  2007/05/07 11:44:54  alex
 * getphotos cannot be used!!!1
 *
 * Revision 1.4  2007/04/17 11:03:27  alex
 * dynamic pager added
 *
 * Revision 1.3  2007/03/29 13:33:44  alex
 * working on photo upload
 *
 * Revision 1.2  2007/03/28 13:52:58  alex
 * edit events page
 *
 * Revision 1.1  2007/03/27 20:03:07  alex
 * added eventCategory
 *
 ****************************************************************/
package ch.unartig.studioserver.model;

import ch.unartig.exceptions.UnartigException;
import org.apache.log4j.Logger;

/**
 * Example: Etappen in Sola
 */
public class EventCategory extends GeneratedEventCategory
{
    Logger _logger = Logger.getLogger(getClass().getName());

    /**
     * default constructor needed for hibernate
     */
    public EventCategory()
    {
    }

    /**
     * 
     * @param eventCategory
     * @param event
     */
    public EventCategory(String eventCategory, SportsEvent event)
    {
        setTitle(eventCategory);
        setEvent(event);
    }

    /**
     * Return true if this eventcategory has uploaded, published photos.
     * @return true if category has photos
     * @throws ch.unartig.exceptions.UnartigException
     */
    public boolean hasPublishedPhotos() throws UnartigException {

        _logger.debug("EventCategory "+ getTitle() +" - EventCategory.getAlbums().size() = " + getAlbums().size());
        for (Object o : getAlbums()) {
            Album album = (Album) o;
            if (album.getPublish() && album.getNumberOfPhotos() > 0) {
                return true;
            }
        }
        return false;
    }
}
