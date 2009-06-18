/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Nov 8, 2005$
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
 * Revision 1.3  2006/01/10 15:44:56  alex
 * vm1 config files, new property "simulateOrderOnly"
 *
 * Revision 1.2  2005/11/21 17:52:58  alex
 * no account action , photo order
 *
 * Revision 1.1  2005/11/09 09:01:29  alex
 * check out form wizard
 *
 ****************************************************************/
package ch.unartig.studioserver.actions;

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.model.Customer;
import ch.unartig.studioserver.persistence.DAOs.CustomerDAO;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.actions.MappingDispatchAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UserAction extends MappingDispatchAction
{
    Logger _logger = Logger.getLogger(getClass().getName());


    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return a forward depending on logged in page
     */
    public ActionForward login(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    {
        return mapping.findForward("toEnterAddress");
    }

    /**
     * set properties for user: no colorplaza emails and no unartig contact
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward accountOptOut(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    {
        CustomerDAO cDao = new CustomerDAO();
        HttpSession session = request.getSession();
        DynaActionForm dynForm = (DynaActionForm) form;
        Long customerId = (Long) session.getAttribute(Registry._NAME_CUSTOMER_ID_SESSION_ATTR);
        try
        {
            HibernateUtil.beginTransaction();
            Customer customer = cDao.load(customerId);
            customer.setNoUnartigAccount((Boolean) dynForm.get("noUnartigAccount"));
            customer.setNoEmailFromCopla((Boolean) dynForm.get("noCoplaEmails"));
            cDao.saveOrUpdate(customer);
            HibernateUtil.commitTransaction();
        } catch (UAPersistenceException e)
        {
            try
            {
                _logger.error("rolling back", e);

                HibernateUtil.rollbackTransaction();
            } catch (UAPersistenceException e1)
            {
                _logger.error("error while rolling back .... dunno what to do", e1);
            }

        } finally
        {
            HibernateUtil.finishTransaction();
        }
        return mapping.findForward("home");
    }
}