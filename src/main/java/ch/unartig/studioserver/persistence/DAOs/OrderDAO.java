/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Nov 9, 2005$
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
 * Revision 1.8  2006/11/01 10:14:45  alex
 * cc interface check in, transactions work
 *
 * Revision 1.7  2006/10/28 21:57:09  alex
 * reformat
 *
 * Revision 1.6  2006/10/17 08:07:06  alex
 * creating the order hashes
 *
 * Revision 1.5  2006/06/29 15:03:58  alex
 * reporting, download photos check in
 *
 * Revision 1.4  2005/12/27 15:22:22  alex
 * payment method shows now bill not credit card
 *
 * Revision 1.3  2005/11/22 21:33:16  alex
 * ordering process
 *
 * Revision 1.2  2005/11/21 17:52:59  alex
 * no account action , photo order
 *
 * Revision 1.1  2005/11/11 10:20:21  alex
 * Order process classes and logic,
 * database creation script now inserts start-data, sql scripts
 * build script
 *
 ****************************************************************/
package ch.unartig.studioserver.persistence.DAOs;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.studioserver.model.Order;
import ch.unartig.studioserver.model.OrderHash;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class OrderDAO
{
    Logger _logger = Logger.getLogger(getClass().getName());

    /**
     * @param order the order class instance
     * @throws UAPersistenceException database problem
     */
    public void save(Order order) throws UAPersistenceException
    {
        try
        {
            HibernateUtil.currentSession().saveOrUpdate(order);
        } catch (HibernateException e)
        {
            _logger.error("Cannot save or update a Order, see stack trace");
            throw new UAPersistenceException("Cannot save or update a Order, see stack trace", e);
        }

    }

    /**
     * retrieve all open orders. open orders have no oipsorderid and no upload completed date
     *
     * @return list of open orders
     * @throws ch.unartig.exceptions.UAPersistenceException db error
     *
     */
    public List getOpenOrders() throws UAPersistenceException
    {
        Criteria c = HibernateUtil.currentSession()
                .createCriteria(Order.class)
                .add(Expression.isNull("oipsOrderId"))
                .addOrder(org.hibernate.criterion.Order.asc("orderDate"));
        return c.list();
    }

    /**
     * lookup the order from the order-hash table
     *
     * @param orderHashString the message digest that is stored with each order
     * @return a single order if a mapping exists or null if none exists
     * @throws ch.unartig.exceptions.UAPersistenceException db-error in case more than one record is returned
     *
     */
    public Order getOrderFromHash(String orderHashString) throws UAPersistenceException
    {
        Criteria c = HibernateUtil.currentSession()
                .createCriteria(OrderHash.class)
                .add(Restrictions.eq("hash", orderHashString));
        OrderHash orderHash = (OrderHash) c.uniqueResult();
        _logger.debug("orderHash = " + orderHash);
        return orderHash != null ? orderHash.getOrder() : null;
    }

    /**
     * load or reload the Order object into the current session using createQuery (in order to avoid a proxy)
     *
     * @param orderId order PK
     * @return persistence Order object instance
     * @throws UAPersistenceException db exception
     */
    public Order reLoad(Long orderId) throws UAPersistenceException
    {
        _logger.debug("reloading Order");
        try
        {
            return (Order)HibernateUtil.currentSession().createQuery("from ch.unartig.studioserver.model.Order as order where order.orderId = "+orderId).uniqueResult();
//            return (Order) HibernateUtil.currentSession().load(Order.class, orderId);
        } catch (HibernateException e)
        {
            throw new UAPersistenceException("Could not load Order, see stack trace", e);
        }
    }

}
