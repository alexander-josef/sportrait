/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 05.04.2007$
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
 * Revision 1.4  2007/06/03 21:35:21  alex
 * Bug #1234 : Ordnung eventcategory: wird nun nach als liste gefuehrt, ordnung wird eingehalten
 *
 * Revision 1.3  2007/05/05 12:21:24  alex
 * errorhandling no eventcategory
 *
 * Revision 1.2  2007/05/03 10:02:22  alex
 * startnummernsuche works
 *
 * Revision 1.1  2007/04/17 11:03:27  alex
 * dynamic pager added
 *
 ****************************************************************/
package ch.unartig.studioserver.actions;

import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.beans.SportsAlbumBean;
import ch.unartig.studioserver.businesslogic.SessionHelper;
import ch.unartig.studioserver.model.EventCategory;
import ch.unartig.studioserver.model.SportsEvent;
import ch.unartig.studioserver.persistence.DAOs.EventCategoryDAO;
import ch.unartig.studioserver.persistence.DAOs.PhotoDAO;
import ch.unartig.util.HttpUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.apache.struts.actions.MappingDispatchAction;
import org.hibernate.HibernateException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class EventCategoryAction extends MappingDispatchAction {
    Logger _logger = Logger.getLogger(getClass().getName());


    /**
     * This action will be called to populate photos from an eventCategory
     * todo: what happens to session
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws UnartigException
     */
    public ActionForward showCategory(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UnartigException {
        PhotoDAO photoDAO = new PhotoDAO();
        int page;
        ActionMessages msgs;
        msgs = new ActionMessages();
        String photoId = request.getParameter("photoId");



        DynaActionForm eventCategoryOverviewForm = (DynaActionForm) form; // needed for startnumber search?
        EventCategoryDAO eventCategoryDao = new EventCategoryDAO();
        _logger.debug("populating category, showCategory");
        // Long eventCategoryIdFromForm = (Long) eventCategoryOverviewForm.get("eventCategoryId");


        // String pageFromForm = eventCategoryOverviewForm.getString("page");
        // _logger.debug("params: page [" + pageFromForm + "],eventCategoryId [" + eventCategoryIdFromForm + "]");
        // Form could live already in session or/and if coming from a deep link, form params are not set.
        //
        // * if category changes? --> update sportsAlbumBean
        // * if page changes ? --> update sportsAlbumBean
        // * if eventcategory changes? (not here, check relevant action) --> update page / event / eventCategoryId (?)

        // if "album bean" not in session or if eventCategory of album bean in session not equal to passed eventCategory URL-parameter, reload
        // needs to reload every time ! Could change because of deleted photos.
//        if (
//                albumBeanInSession == null // (re)load if there's no album bean in session
//                || !albumBeanInSession.getEventCategory().getEventCategoryId().equals(eventCategoryIdFromForm) // reload if the event category changed
//                || albumBeanInSession.getPage()!=Integer.parseInt(pageFromForm) // reload if the page changed
//                || ((albumBeanInSession.getStartNumber()!=null) && !albumBeanInSession.getStartNumber().equals(eventCategoryOverviewForm.getString("startNumber"))) // reload if the startnumber changed , ! -> startnumber from albumbean can be null! (only if coming from deep link?)
//                )
//        {
        _logger.debug("SportsAlbumBean not yet in session, creating new one from form (showCategory)");
        SportsAlbumBean sportsAlbumBean;
        sportsAlbumBean = new SportsAlbumBean(HttpUtil.getWebApplicationUrl(request)); // sportsAlbumBean will be newly created, even if an instance already exists in session (no need to use existing)

        SportsEvent event;
        EventCategory eventCategory;
        // used to mark photos that are in the shopping cart:
        try {
            Long eventCategoryId = Long.valueOf(request.getParameter("eventCategoryId"));
            BeanUtils.copyProperties(sportsAlbumBean, eventCategoryOverviewForm); // what's this? --> convenience method to copy form params to bean.
            eventCategory = eventCategoryDao.load(eventCategoryId);
            event = eventCategory.getEvent();
            List list = event.getEventCategories();
            if (eventCategory.getEventCategoryId() == null) {
                // _logger.info("Could not load eventCategory with ID : " + eventCategoryIdFromForm + " -- Showing homepage");
                msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.eventCategory.notFound"));
                saveMessages(request, msgs);
                return mapping.findForward("notFound");
            }
            // make sure correct page is set when returning from new display page
            // get page from photoId that is submitted as a url parameter
            // todo : startnumber?
            _logger.debug("looking up page for startnumber ["+sportsAlbumBean.getStartNumber()+"] and photoId ["+photoId+"]");
            if (photoId!=null && !photoId.isEmpty()) {
                page = photoDAO.getAlbumPageNrFor(Long.valueOf(photoId),eventCategory,sportsAlbumBean.getStartNumber());
                sportsAlbumBean.setPage(page);
            }
            sportsAlbumBean.setEventCategory(eventCategory);
            sportsAlbumBean.setSportsEvent(event);
            sportsAlbumBean.setEventCategories(list);
            sportsAlbumBean.setShoppingCart(SessionHelper.getShoppingCartFromSession(request));

        } catch (HibernateException | NumberFormatException he) {
            _logger.info("Could not load eventCategory with ID -- Showing homepage");
            msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.eventCategory.notFound"));
            saveMessages(request, msgs);
            return mapping.findForward("notFound");
        } catch (IllegalAccessException | InvocationTargetException e) {
            _logger.info("Could not process eventcategory overview -- Showing homepage");
            return mapping.findForward("notFound");
        }

        sportsAlbumBean.populateAlbumBeanTemplate(); // this is where the heavy lifting is happening ...

        request.getSession().setAttribute(Registry._NAME_ALBUM_BEAN_ATTR, sportsAlbumBean);
        request.setAttribute("sportsEvent", event);
//        request.getSession().setAttribute("eventCategories",((SportsEvent)eventCategory.getEvent()).getEventCategories());
//        }

        return mapping.findForward("success");
    }


}
