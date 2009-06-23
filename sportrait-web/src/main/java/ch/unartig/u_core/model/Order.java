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
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.8  2006/11/21 08:23:19  alex
 * news-fenster
 *
 * Revision 1.7  2006/11/14 16:19:24  alex
 * digital order
 *
 * Revision 1.6  2006/10/28 21:57:09  alex
 * reformat
 *
 * Revision 1.5  2006/10/17 08:07:06  alex
 * creating the order hashes
 *
 * Revision 1.4  2006/06/29 15:03:58  alex
 * reporting, download photos check in
 *
 * Revision 1.3  2005/11/21 17:52:59  alex
 * no account action , photo order
 *
 * Revision 1.2  2005/11/18 19:15:52  alex
 * stuff ...
 *
 * Revision 1.1  2005/11/09 21:59:36  alex
 * Order process classes and logic,
 * database creation script now inserts start-data, sql scripts
 * build script
 *
 ****************************************************************/
package ch.unartig.u_core.model;

import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Set;

import ch.unartig.u_core.model.GeneratedOrder;
import ch.unartig.u_core.model.OrderItem;

/**
 * Processed Order after checking out the shopping cart
 */
public class Order extends GeneratedOrder
{
    Logger _logger = Logger.getLogger(getClass().getName());

    /**
     * standard noarg constructor
     */
    public Order()
    {
    }

    public Order(Date date)
    {
        setOrderDate(date);
    }

    /**
     * after an order has completly uploaded, write the orderid and the upload completed date to the db
     *
     * @param oipsOrderId id given by oips
     */
    public void confirmUpload(String oipsOrderId)
    {
        setOipsOrderId(oipsOrderId);
        setUploadCompletedDate(new Date());
    }

    /**
     * check the orderitems for digital photos
     *
     * @return true if the order contains digital photo orderitems, false if not
     */
    public boolean hasDigitalOrderItems()
    {
        // todo implement if needed
        return true;
    }

    /**
     * This method will be used to return all
     * items to create either the digital negativ or a standard format
     * <p/>
     * return a list with all orderitems that contain an order for a digital photo<br>
     * currently all photo-product will be processsed, i.e. the customer gets a small digi photo from every orderitem
     *
     * @return list of @see 'ch.unartig.studioserver.model.orderItem'
     */
    public Set getDigitalItems()
    {
        // todo if different from orderItems
        return getOrderItems();
    }

    /**
     * return true if order contains only digital products
     * <p> this can be used for instance to set a free delivery method, if no print products are involved in the order
     * @return true or false
     */
    public boolean isOnlyDigitalProducts()
    {
        _logger.debug("checking for digital products in order");
        for (Object o : getOrderItems()) {
            OrderItem scItem = (OrderItem) o;
            if (!scItem.getProduct().isDigitalProduct()) {
                _logger.debug("found non-digital product, returning false");
                return false;
            }
        }
        _logger.debug("all products digital, returning true");
        return true;
    }
}
