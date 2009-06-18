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
 * Revision 1.1  2007/03/01 18:23:42  alex
 * initial commit maven setup no history
 *
 * Revision 1.1  2005/11/21 17:52:59  alex
 * no account action , photo order
 *
 ****************************************************************/
package ch.unartig.studioserver.persistence.DAOs;

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.studioserver.model.OrderItem;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

public class OrderItemDAO
{
    Logger _logger = Logger.getLogger(getClass().getName());

    public void saveOrUpdate(OrderItem oi) throws UAPersistenceException
    {
        try
        {
            HibernateUtil.currentSession().saveOrUpdate(oi);
        } catch (HibernateException e)
        {
            _logger.error("Cannot save or update a OrderItem, see stack trace");
            throw new UAPersistenceException("Cannot save or update a OrderItem, see stack trace", e);
        }

    }

    public OrderItem load(Long orderItemId) throws UAPersistenceException
    {
        return (OrderItem)HibernateUtil.currentSession().load(OrderItem.class,orderItemId);

    }
}
