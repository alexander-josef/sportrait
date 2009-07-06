/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since Oct 25, 2005$
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
 * Revision 1.23  2006/12/05 22:51:56  alex
 * album kann jetzt freigeschaltet werden oder geschlossen sein
 *
 * Revision 1.22  2006/11/12 11:58:47  alex
 * dynamic album ads
 *
 * Revision 1.21  2006/03/20 15:33:33  alex
 * first check in for new sports album logic and db changes
 *
 * Revision 1.20  2006/02/22 14:00:51  alex
 * new album nav concept works also in display
 *
 * Revision 1.19  2006/02/20 16:54:49  alex
 * new album nav concept works
 *
 * Revision 1.18  2006/02/10 14:21:49  alex
 * admin tool: edit level partly ...
 *
 * Revision 1.17  2006/02/07 14:48:53  alex
 * bug 820 and minor refactorings
 *
 * Revision 1.16  2006/01/27 09:30:36  alex
 * new pager implemenatation
 *
 * Revision 1.15  2005/11/28 17:52:16  alex
 * bug fixes
 *
 * Revision 1.14  2005/11/25 10:56:58  alex
 *
 * Revision 1.13  2005/11/21 17:52:58  alex
 * no account action , photo order
 *
 * Revision 1.12  2005/11/19 16:31:43  alex
 * bookmarks of displays should work now
 *
 * Revision 1.11  2005/11/19 11:08:20  alex
 * index navigation works, (extended GenericLevel functions)
 * wrong calculation of fixed checkout overview eliminated
 *
 * Revision 1.10  2005/11/14 14:40:05  alex
 * back to album from shopping cart works
 *
 * Revision 1.9  2005/11/05 21:41:57  alex
 * overview und links in tree menu
 *
 * Revision 1.8  2005/11/05 16:37:25  alex
 * tiles error, more sc stuff
 *
 * Revision 1.7  2005/11/05 16:00:41  alex
 * tiles error, more sc stuff
 *
 * Revision 1.6  2005/11/05 10:32:14  alex
 * shopping cart and minor problems, exception handling
 *
 * Revision 1.5  2005/11/01 11:28:39  alex
 * pagination works; put logic in overview bean
 *
 * Revision 1.4  2005/10/28 10:00:20  alex
 * layout changes
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
package ch.unartig.studioserver.actions;

import ch.unartig.u_core.Registry;
import ch.unartig.u_core.util.SessionHelper;
import ch.unartig.u_core.model.Album;
import ch.unartig.u_core.beans.AlbumBean;
import ch.unartig.u_core.persistence.DAOs.GenericLevelDAO;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;

/**
 * This action class is called if an album shall be presented in the view
 * reads from url the necessay infos
 *
 * @author Alexander Josef 2005
 */
public class AlbumAction extends Action
{
    Logger _logger = Logger.getLogger(getClass().getName());

    /**
     * check params and populate album
     *
     * @param actionMapping
     * @param actionForm
     * @param request
     * @param httpServletResponse
     * @return view
     */
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse httpServletResponse) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException
    {
        ActionMessages msgs = new ActionMessages();
        GenericLevelDAO glDao = new GenericLevelDAO();
        Album album;
        String albumPath = actionMapping.getParameter();
        _logger.debug("actionMapping.getParameter() = " + albumPath);
        String albumIdPart = albumPath.split("/")[0];
        Long albumId = null;
        AlbumBean albumBean = new AlbumBean();
        DynaActionForm albumForm = (DynaActionForm)actionForm;
        BeanUtils.copyProperties(albumBean,albumForm);
        try
        {
            albumId = new Long(albumIdPart);
            album = (Album) glDao.load(albumId, Album.class);
            albumBean.setAlbum(album);
            _logger.debug("going to process album for [" + albumBean.getAlbum().getGenericLevelId().toString() + "]");
            _logger.debug(albumBean);
            albumBean.setShoppingCart(SessionHelper.getShoppingCartFromSession(request));
            albumBean.populateAlbumBeanTemplate();
        } catch (NumberFormatException e)
        {
            msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("album.populate.problem"));
            _logger.error("the album Id part in the URL could not be parsed into a Long : " + albumIdPart, e);
            return actionMapping.findForward("error");
        }
        request.setAttribute("indexNavEntries", albumBean.getAlbum().getIndexNavEntries());
        // set the level to be available as request param throughout the page
        request.setAttribute("level", album);
//        request.setAttribute("albumBean",albumBean);
        request.getSession().setAttribute(Registry._NAME_ALBUM_BEAN_ATTR,albumBean);
        saveMessages(request, msgs);
        return actionMapping.findForward("album");
    }
}
