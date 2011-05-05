/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since 27.02.2006$
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
 * Revision 1.2  2006/04/30 16:21:27  alex
 * removing system.outs
 *
 * Revision 1.1  2006/02/28 14:57:46  alex
 * added more resources (email for order confirmation), small fixes
 *
 ****************************************************************/
package ch.unartig.studioserver.actions;

import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PrivateAuthorizeAction extends Action
{
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse httpServletResponse)
    {
        DynaActionForm authorizeForm = (DynaActionForm)actionForm;
        String accessKey = authorizeForm.getString("accessKey");

        if ("ECZ-9283".equals(accessKey))
        {
            return new ActionForward("/overview/" + request.getSession().getAttribute("privateOverviewPath") + ".html",true);
        } else
        {
            return actionMapping.findForward("notAuthorized");
        }
    }
}
