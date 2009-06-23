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
 * Revision 1.2  2007/03/27 16:39:17  alex
 * refactored studioalbum into album
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.5  2006/04/30 16:21:27  alex
 * removing system.outs
 *
 * Revision 1.4  2006/04/29 23:32:07  alex
 * many sola features, bugs, hibernate config
 *
 * Revision 1.3  2006/03/21 17:17:03  alex
 * sportsalbum changes, empty etappe now works
 *
 * Revision 1.2  2006/03/20 17:20:37  alex
 * ui improvements, sportsalbum
 *
 * Revision 1.1  2006/03/20 15:33:33  alex
 * first check in for new sports album logic and db changes
 *
 ****************************************************************/
package ch.unartig.studioserver.actions;

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.u_core.exceptions.UnartigException;
import ch.unartig.u_core.Registry;
import ch.unartig.u_core.util.SessionHelper;
import ch.unartig.u_core.presentation.EventAlbum;
import ch.unartig.u_core.model.Album;
import ch.unartig.u_core.model.SportsAlbum;
import ch.unartig.u_core.model.Event;
import ch.unartig.studioserver.beans.SportsAlbumBean;
import ch.unartig.u_core.persistence.DAOs.GenericLevelDAO;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;

/**
 * this action class is called if an album with 'Startnumber' Photos shall be presented in the view <br>
 * reads from url the necessay infos<br>
 * delegate
 *
 * @author Alexander Josef 2006
 */
public class SportsAlbumAction extends Action
{
    Logger _logger = Logger.getLogger(getClass().getName());

