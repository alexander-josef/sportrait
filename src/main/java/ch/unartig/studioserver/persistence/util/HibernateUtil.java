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
package ch.unartig.studioserver.persistence.util;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.studioserver.model.UserProfile;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.Configuration;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class HibernateUtil
{
    private static Logger  _logger = Logger.getLogger("ch.unartig.studioserver.persistence.util.HibernateUtil");

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

                // A SessionFactory is set up once for an application!
                final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                        .configure() // configures settings from hibernate.cfg.xml
                        .build();

                try {
                    _logger.debug("Going to create SessionFactory ");
                    sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
                    _logger.debug("Hibernate could create SessionFactory");
                }

                catch (Exception e) {
                    // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
                    // so destroy it manually.
                    _logger.debug("exception during hibernate session factory creation",e);
                    StandardServiceRegistryBuilder.destroy( registry );
                }




//                sessionFactory = new Configuration().configure().buildSessionFactory();
//                sessionFactory.openSession();
                // outputSchemaDdl();
            }
        } catch (Throwable t)
        {
            _logger.debug("Hibernate could not create SessionFactory",t);
            t.printStackTrace();
        }
    }

    private static void outputSchemaDdl() {
       /*

       MetadataSources metadata = new MetadataSources();

        SchemaExport schemaExport;
        schemaExport = new SchemaExport(metadata.buildMetadata());
        schemaExport.setHaltOnError(true);
        schemaExport.setFormat(true);
        schemaExport.setDelimiter(";");
        schemaExport.setOutputFile("db-schema.sql");
        schemaExport.execute(true, true, false, true);

        */
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
            // Thread.dumpStack();
            Session session = sessionFactory.getCurrentSession();
            _logger.debug("got current session : " + session.hashCode());
            TransactionStatus transactionStatus = session.getTransaction().getStatus();
            _logger.debug("transaction status : " + transactionStatus);
            if (transactionStatus==TransactionStatus.NOT_ACTIVE) {
                _logger.debug("no transaction ? starting new unit of work with a transaction");
                _logger.debug("transaction status OLD : " + session.getTransaction().getStatus());

                session.beginTransaction(); // when to start transaction? here?
                _logger.debug("transaction status NEW : " + session.getTransaction().getStatus());
                _logger.debug("^^^^^^^^^^^ new Transaction started ^^^^^^^^^^^");
            }

            // start a transaction. We will always need a transaction. If beginTransaction will be called again for the same unit of work, it should do nothing (??)
            // not sure here : always start a transaction? will also be started with filter on every request ... causes an exception in Hibernate 5
            // session.beginTransaction();
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
        // Session should be opened with every call to "currentSession()"
        // session.beginTransaction();
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
                _logger.debug("******************** TRANSACTION COMMITED *************************");
                // session commited and closed. New session will be spawned upon calling currentSession on factory.
            } catch (HibernateException e)
            {
                _logger.warn("commitTransaction: Throwing Exception e = " + e);
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
        _logger.info("INFO: starting rollback ... check stacktrace for source");
        _logger.info("Stacktrace = ", new Throwable());
        Transaction tx = currentSession().getTransaction();
        if (tx != null)
        {
            try
            {
                tx.rollback();
                _logger.info("******************** TRANSACTION ROLLBACK *************************");

            } catch (HibernateException e)
            {
                _logger.warn("exception in rollback : ",e);
                throw new UAPersistenceException("cannot rollback transaction, see stack trace", e);
            }
        } else
        {
            _logger.info("transaction is null in rollback");
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
                _logger.info("INFO: Transaction not null in Finish Transaction");
                rollbackTransaction();
//                throw new UAPersistenceException("Incorrect Transaction handling! While finishing transaction, transaction still open. Rolling Back.");
            } catch (UAPersistenceException e)
            {
                _logger.info("Finish Transaction threw an exception. Don't know what to do here. TODO find solution for handling this situation",e);
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
     * @param query      Query
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
            Query q = sess.createQuery(query).setCacheable(true);
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


}
