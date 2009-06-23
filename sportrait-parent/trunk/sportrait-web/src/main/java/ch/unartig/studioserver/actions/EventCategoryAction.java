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

import ch.unartig.u_core.exceptions.UnartigException;
import ch.unartig.u_core.Registry;
import ch.unartig.studioserver.beans.SportsAlbumBean;
import ch.unartig.u_core.util.SessionHelper;
import ch.unartig.u_core.model.EventCategory;
import ch.unartig.u_core.model.SportsEvent;
import ch.unartig.u_core.persistence.DAOs.EventCategoryDAO;
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
        _logger.debug("params: page ["+eventCategoryOverviewForm.getString("page")+"],eventCategoryId ["+eventCategoryOverviewForm.getString("eventCategoryId")+"]");
        SportsAlbumBean sportsAlbumBean;
        sportsAlbumBean = new SportsAlbumBean();
        EventCategory eventCategory;
        // used to mark photos that are in the shopping cart:
        try
        {
            BeanUtils.copyProperties(sportsAlbumBean, eventCategoryOverviewForm);
            eventCategory = eventCategoryDao.load(sportsAlbumBean.getEventCategoryId());
            SportsEvent event = eventCategory.getEvent();
            List list = event.getEventCategories();
            if (eventCategory==null || eventCategory.getEventCategoryId()==null)
            {
                _logger.info("Could not load eventCategory with ID : " + eventCategoryOverviewForm.getString("eventCategoryId" + " -- Showing homepage"));
                msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.eventCategory.notFound"));
                saveMessages(request, msgs);
                return mapping.findForward("notFound");
            }
            sportsAlbumBean.setEventCategory(eventCategory);
            sportsAlbumBean.setSportsEvent(event);
            sportsAlbumBean.setEventCategories(list);
            sportsAlbumBean.setShoppingCart(SessionHelper.getShoppingCartFromSession(request));

        } catch (IllegalAccessException e)
        {
            _logger.error("problem with params");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e)
        {
            _logger.error("problem with params");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch(HibernateException he)
        {
            _logger.info("Could not load eventCategory with ID : " + eventCategoryOverviewForm.getString("eventCategoryId") + " -- Showing homepage",he);
            msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.eventCategory.notFound"));
            saveMessages(request, msgs);
            return mapping.findForward("notFound");
        }

        sportsAlbumBean.populateAlbumBeanTemplate();
        
        request.getSession().setAttribute(Registry._NAME_ALBUM_BEAN_ATTR,sportsAlbumBean);
//        request.getSession().setAttribute("eventCategories",((SportsEvent)eventCategory.getEvent()).getEventCategories());

        return mapping.findForward("success");
    }


}
