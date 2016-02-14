/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since Sep 21, 2005$
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
 * Revision 1.6  2007/06/09 11:15:37  alex
 * photographer
 *
 * Revision 1.5  2007/05/03 16:05:14  alex
 * startnummernsuche works
 *
 * Revision 1.4  2007/03/29 13:33:44  alex
 * working on photo upload
 *
 * Revision 1.3  2007/03/28 13:52:58  alex
 * edit events page
 *
 * Revision 1.2  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/18 13:28:20  urban
 * initial check in
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.16  2006/10/17 08:07:07  alex
 * creating the order hashes
 *
 * Revision 1.15  2006/06/29 15:03:58  alex
 * reporting, download photos check in
 *
 * Revision 1.14  2006/04/30 16:21:27  alex
 * removing system.outs
 *
 * Revision 1.13  2006/04/29 23:32:07  alex
 * many sola features, bugs, hibernate config
 *
 * Revision 1.12  2006/04/19 21:31:53  alex
 * session will be restored with album-bean (i.e. for bookmarked urls or so...)
 *
 * Revision 1.11  2006/03/21 17:17:03  alex
 * sportsalbum changes, empty etappe now works
 *
 * Revision 1.10  2006/03/20 15:33:33  alex
 * first check in for new sports album logic and db changes
 *
 * Revision 1.9  2006/01/27 09:30:36  alex
 * new pager implemenatation
 *
 * Revision 1.8  2005/11/25 11:09:09  alex
 * removed system outs
 *
 * Revision 1.7  2005/11/25 10:56:58  alex
 *
 * Revision 1.6  2005/11/22 21:33:16  alex
 * ordering process
 *
 * Revision 1.5  2005/11/22 19:45:46  alex
 * admin actions, configurations
 *
 * Revision 1.4  2005/11/06 21:43:22  alex
 * overview, admin menu, index-photo upload
 *
 * Revision 1.3  2005/10/24 13:50:07  alex
 * upload of album
 * import in db 
 * processing of images
 *
 * Revision 1.2  2005/10/03 10:48:05  alex
 * *** empty log message ***
 *
 * Revision 1.1  2005/09/26 18:37:45  alex
 * *** empty log message ***
 *
 ****************************************************************/
package ch.unartig.controller;

import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.businesslogic.PhotoOrderService;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import ch.unartig.util.CryptoUtil;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;


public class UnartigActionServlet extends ActionServlet
{
    Logger logger = Logger.getLogger(getClass().getName());

    /**
     * This is called only upon servlet startup
     * init constants in registry from properties file
     * create the tree for the navigation
     * don't forget to call the super!
     * @throws ServletException
     */
    public void init() throws ServletException
    {
        logger.debug("@@ init unartig action servlet  WITH   STRUTS");
        logger.debug("Calling init on Registry");
        try
        {
        Registry.init();

            logger.info("Init security");
            CryptoUtil.setPrng(SecureRandom.getInstance("SHA1PRNG"));
//            logger.info("new navigation tree for tigra tree menu generated!");
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("Exception while creating SecureRandom instance",e);
             throw new ServletException("initialization failed",e);
         } catch (ClassNotFoundException e) {
            logger.error("Exception during Servlet init, class not found", e);
            throw new ServletException("initialization failed",e);
        } catch (InstantiationException e) {
            logger.error("Exception during Servlet init, cannot instantiate class", e);
            throw new ServletException("initialization failed",e);
        } catch (IllegalAccessException e) {
            logger.error("Exception during Servlet init", e);
            throw new ServletException("initialization failed",e);
        }

        // todo: clean up. Remove order service if not needed anymore
        PhotoOrderService orderService = PhotoOrderService.getInstance();
        // no service needed to batch-process orders currently:
//        orderService.startService();
//        System.out.println("UnartigActionServlet.init : STARTED ORDER SERVICE !");

        //Initialize SecureRandom
        //This is a lengthy operation, to be done only upon
        //initialization of the application
        try {
            //noinspection UnusedDeclaration
            SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            logger.error("Exception while initializing SecureRandom instance",e);
        }
        super.init();
    }

    public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException
    {

        // Todo google for this code ... where does that come from and why? There should be no HEAD method coming in here ...
        // check for HEAD method
        if ("HEAD".equals(httpServletRequest.getMethod())) {

            // commit response, return. nothing to do.
            logger.info("HEAD request .... returning without forwarding to the controler.");
            httpServletResponse.flushBuffer();
            return;
        }
        logger.debug("@@ processing doGet()");
        logger.debug("@@ processing requestURL : "  + httpServletRequest.getRequestURL());
        handleClientAuthorization(httpServletRequest);
        super.doGet(httpServletRequest, httpServletResponse);
    }

    public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException
    {
        logger.debug("@@ processing doPost()");
        handleClientAuthorization(httpServletRequest);
        // this removes the userprofile from this thread. If used later, a new session shall be attached to a new thread
        super.doPost(httpServletRequest, httpServletResponse);
    }

    /**
     * Extending the Struts request handling.
     * <p>Process an HTTP "PUT" request.</p>
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public void doPut(HttpServletRequest request,
              HttpServletResponse response)
        throws IOException, ServletException {

        process(request, response);

    }

    /**
     * Extending the Struts request handling.
     * <p>Process an HTTP "DELETE" request.</p>
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public void doDelete(HttpServletRequest request,
              HttpServletResponse response)
        throws IOException, ServletException {

        process(request, response);

    }

    /**
     * We need a 'Client' Object in the session to identify a logged in client.
     * Check the client status: do we have an authorized user? make sure the Client object is up-to-date in the session
     * @param request HttpRequest
     * @return the authorized client or null
     */
    private Client handleClientAuthorization(HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        Principal userPrincipal = request.getUserPrincipal();
        Client client = null;
        if (userPrincipal == null && session != null)
        {
            // No authorized user. make sure no client session exists anymore.
            session.removeAttribute(Registry._SESSION_CLIENT_NAME);
        } else if (userPrincipal!=null)
        {
            client = new Client(request);
            // set username to client object and store in session:
            String userName = userPrincipal.getName();
            client.init(userName);
            request.getSession(true).setAttribute(Registry._SESSION_CLIENT_NAME,client);
        }
        return client;
    }

    public void destroy() {
        logger.info("destroying unartig action servlet!");
        logger.info("Going to stop order service ......");
        PhotoOrderService.getInstance().stopService();
        logger.info("..... Order Service stopped!");
        logger.info("Going to stop Hibernate......");
        HibernateUtil.destroy();
        logger.info("..... Service stopped!");
        logger.info("calling destroy on struts action servlet ....");
        super.destroy();
    }

}
