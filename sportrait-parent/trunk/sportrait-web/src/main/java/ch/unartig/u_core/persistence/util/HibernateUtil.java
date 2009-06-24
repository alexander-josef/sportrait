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
 * Revision 1.6  2006/11/12 11:58:47  alex
 * dynamic album ads
 *
 * Revision 1.5  2006/11/05 12:15:45  alex
 * comments
 *
 * Revision 1.4  2006/11/05 12:04:09  alex
 * changing error handling. rollback runs now smoother with less stacktrace. not yet fully tested
 *
 * Revision 1.3  2006/05/02 20:39:44  alex
 * removing verbose system out
 *
 * Revision 1.2  2005/10/26 14:34:32  alex
 * first version of album overview
 * new mappings in struts for the /album/** url
 *
 * Revision 1.1  2005/09/26 18:37:45  alex
 * *** empty log message ***
 *
 ****************************************************************/
package ch.unartig.u_core.persistence.util;

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.studioserver.model.UserProfile;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class HibernateUtil
{
    private static SessionFactory sessionFactory;

//    private static final ThreadLocal service = new ThreadLocal();
//    private static final ThreadLocal session = new ThreadLocal();
//    private static final ThreadLocal transaction = new ThreadLocal();
//    private static final String _MYOWN = "sKmArKeR";
//    private static final long _SESSION_FACTORY_WAIT_TIME = 2000; // 10 seconds

    /**
     * Tries to create SessionFactory and logs success or failure.
     * Must be called on web application startup.
     */
    public static synchronized void init()
    {
        lazyinit();
    }

    /**
     * Tries to create SessionFactory and logs success or failure.
     * Must be called on web application startup.
     */
    private static synchronized void lazyinit()
    {
        try
        {
            if (sessionFactory == null)
            {
                System.out.println("Going to create SessionFactory ");
                sessionFactory = new Configuration().configure().buildSessionFactory();
//                sessionFactory.openSession();
                System.out.println("Hibernate could create SessionFactory");
            }
        } catch (Throwable t)
        {
            System.out.println("Hibernate could not create SessionFactory");
            t.printStackTrace();
        }
    }

    /**
     * Tries to destroy SessionFactory and logs success or failure.
     * Must be called on web application shutdown.
     */
    public static synchronized void destroy()
    {
        try
        {
            if (sessionFactory != null)
            {
                sessionFactory.close();
                System.out.println("Hibernate could destroy SessionFactory");
            }
        } catch (Throwable t)
        {
            System.out.println("Hibernate could not destroy SessionFactory");
            t.printStackTrace();
        }
        sessionFactory = null;
    }

    /**
     * Does nothing actually, DB connection is acquired on-demand.
     *
     * @see #currentSession
     * @param request Servlet Request
     */
/*
    public static void enterService(HttpServletRequest request)
    {
        if (request.getAttribute(_MYOWN) != null && ((String) request.getAttribute(_MYOWN)).length() != 0)
        {
            return;
        }
        request.setAttribute(_MYOWN, "hibernatefilter");
        if (service.get() == null)
        {
            //            System.out.println("############Entering service setting true "+service.get());
            service.set(Boolean.TRUE); //just something
        } else
        {
            //            System.out.println("$$$$$$$$$$$$$$$$$$$$$ holds lock throwing exception ");
            throw new IllegalStateException("Thread holds service lock.");
        }
    }
*/

    /**
     * If DB connection was acquired, DB connection is released.
     * @param request Servlet Request
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException Exception
     */
/*
    public static void leaveService(HttpServletRequest request) throws UAPersistenceException
    {
        //        if(request.getAttribute(_MYOWN) != null && ((String) request.getAttribute(_MYOWN)).length() != 0)
        //        {
        request.removeAttribute(_MYOWN);
        //        }
        if (service.get() != null)
        {
            //            System.out.println("%%%%%%%%%%%%%leaving service setting null "+service.get());
            service.set(null); //just null
        } else
        {
            //            System.out.println("@@@@@@@@@@@@@@@@@@@@ holds no lock throwing exception");
            //            throw new IllegalStateException("Thread holds no service lock.");
        }

        // check if seesion exists and close
        //            System.out.println("HibernateFilter.doFilter: post-chain, trying to close hibernate session if exists");
        if (HibernateUtil.sessionExists())
        {
            HibernateUtil.closeSession();
        }
    }
*/


    /**
     * Hibernate 3+ style for returning the current session. Uses the "org.hibernate.context.ThreadLocalSessionContext" .
     * @return a hibernate Session according to the session context class (threadlocal session context)
     */
    public static Session currentSession()
    {
        // make sure we have a factory:
        lazyinit();
        try
        {
//            System.out.println("**************************** Getting session from factory : " + sessionFactory + "*************************************");
            Session session = sessionFactory.getCurrentSession();
            // start a transaction. We will always need a transation. If beginTransaction will be called again for the same unit of work, it should do nothing (??)
            session.beginTransaction();
//            System.out.println("&&&&&&&&&&&&&& Session hashcode: " + session.hashCode());
            return session;
        } catch (HibernateException e)
        {
            throw new RuntimeException("Error getting current session!",e);
        }
    }


    /**
     * Return a transaction that is associated with the current resource
     * @throws UAPersistenceException
     * @deprecated Ask why we manually set a transaction border. Use open session in view pattern?
     */
    public static void beginTransaction() throws UAPersistenceException
    {
        final Session session = currentSession();
        session.beginTransaction();
    }

    /**
     * Commit the open transaction that is associated with the current resource.
     * @throws UAPersistenceException
     * @deprecated Ask why we manually set a transaction border. Use open session in view pattern?

     */
    public static void commitTransaction() throws UAPersistenceException
    {
        final Session session = currentSession();
        Transaction tx = session.getTransaction();
        if (tx != null)
        {
            try
            {
                tx.commit();
                // session commited and closed. New session will be spawned upon calling currentSession on factory.
            } catch (HibernateException e)
            {
                System.out.println("commitTransaction: Throwing Exception e = " + e);
                throw new UAPersistenceException("cannot commit transaction, see stack trace", e);
            }
        } else
        {
            throw new IllegalStateException("Cannot commit transaction, no open transaction available!");
        }

    }

    /**
     * <p>rollback
     * <p>close session
     *
     * @throws UAPersistenceException Hibernate Problems during rollback
     */
    public static void rollbackTransaction() throws UAPersistenceException
    {
        System.out.println("INFO: starting rollback ... check stacktrace for source");
        System.out.println("Stacktrace = " + Thread.currentThread().getStackTrace());
        Thread.currentThread().getStackTrace();
        Thread.dumpStack();
        Transaction tx = currentSession().getTransaction();
        if (tx != null)
        {
            try
            {
                tx.rollback();
            } catch (HibernateException e)
            {
                System.out.println("exception in rollback : " + e.getMessage());
                throw new UAPersistenceException("cannot rollback transaction, see stack trace", e);
            }
        } else
        {
            System.out.println("transaction is null in rollback");
            throw new IllegalStateException("Cannot rollback transaction, no open transaction available!");
        }
    }

    /**
     * Checks for open transaction. If one exists, rollback will be applied, session will be flushed and closed
     * @deprecated  this call is necessary anymore, handeled in filter
     */
    public static void finishTransaction()
    {
        if (currentSession().getTransaction().isActive())
        {
            try
            {
                System.out.println("INFO: Transaction not null in Finish Transaction");
                Thread.dumpStack();
                rollbackTransaction();
//                throw new UAPersistenceException("Incorrect Transaction handling! While finishing transaction, transaction still open. Rolling Back.");
            } catch (UAPersistenceException e)
            {
                System.out.println("Finish Transaction threw an exception. Don't know what to do here. TODO find solution for handling this situation");
            }
        }
    }

    /**
     * Return a list of objects matching the given hibernate query string, with the given
     * parameter value map.
     *
     * @param query      the Hibernate Query used to find the objects.
     * @param parameters a map of named parameters in the query
     * @return a list of found objects
     * @throws UAPersistenceException if the objects cannot be loaded, or the query is faulty.
     */
    public static List find(String query, Map parameters) throws UAPersistenceException
    {
        //        System.out.println("HibernateUtil.find: will execute HQL=" + query + "; params=" + parameters);
        Session sess = HibernateUtil.currentSession();
        List retVal;
        try
        {
            Query q = sess.createQuery(query);
            setParameters(parameters, q);
            retVal = q.list();
            // System.out.println("HibernateUtil.find: query result=" + retVal);
        } catch (HibernateException e)
        {
            throw new UAPersistenceException("HibernateUtil.find:: problems creating query, see stack trace", e);
        }
        return retVal;
    }

    public static List pagedFind(String query, Map parameters, int firstResult, int maxResults) throws UAPersistenceException
    {
        //        System.out.println("HibernateUtil.find: will execute HQL=" + query + "; params=" + parameters);
        Session sess = HibernateUtil.currentSession();
        List retVal;
        try
        {
            Query q = sess.createQuery(query);
            q.setFirstResult(firstResult)
                    .setMaxResults(maxResults);
            setParameters(parameters, q);
            retVal = q.list();
            // System.out.println("HibernateUtil.find: query result=" + retVal);
        } catch (HibernateException e)
        {
            throw new UAPersistenceException("HibernateUtil.find:: problems creating query, see stack trace", e);
        }
        return retVal;
    }


    /**
     * query (with given params map) returns exactly one result
     *
     * @param query      Qury
     * @param parameters Query parameters
     * @return result Object
     * @throws UAPersistenceException Hibernate exception
     */
    public static Object getUnique(String query, Map parameters) throws UAPersistenceException
    {
        Session sess = HibernateUtil.currentSession();
        Object retVal;
        try
        {
            Query q = sess.createQuery(query);
            setParameters(parameters, q);
            retVal = q.uniqueResult();

        } catch (HibernateException e)
        {
            throw new UAPersistenceException("problems creating query, see stack trace", e);
        }
        return retVal;
    }

    public static Object getUnique(String query) throws UAPersistenceException
    {
        return getUnique(query, new HashMap());
    }

    /**
     * constructs the parameters for a query
     *
     * @param parameters the query parameter to populate the query with
     * @param query      the Hibernate Query
     * @throws HibernateException Hibernate Error
     */
    private static void setParameters(Map parameters, Query query) throws HibernateException
    {
        for (Iterator iterator = parameters.keySet().iterator(); iterator.hasNext();)
        {
            String key = (String) iterator.next();
            Object val = parameters.get(key);

            if (val instanceof Object[])
            {
                query.setParameterList(key, (Object[]) val);
            } else if (val instanceof Collection)
            {
                query.setParameterList(key, (Collection) val);
            } else
            {
                query.setParameter(key, val);
            }
        }
    }


    public static void evictFromFactory(UserProfile userProfile)
    {
        System.out.println("Session Factory: evicting class = " + userProfile.getClass());
        sessionFactory.evict(userProfile.getClass());
    }
}
