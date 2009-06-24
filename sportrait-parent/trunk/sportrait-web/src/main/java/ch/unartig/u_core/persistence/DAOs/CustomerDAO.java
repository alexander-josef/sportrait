/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Nov 21, 2005$
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
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.1  2005/11/21 17:52:59  alex
 * no account action , photo order
 *
 ****************************************************************/
package ch.unartig.u_core.persistence.DAOs;

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.u_core.model.Customer;
import ch.unartig.u_core.persistence.util.HibernateUtil;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

public class CustomerDAO
{
    Logger _logger = Logger.getLogger(getClass().getName());

    public void saveOrUpdate(Customer customer) throws UAPersistenceException
    {
        try
        {
            HibernateUtil.currentSession().saveOrUpdate(customer);
        } catch (HibernateException e)
        {
            _logger.error("Cannot save or update a customer, see stack trace", e);
            throw new UAPersistenceException("Cannot save or update a customer, see stack trace", e);
        }

    }

    public Customer load(Long customerId) throws UAPersistenceException
    {
        try
        {
            return (Customer) HibernateUtil.currentSession().load(Customer.class, customerId);
        } catch (HibernateException e)
        {
            throw new UAPersistenceException("Could not load Customer, see stack trace", e);
        }
    }
}
