/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Nov 5, 2005$
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
 * Revision 1.7  2006/05/04 18:04:13  alex
 * exception handling
 *
 * Revision 1.6  2006/03/20 15:33:33  alex
 * first check in for new sports album logic and db changes
 *
 * Revision 1.5  2006/01/09 21:34:28  alex
 * bug 943
 *
 * Revision 1.4  2005/11/20 21:24:32  alex
 * side navigation fixes
 *
 * Revision 1.3  2005/11/19 11:08:20  alex
 * index navigation works, (extended GenericLevel functions)
 * wrong calculation of fixed checkout overview eliminated
 *
 * Revision 1.2  2005/11/08 09:33:45  alex
 * languages
 *
 * Revision 1.1  2005/11/05 21:41:57  alex
 * overview und links in tree menu
 *
 ****************************************************************/
package ch.unartig.studioserver.actions;

import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.model.GenericLevel;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

// todo delete me if not needed anymore
public class OverviewAction extends Action
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
        GenericLevelDAO glDao = new GenericLevelDAO();
        String overviewPath = actionMapping.getParameter();
        _logger.debug("actionMapping.getParameter() = " + overviewPath);
        String overviewIdPart = overviewPath.split("/")[0];
        Long levelOverviewId = null;
        String forwardView="overview";
        GenericLevel level;
        levelOverviewId = new Long(overviewIdPart);
        level = glDao.load(levelOverviewId, GenericLevel.class);
        _logger.debug("going to process overview for level [" + level.getGenericLevelId().toString() + "]");
        _logger.debug("");
        processOverviewBean(request, level);
        request.setAttribute("level", level);
        return actionMapping.findForward(forwardView);
    }


    /**
     * list at most [Registry._MAX_ENTRIES_FOR_OVERVIEW] entries of the level and put the list to the request
     *
     * @param request
     * @param level
     */
    private void processOverviewBean(HttpServletRequest request, GenericLevel level)
    {
        List allChildren = level.listChildren();
        List levelEntries = null;
        try
        {
            levelEntries = allChildren.subList(0, allChildren.size() > Registry._MAX_ENTRIES_FOR_OVERVIEW ? Registry._MAX_ENTRIES_FOR_OVERVIEW : allChildren.size());
        } catch (Exception e)
        {
            // exception handling missing
            _logger.error("Exception. Probably null for allChildren",e);
        }
        request.setAttribute("overviewEntriesList", levelEntries);
    }
}
