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
 * Revision 1.2  2007/03/27 16:39:17  alex
 * refactored studioalbum into album
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.9  2006/02/20 16:54:49  alex
 * new album nav concept works
 *
 * Revision 1.8  2006/01/27 09:30:36  alex
 * new pager implemenatation
 *
 * Revision 1.7  2005/11/25 10:56:58  alex
 *
 * Revision 1.6  2005/11/05 21:41:58  alex
 * overview und links in tree menu
 *
 * Revision 1.5  2005/11/05 10:32:14  alex
 * shopping cart and minor problems, exception handling
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
package ch.unartig.u_core.presentation;

import ch.unartig.u_core.exceptions.UnartigException;
import ch.unartig.u_core.Registry;
import ch.unartig.u_core.model.Album;
import ch.unartig.u_core.beans.AlbumBean;
import ch.unartig.u_core.persistence.DAOs.PhotoDAO;
import org.apache.log4j.Logger;

import java.util.List;

/**
 *
 */
public class NoTimePopulatorImpl implements OverviewPopulator
{
    Logger _logger = Logger.getLogger(getClass().getName());

    /**
     * @param ob
     * @param album
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     */
    public void populate(AlbumBean ob, Album album) throws UnartigException
    {
        PhotoDAO pDao = new PhotoDAO();
        List albumPhotos = pDao.getPhotosForPage(-1, album, Registry.getItemsOnPage());
        if (albumPhotos==null || albumPhotos.size()==0)
        {
            throw new UnartigException("No Photos available for album");
        }
        ob.setPhotos(albumPhotos);
//        fillPageWithBlanks(ob);
        ob.setSize(pDao.countPhotos(album));
        ob.setNumberOfPages(1 + (ob.getSize() - 1) / ob.getItemsOnPage());
        ob.setFirstElementOnPage();
        ob.setLastElementOnPage();
//        ob.setProjectRoot(ob.webContextPhotoRoot + album.getFsPath() + '/');
        _logger.debug("overview for album ["+album.getNavTitle()+"] has been populated with ["+albumPhotos.size()+"] photos -- INCLUDING THE BLANKS");
    }



}
