/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 28.03.2007$
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
 * Revision 1.2  2007/03/29 13:33:44  alex
 * working on photo upload
 *
 * Revision 1.1  2007/03/28 14:42:52  alex
 * home page most recent events are working
 *
 ****************************************************************/
package ch.unartig.studioserver.actions;

import org.apache.struts.action.*;
import org.apache.struts.actions.MappingDispatchAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;

import java.util.List;
import java.util.Date;

/**
 * Action that are used around the home page
 */
public class SportraitHomeAction extends MappingDispatchAction
{
    /**
     * default action when home is called;
     * prepare the recent events
     */
    @SuppressWarnings({"JavaDoc", "UnusedDeclaration"})
    public ActionForward home(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UAPersistenceException
    {

        GenericLevelDAO glDao = new GenericLevelDAO();
        Date today = new Date();
        List recentEvents = glDao.getSportsEventsBefore(today);
        request.setAttribute("recentEvents",recentEvents);
        // only for debugging:
//        Client client = (Client)request.getSession().getAttribute(Registry._SESSION_CLIENT_NAME);
        return mapping.findForward("success");
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws UAPersistenceException
     */
    @SuppressWarnings({"JavaDoc", "UnusedDeclaration"})
    public ActionForward authError(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UAPersistenceException
    {
        ActionMessages actionMesssages = new ActionMessages();
        actionMesssages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("authentication.error"));
        actionMesssages.add("authError", new ActionMessage("authentication.error"));

        saveMessages(request,actionMesssages);
        return home(mapping,form,request,response);
    }

}
