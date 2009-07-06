/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Nov 2, 2005$
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
 * Revision 1.4  2006/05/04 17:58:02  alex
 * error handling
 *
 * Revision 1.3  2006/05/02 18:59:11  alex
 * logging exception for malfurmed url
 *
 * Revision 1.2  2006/01/10 15:49:56  alex
 * removing the troubeling debug statement
 *
 * Revision 1.1  2005/11/03 15:50:55  alex
 * languages and upload stuff
 *
 ****************************************************************/
package ch.unartig.u_core.actions;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import ch.unartig.u_core.exceptions.UnartigException;

public class UnartigLocaleAction extends Action
{
    Logger _logger = Logger.getLogger(getClass().getName());

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse httpServletResponse) throws UnartigException, MalformedURLException
    {
        if (_logger.isDebugEnabled())
        {
            _logger.debug("if the request needs debugging info fix the following method : DebugUtils.debugRequest(request)");
            // this hangs
//            DebugUtils.debugRequest(request);
        }
        String page=null;
        String pageUrl;
//        set page to refering url
        pageUrl = request.getHeader("Referer");
        _logger.debug("pageUrl = " + pageUrl);

        try
        {
            page = new URL(pageUrl).getFile();
        } catch (MalformedURLException e)
        {
            _logger.info("pageUrl = " + pageUrl);
            _logger.info("referer : " + request.getHeader("Referer"));
            _logger.error("URL exception !!",e);

//            throw new UnartigException("malformed url exception ...",e);
        }
        if (page==null)
        {
            page = "/index.html";
        }
        _logger.debug("page = " + page);


        String language = ((DynaActionForm) actionForm).getString("language");
        _logger.debug("language = " + language);

        HttpSession session = request.getSession();
        Locale locale = getLocale(request);

        boolean isLanguage = (language != null && language.length() > 0);

        if (isLanguage)
        {
            locale = new java.util.Locale(language, "");
        }
        // use forwardaction to locale action
        session.setAttribute(Globals.LOCALE_KEY, locale);

        _logger.debug("Locale Language: " + locale.getDisplayLanguage());
        return new ActionForward(page,true);
    }
}
