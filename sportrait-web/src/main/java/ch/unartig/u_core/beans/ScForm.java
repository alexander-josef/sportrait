/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Nov 6, 2005$
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
 * Revision 1.2  2006/02/07 14:48:53  alex
 * bug 820 and minor refactorings
 *
 * Revision 1.1  2005/11/12 23:13:58  alex
 * add ScForm
 *
 * Revision 1.2  2005/11/07 17:38:26  alex
 * admin interface refactored
 *
 * Revision 1.1  2005/11/06 21:43:22  alex
 * overview, admin menu, index-photo upload
 *
 ****************************************************************/
package ch.unartig.u_core.beans;

import org.apache.struts.action.ActionForm;

import java.io.Serializable;

import ch.unartig.u_core.beans.ScOrderItem;

/**
 * Module Refactoing:
 *  + Checked with unartig: OK, identical
 *
 *
 * Shopping Cart Form: used to process the parameters from the shopping cart when updated or finished with the check-out process
 * a shopping-cart-form consists of:
 * - orderItem: an array of [photoId,productId,quantity]
 * - the shopping cart is a list of order items
 */
public class ScForm extends ActionForm implements Serializable
{


    private String[] stringIndexed;

    public String[] getStringIndexed()
    {
        return stringIndexed;
    }

    public void setStringIndexed(String[] stringIndexed)
    {
        this.stringIndexed = stringIndexed;
    }

    private ScOrderItem[] orderItemsArray = new ScOrderItem[100];


/*
    public ScOrderItem[] getOrderItemsArray()
    {
        return orderItemsArray;
    }

    public void setOrderItemsArray(ScOrderItem[] orderItemsArray)
    {
        this.orderItemsArray = orderItemsArray;
    }
*/



    // index setter getter
    public ScOrderItem getOrderItemsArray(int index)
    {
        return orderItemsArray[index];
    }

    public void setOrderItemsArray(int index, ScOrderItem orderItemsArray)
    {
        this.orderItemsArray[index] = orderItemsArray;
    }

}