    /**
     * check params and populate a sports album<br/>
     * - if called from sportsevent overview and a valid startnumber is given, all matching photos in the *event* shall be shown<br/>
     * - if called from within the album: if no number is given, all photos shall be shown, if number is given, only matching photos within album
     *
     * @param actionMapping
     * @param actionForm
     * @param request
     * @param httpServletResponse
     * @return view
     * @throws IllegalAccessException
     */
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse httpServletResponse) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, UnartigException
    {
        GenericLevelDAO glDao = new GenericLevelDAO();
        ActionMessages msgs = new ActionMessages();
        Album album;
        DynaActionForm sportsAlbumForm;
        SportsAlbumBean sportsAlbumBean;
        sportsAlbumBean = new SportsAlbumBean();
        sportsAlbumForm = (DynaActionForm) actionForm;
        /*copy all properties to album bean if they are set in sportsAlbumForm; silently ignores unknown properties*/
        _logger.debug("value of http-parameter etappe : " + sportsAlbumForm.getString("etappe"));
        _logger.debug("value of http-parameter startNumber : " + sportsAlbumForm.getString("startNumber"));
        BeanUtils.copyProperties(sportsAlbumBean, sportsAlbumForm);
        _logger.debug("value of bean after copyProperties : " + sportsAlbumBean.getEtappe());
        _logger.debug("value of bean after copyProperties : " + sportsAlbumBean.getStartNumber());

        _logger.debug("callingLevel = " + sportsAlbumForm.getString("callingLevel"));

        // only if no etappe has benn selected and no startnumber is entered we return to the overviews:
        if (noEtappeSelected(sportsAlbumBean, sportsAlbumForm.getString("callingLevel")) && (sportsAlbumBean.getStartNumber()==null || "".equals(sportsAlbumBean.getStartNumber().trim())))
        {
            return returnToOverview(sportsAlbumBean, sportsAlbumForm, glDao, request);
        }

        _logger.debug("going to populate Album:");

        try
        {
            // manually set eventAlbum param. will not be reset because action form stays in session
            sportsAlbumBean.setEventAlbum(request.getParameter("eventAlbum")!=null && "true".equals(request.getParameter("eventAlbum")));
            album = extractAlbumIdFromRequest(sportsAlbumBean, actionMapping);
            // what do we set here in case of showing all photos for event with startnumber selected?
            sportsAlbumBean.setAlbum(album);
            _logger.debug("going to process overview for album [" + sportsAlbumBean.getAlbum().getGenericLevelId().toString() + "]");
            _logger.debug(sportsAlbumBean);
            sportsAlbumBean.setShoppingCart(SessionHelper.getShoppingCartFromSession(request));
            sportsAlbumBean.populateAlbumBeanTemplate();
        } catch (NumberFormatException e)
        {
            msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("album.populate.problem"));
            _logger.error("the album Id part in the URL could not be parsed into a Long : " + actionMapping.getParameter(), e);
            return actionMapping.findForward("error");
        } catch (UAPersistenceException e)
        {
            msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("album.populate.problem"));
            _logger.error("the album id could not be loaded from the db", e);
            return actionMapping.findForward("error");
        } catch (UnartigException e)
        {
            msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("album.populate.problem"));
            _logger.error("the album could not be populated", e);
            return actionMapping.findForward("error");
        }
        request.setAttribute("indexNavEntries", sportsAlbumBean.getAlbum().getIndexNavEntries());
        if (sportsAlbumBean.getAlbum() instanceof EventAlbum)
        {
            _logger.debug("!!!!!!!!! EVENT ALBUM");
            request.setAttribute("level", sportsAlbumBean.getAlbum().getEvent());
        } else
        {
            request.setAttribute("level", sportsAlbumBean.getAlbum());
        }

        request.setAttribute("sportsAlbumList", album.getEvent().getSportsAlbums());

        request.getSession().setAttribute(Registry._NAME_ALBUM_BEAN_ATTR, sportsAlbumBean);
        saveMessages(request, msgs);

        return actionMapping.findForward("album");
    }

    private ActionForward returnToOverview(SportsAlbumBean sportsAlbumBean, DynaActionForm sportsAlbumForm, GenericLevelDAO glDao, HttpServletRequest request) throws UAPersistenceException
    {
        Album album;
        String eventActionUrl = "/sportsOverview/";
        sportsAlbumBean.setEtappe(null);
        _logger.debug("callingLevel = " + sportsAlbumForm.getString("callingLevel"));
        _logger.debug("sportsAlbumBean.isAlbumLevel() = " + sportsAlbumBean.isAlbumLevel());
        if (sportsAlbumBean.isAlbumLevel())
        { // no etappe select and we are in an album
            album = (SportsAlbum) glDao.load(new Long(sportsAlbumBean.getLevelId()), SportsAlbum.class);
            eventActionUrl+=album.getEvent().getGenericLevelId().toString();
            _logger.debug("ALBUM: returning URL : ["+eventActionUrl+"]");
        }
        else
        {// no etappe selected AND no startnumber given and we are in overview
//            System.out.println("eventActionUrl = " + eventActionUrl);
//            System.out.println("sportsAlbumBean.getLevelId = " + sportsAlbumBean.getLevelId());
//            System.out.println("request.getParameter(\"levelId\") = " + request.getParameter("levelId"));
//            System.out.println("sportsAlbumForm.get('levelId') = " + sportsAlbumForm.get("levelId"));
            eventActionUrl+=sportsAlbumBean.getLevelId();
            _logger.debug("OVERVIEW: returning URL : ["+eventActionUrl+"]");
        }
        eventActionUrl+="/overview.html";
        return new ActionForward(eventActionUrl);
    }

    /**
     * returns true if no page == zero AND etappe <0 AND calling level not empty
     * when could callinglevel be empty?
     * @param sportsAlbumBean
     * @param callingLevel
     * @return true if no page == zero AND etappe <0 AND calling level not empty
     */
    private boolean noEtappeSelected(SportsAlbumBean sportsAlbumBean, String callingLevel)
    {
        return sportsAlbumBean.getPage() == 0 && sportsAlbumBean.getEtappe().intValue() < 0 && callingLevel != null && !"".equals(callingLevel);
    }

    /**
     * if page == 0 access by parameter
     * <br>
     *
     * @param sportsAlbumBean
     * @param actionMapping
     * @return the album genericlevelid
     * @throws ch.unartig.u_core.exceptions.UnartigException
     */
    private Album extractAlbumIdFromRequest(SportsAlbumBean sportsAlbumBean, ActionMapping actionMapping) throws UnartigException {
        Album retVal;
        GenericLevelDAO glDao = new GenericLevelDAO();
//        System.out.println("sportsAlbumBean.getEtappe() = " + sportsAlbumBean.getEtappe());
        _logger.debug("sportsAlbumBean.isEventAlbum() = " + sportsAlbumBean.isEventAlbum());

        if (sportsAlbumBean.getPage() != 0 && !sportsAlbumBean.isEventAlbum())
        { // page is not null and eventAlbum = false we are not called via the sports album nav form
            String albumIdPart = extractIdFromPath(actionMapping);

            retVal = (Album) glDao.load(new Long(albumIdPart), SportsAlbum.class);
            return retVal;
        } else if (sportsAlbumBean.getPage() == 0 && sportsAlbumBean.getEtappe() != null && !(sportsAlbumBean.getEtappe().intValue() < 0))
        {   // we are called via the nav form; etappe set
            // important: initialize page to 1
            sportsAlbumBean.setPage(1);

            retVal = (Album) glDao.load(sportsAlbumBean.getEtappe(), SportsAlbum.class);
            return retVal;
        } else if ((sportsAlbumBean.getPage() == 0 || sportsAlbumBean.isEventAlbum()) && sportsAlbumBean.getEtappe() != null && (sportsAlbumBean.getEtappe().intValue() < 0))
        {// we are called via the nav form; etappe NOT set; either page = 0 ore eventAlbum=true; we are returning special album eventAlbum

            _logger.debug("sports event called with startnumber selected. showing all photos for event ["+sportsAlbumBean.getLevelId()+"]");
            Event event;
            if (!sportsAlbumBean.isAlbumLevel())
            {   // not album level: get level directly from levelid
                event = (Event) glDao.load(new Long(sportsAlbumBean.getLevelId()), Event.class);
            } else
            {   // we're called from album level
                Album album = (Album) glDao.load(new Long(sportsAlbumBean.getLevelId()), SportsAlbum.class);
                event = album.getEvent();
            }


            retVal = new EventAlbum(event,sportsAlbumBean.getStartNumber());

            return retVal;
        } else
        {
            throw new UnartigException("invalid etappe value : " + sportsAlbumBean.getEtappe().toString());
        }
    }

    /**
     * helper to extract the id from the url
     *
     * @param actionMapping
     * @return the id from the path as string
     */
    private String extractIdFromPath(ActionMapping actionMapping)
    {
        String albumPath = actionMapping.getParameter();
        _logger.debug("actionMapping.getParameter() = " + albumPath);
        return albumPath.split("/")[0];
    }

}
