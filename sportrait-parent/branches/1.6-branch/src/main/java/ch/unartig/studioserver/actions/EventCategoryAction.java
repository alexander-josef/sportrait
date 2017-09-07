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
import ch.unartig.studioserver.beans.AbstractAlbumBean;
import ch.unartig.studioserver.beans.SportsAlbumBean;
import ch.unartig.studioserver.businesslogic.SessionHelper;
import ch.unartig.studioserver.model.EventCategory;
import ch.unartig.studioserver.model.SportsEvent;
import ch.unartig.studioserver.persistence.DAOs.EventCategoryDAO;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.apache.struts.actions.MappingDispatchAction;
import org.hibernate.HibernateException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class EventCategoryAction extends MappingDispatchAction
{
    Logger _logger = Logger.getLogger(getClass().getName());


    /**
     * This action will be called to populate photos from an eventCategory
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws UnartigException
     */
    public ActionForward showCategory(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UnartigException
    {
        ActionMessages msgs;
        msgs = new ActionMessages();

        DynaActionForm eventCategoryOverviewForm = (DynaActionForm) form;
        EventCategoryDAO eventCategoryDao = new EventCategoryDAO();
        _logger.debug("populating category, showCategory");
        String eventCategoryIdFromForm = eventCategoryOverviewForm.getString("eventCategoryId");
        String pageFromForm = eventCategoryOverviewForm.getString("page");
        _logger.debug("params: page ["+ pageFromForm +"],eventCategoryId ["+ eventCategoryIdFromForm +"]");
        // todo: Form could live already in session or/and if coming from a deep link, form params are not set. Find better solution or populate form here if needed
        // todo:
        // * if category changes? --> update sportsAlbumBean
        // * if page changes ? --> update sportsAlbumBean
        // * if eventcategory changes? (not here, check relevant action) --> update page / event / eventCategoryId (?)

        // if "album bean" not in session or if eventCategory of album bean in session not equal to passed eventCategory URL-parameter, reload

        SportsAlbumBean albumBeanInSession = (SportsAlbumBean) SessionHelper.getAlbumBeanFromSession(request);

        if (albumBeanInSession == null
                || !albumBeanInSession.getEventCategory().getEventCategoryId().toString().equals(eventCategoryIdFromForm)
                || albumBeanInSession.getPage()!=Integer.parseInt(pageFromForm))
        {
            _logger.debug("SportsAlbumBean not yet in session, creating new one from form (showCategory)");
            SportsAlbumBean sportsAlbumBean;
            sportsAlbumBean = new SportsAlbumBean(); // sportsAlbumBean will be newly created, even if an instance already exists in session (no need to use existing)
            EventCategory eventCategory;
            // used to mark photos that are in the shopping cart:
            try {
                BeanUtils.copyProperties(sportsAlbumBean, eventCategoryOverviewForm); // what's this? --> convenience method to copy form params to bean.
                eventCategory = eventCategoryDao.load(sportsAlbumBean.getEventCategoryId());
                SportsEvent event = eventCategory.getEvent();
                List list = event.getEventCategories();
                if (eventCategory == null || eventCategory.getEventCategoryId() == null) {
                    _logger.info("Could not load eventCategory with ID : " + eventCategoryIdFromForm + " -- Showing homepage");
                    msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.eventCategory.notFound"));
                    saveMessages(request, msgs);
                    return mapping.findForward("notFound");
                }
                sportsAlbumBean.setEventCategory(eventCategory);
                sportsAlbumBean.setSportsEvent(event);
                sportsAlbumBean.setEventCategories(list);
                sportsAlbumBean.setShoppingCart(SessionHelper.getShoppingCartFromSession(request));

            } catch (IllegalAccessException e) {
                _logger.error("problem with params");
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                _logger.error("problem with params");
                e.printStackTrace();
            } catch (HibernateException he) {
                _logger.info("Could not load eventCategory with ID : " + eventCategoryIdFromForm + " -- Showing homepage");
                msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.eventCategory.notFound"));
                saveMessages(request, msgs);
                return mapping.findForward("notFound");
            }

            sportsAlbumBean.populateAlbumBeanTemplate();

            request.getSession().setAttribute(Registry._NAME_ALBUM_BEAN_ATTR, sportsAlbumBean);
//        request.getSession().setAttribute("eventCategories",((SportsEvent)eventCategory.getEvent()).getEventCategories());
        }

        return mapping.findForward("success");
    }


}
