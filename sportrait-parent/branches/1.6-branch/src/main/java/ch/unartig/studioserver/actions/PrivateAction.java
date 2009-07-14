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
 * Revision 1.1  2006/02/28 14:57:46  alex
 * added more resources (email for order confirmation), small fixes
 *
 ****************************************************************/
package ch.unartig.studioserver.actions;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PrivateAction extends Action
{

    Logger _logger = Logger.getLogger(getClass().getName());

    /**
     * reads the level of the overview to be presented.<br/>
     *
     * @param actionMapping
     * @param actionForm
     * @param request
     * @param httpServletResponse
     * @return overview forward
     */
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse httpServletResponse)
    {
        String overviewPath = actionMapping.getParameter();

        _logger.debug("Private Action actionMapping.getParameter() = " + overviewPath);
        String overviewIdPart = overviewPath.split("/")[0];
        request.getSession().setAttribute("privateOverviewPath",overviewPath);

        return actionMapping.findForward("success");
    }
}
