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
 * Revision 1.1  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:42  alex
 * initial commit maven setup no history
 *
 * Revision 1.1  2005/09/26 18:37:45  alex
 * *** empty log message ***
 *
 ****************************************************************/
package ch.unartig.studioserver.persistence.util;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.*;
import java.io.IOException;


public class HibernateFilter implements Filter
{
    final Logger _logger = Logger.getLogger(this.getClass());

    /**
     */
    public void init(FilterConfig filterConfig) throws ServletException
    {
        HibernateUtil.init();
        System.out.println("INITING HIBERNATEFILETER");
        // Initialize hibernate lazily
    }

    /**
     * This filter wrapps a hibernate transaction around the request. This makes sure, that all db calls are within a transaction and that the session closes when the response is returned to the client.
     * Http HEAD methods are skipped!
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
//        _logger.debug("HibernateFilter.doFilter :  pre-chain++++");
        try
        {

            if (request instanceof HttpServletRequest && "HEAD".equals(((HttpServletRequest)request).getMethod())) {
                _logger.debug("Skipping Hibernate Filter for HEAD method; No Filter logic applied.");
                chain.doFilter(request, response);
                return;
            }

//            _logger.debug("Beginning transaction ......... ");
            HibernateUtil.currentSession().beginTransaction();
//            _logger.debug("Open Transaction in current session : " + HibernateUtil.currentSession().hashCode());

//            _logger.debug("HibernateFilter.doFilter: processing additional filters or request");
            chain.doFilter(request, response);
//            _logger.debug("Hibernate Filter after the execution of the request: commits transaction and closes current sesssion :" + HibernateUtil.currentSession().hashCode());
            HibernateUtil.currentSession().getTransaction().commit();
            HibernateUtil.currentSession().close();
//            _logger.debug("..... Hibernate Session closed!");
        }
        catch (Throwable t)
        // Rollback
        {
            _logger.error("Exception in Servlet filter, Rolling back : ",t);
            if (HibernateUtil.currentSession().getTransaction().isActive())
            {
                _logger.info("Open Transaction: Rolling back!");
                try {
                    HibernateUtil.currentSession().getTransaction().rollback();
                    _logger.info("Transaction has been rolled back in filter");

                } catch (Throwable e) {
                    _logger.info("Cannot rollback Transaction after exception!",e);
                    e.printStackTrace();
                }
            }
            throw new ServletException("Exception in Servlet filter: ",t);
        }
    }

    /**
     */
    public void destroy()
    {
        HibernateUtil.destroy();
    }

}
