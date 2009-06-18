/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Nov 18, 2005$
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
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.11  2006/12/18 16:22:58  alex
 * fixing shopping cart bug
 *
 * Revision 1.10  2006/11/22 09:14:25  alex
 * small fixes
 *
 * Revision 1.9  2006/06/29 15:03:58  alex
 * reporting, download photos check in
 *
 * Revision 1.8  2006/04/12 18:30:32  alex
 * back to album links reworked
 *
 * Revision 1.7  2006/04/06 18:31:22  alex
 * display fixed for sports album
 *
 * Revision 1.6  2006/02/22 20:37:25  alex
 * back to album in shopping cart works
 *
 * Revision 1.5  2006/02/22 14:00:51  alex
 * new album nav concept works also in display
 *
 * Revision 1.4  2006/02/20 16:54:49  alex
 * new album nav concept works
 *
 * Revision 1.3  2006/01/24 15:38:35  alex
 * file ending is now .html
 *
 * Revision 1.2  2005/11/19 16:31:43  alex
 * bookmarks of displays should work now
 *
 * Revision 1.1  2005/11/18 17:28:36  alex
 * back link, incl. new interface for naviable objects
 *
 ****************************************************************/
package ch.unartig.studioserver.businesslogic;

import ch.unartig.exceptions.UnartigSessionExpiredException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.beans.AbstractAlbumBean;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class NavigationHelper
{
    static Logger _logger = Logger.getLogger("NavigationHelper");
    /**
     * read the overview from the session and set parameter for a back link
     *
     * @param request
     * @param navObject
     * @throws ch.unartig.exceptions.UnartigException
     * @throws ch.unartig.exceptions.UnartigSessionExpiredException
     */
    public static void setBackToAlbumLink(HttpServletRequest request, NavigableObject navObject) throws UnartigException, UnartigSessionExpiredException
    {
        _logger.debug("setting back link");
        AbstractAlbumBean albumBean = SessionHelper.getAlbumBeanFromSession(request);
//        System.out.println("albumBean.getClass().getName() = " + albumBean.getClass().getName());
        if (albumBean!=null)
        {
            setBackToAlbumLink(navObject, albumBean);
        } else
        {
            _logger.info("no back navigation available ... ");
            // we shouldn't do that: there can be many reasons why an album bean is not available
//            throw new UnartigSessionExpiredException("No album Bean available, cannot set back link");
            navObject.setBackToAlbumParams(null);
        }
    }

    /**
     * set the necessary parameter for a back link on the page
     *
     * @param navObject
     * @param albumBean
     * @throws ch.unartig.exceptions.UnartigException
     */
    public static void setBackToAlbumLink(NavigableObject navObject, AbstractAlbumBean albumBean) throws UnartigException
    {
        Map backParamMap = new HashMap();
        if (albumBean == null)
        {
            // no album available; give back page 1 or throw an exception?
//            backParamMap.put("page","1");
//            navObject.setBackToAlbumParams(backParamMap);
            throw new UnartigException("can not set back to album link, albumbean not available");
        } else
        {
            backParamMap.put(Registry._NAME_PAGE_PARAM,String.valueOf(albumBean.getPage()));
            backParamMap.put(Registry._NAME_ALBUM_TYPE_PARAM, albumBean.getType());
            navObject.setBackToAlbumParams(backParamMap);
        }
    }


}
