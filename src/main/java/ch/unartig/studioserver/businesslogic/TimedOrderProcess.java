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
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.9  2006/11/22 12:41:22  alex
 * order process crashed with invalid orderitem. now it's thrown and handeled gracefully
 *
 * Revision 1.8  2006/11/05 16:41:43  alex
 * action messages work for order confirmation
 *
 * Revision 1.7  2006/01/10 15:44:56  alex
 * vm1 config files, new property "simulateOrderOnly"
 *
 * Revision 1.6  2005/11/30 17:08:34  alex
 * order process bug fix
 *
 * Revision 1.5  2005/11/25 16:30:45  alex
 * order save fix
 *
 * Revision 1.4  2005/11/23 20:52:10  alex
 * bug-fixes
 *
 * Revision 1.3  2005/11/22 21:33:16  alex
 * ordering process
 *
 * Revision 1.2  2005/11/21 17:52:59  alex
 * no account action , photo order
 *
 * Revision 1.1  2005/11/21 09:58:34  alex
 * init. version
 *
 ****************************************************************/
package ch.unartig.studioserver.businesslogic;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.model.Order;
import ch.unartig.studioserver.persistence.DAOs.OrderDAO;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

public class TimedOrderProcess extends TimerTask
{
    Logger _logger = Logger.getLogger(getClass().getName());
    /*use the copla demo order account?*/
    public static final boolean _DEMO_ORDER = Registry.isDemoOrderMode();
    public static final boolean _SIMULATE_ORDER_ONLY = Registry.isSimulateOrderOnly();


    public void run()
    {
        try
        {
            processOrders();
            _logger.info("################################################################################################");
            _logger.info("TimedOrderProcess.run has been executed at :" + new Date());
            _logger.info("TimedOrderProcess.run: " + this.hashCode());
            _logger.info("################################################################################################");
        } catch (UAPersistenceException e)
        {
            _logger.error("TimedOrderProcess returned an error", e);
        }

    }

    /**
     * processes a batch of orders
     * <p>every order is commited separatly!
     * @throws ch.unartig.exceptions.UAPersistenceException problem getting orders from database
     */
    private void processOrders() throws UAPersistenceException
    {
        OrderDAO orderDao = new OrderDAO();
        Order order = null;

        HibernateUtil.beginTransaction();
        List openOrders = orderDao.getOpenOrders();
        HibernateUtil.commitTransaction();
        _logger.info("" + openOrders.size());
        PhotoOrderIF photoOrder;


        for (int i = 0; i < openOrders.size(); i++)
        {
            try
            {
                HibernateUtil.beginTransaction();
                Long orderId = ((Order) openOrders.get(i)).getOrderId();
                order=orderDao.reLoad(orderId);
                _logger.debug("re-loaded order with id ["+order.getOrderId()+"]");

                _logger.info("-----#########---------##########----------");
                _logger.info("proccessing open order with ID : " + order.getOrderId());
                _logger.info("number of order items to process : " + order.getOrderItems().size());
                _logger.info("Customer ID and last name : " + order.getCustomer().getCustomerId().toString() + " / " + order.getCustomer().getLastName());
                _logger.info("-----#########---------##########----------");
                photoOrder = new CoplaPhotoOrder(order, _DEMO_ORDER, _SIMULATE_ORDER_ONLY);
                photoOrder.processOrder();
                HibernateUtil.commitTransaction();
                _logger.info("commited ");
            } catch (UnartigException e)
            {
                _logger.error("Order failed!! rolling back this order process for order ["+order+"] Continuing. Check Stacktrace",e);
                HibernateUtil.rollbackTransaction();
                _logger.info("continue with next order");
            }

        }
        // todo add a list of commited order to print out or even better email status report with failed orders

    }
}
