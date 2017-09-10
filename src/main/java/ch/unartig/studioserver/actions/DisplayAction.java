/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since Nov 1, 2005$
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
 * Revision 1.16  2006/05/01 12:43:48  alex
 * fix for album reload for sports and event album
 *
 * Revision 1.15  2006/04/19 21:31:53  alex
 * session will be restored with album-bean (i.e. for bookmarked urls or so...)
 *
 * Revision 1.14  2006/04/06 18:31:22  alex
 * display fixed for sports album
 *
 * Revision 1.13  2006/02/24 17:15:07  alex
 * display title hint removed, throwing an exception when session is expired upon displayview
 *
 * Revision 1.12  2006/02/22 20:37:25  alex
 * back to album in shopping cart works
 *
 * Revision 1.11  2006/02/22 16:10:25  alex
 * added back link
 *
 * Revision 1.10  2006/02/22 14:00:51  alex
 * new album nav concept works also in display
 *
 * Revision 1.9  2006/02/20 16:54:49  alex
 * new album nav concept works
 *
 * Revision 1.8  2005/11/25 10:56:58  alex
 *
 * Revision 1.7  2005/11/20 21:34:22  alex
 * last first photo fix in display view
 *
 * Revision 1.6  2005/11/19 16:31:43  alex
 * bookmarks of displays should work now
 *
 * Revision 1.5  2005/11/18 17:28:36  alex
 * back link, incl. new interface for naviable objects
 *
 * Revision 1.4  2005/11/05 21:41:57  alex
 * overview und links in tree menu
 *
 * Revision 1.3  2005/11/05 15:22:30  alex
 * more shopping cart ...
 *
 * Revision 1.2  2005/11/02 23:22:16  alex
 * locales are now chosen according to language-links
 *
 * Revision 1.1  2005/11/02 09:10:09  alex
 * album view works
 *
 ****************************************************************/
package ch.unartig.studioserver.actions;

import ch.unartig.exceptions.UnartigSessionExpiredException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.beans.AbstractAlbumBean;
import ch.unartig.studioserver.beans.AlbumBean;
import ch.unartig.studioserver.beans.DisplayBean;
import ch.unartig.studioserver.beans.SportsAlbumBean;
import ch.unartig.studioserver.businesslogic.SessionHelper;
import ch.unartig.studioserver.model.Album;
import ch.unartig.studioserver.model.Photo;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;

public class DisplayAction extends Action
{
    Logger _logger = Logger.getLogger(getClass().getName());

    /**
     * Action to prepare the display of a photo
     * <ul>
     * <li>shows the display photo,
     * <li>provides back link,
     * <li> provides button to place in shopping cart
     * </ul>
     *
     * This action could be called from a deep link, we cannot assume session data to be present.
     *
     * @param actionMapping
     * @param actionForm
     * @param request
     * @param httpServletResponse
     * @return
     * @throws ch.unartig.exceptions.UnartigException
     */
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse httpServletResponse)

    {
        DynaActionForm eventCategoryOverviewForm = (DynaActionForm) actionForm;

        DisplayBean displayBean;
        String photoPath = actionMapping.getParameter();
        _logger.debug("actionMapping.getParameter() = " + photoPath);
        String photoIdString = photoPath.split("/")[0];
        Long displayPhotoId = new Long(photoIdString);
        displayBean = new DisplayBean(displayPhotoId);
        Album albumFromPhotoID = displayBean.getAlbumFromPhoto(); // needed in case of deep linking. Could we avoid it? Don't think so. Check for performance penalties (SQL)
        // or just the photo list??
        AbstractAlbumBean albumBean = SessionHelper.getAlbumBeanFromSession(request);
        if (albumBean == null // we're called without an album in the session - we need to load an album from the given URL parameter (display photo ID?)
                || !albumFromPhotoID.getEventCategory().getEventCategoryId().equals(((SportsAlbumBean) albumBean).getEventCategory().getEventCategoryId())) // we're called from a deeplink - eventcategory in session differs from photoId in URL - reload!
        {
            _logger.info("album not found in session or eventcategory differs from session (deep links)... reloading ...");
            albumBean = albumFromPhotoID.getAlbumBean();
            try {
                albumBean.reloadPhotosTemplate(displayPhotoId);
            } catch (UnartigException e) {
                _logger.warn(e);
                // todo handle exception
            }
            request.getSession().setAttribute(Registry._NAME_ALBUM_BEAN_ATTR,albumBean);
            _logger.debug("set albumbean to session");
        }
        // else we assume the correct album is in the session. is it?

        displayBean.setAlbumBean(albumBean);

        try {
            displayBean.processDisplayBean();
        } catch (UnartigException e) {
            _logger.warn(e);
            // todo: check why we don't get here in case of an exception
            // todo handle exception
        }

        eventCategoryOverviewForm.set("page", String.valueOf(albumBean.getPage())); // make sure page is set in dynaactionform in case the page number has been newly calculated
        eventCategoryOverviewForm.set("eventCategoryId", ((SportsAlbumBean) albumBean).getEventCategoryId()); // make sure eventCategoryId is set in dynaactionform in case the page number has been newly calculated
        request.setAttribute("display", displayBean);
        return actionMapping.findForward("display");
    }

    private void debugDisplayPhotoView(Photo displayPhoto, AlbumBean albumBean)
    {
        _logger.debug("#######################################################################");
        _logger.debug("PHOTO ID OF Displayphoto : " + displayPhoto.getPhotoId().toString());
        for (int i = 0; i < albumBean.getPhotos().size(); i++)
        {
            Photo photo = (Photo) albumBean.getPhotos().get(i);
            _logger.debug("Overview contains PhotoID : " + photo.getPhotoId().toString());
            _logger.debug("equals ? : " + photo.equals(displayPhoto));
        }
        _logger.debug("#######################################################################");
    }


}
