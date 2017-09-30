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
 * Revision 1.2  2005/11/21 17:52:59  alex
 * no account action , photo order
 *
 * Revision 1.1  2005/11/11 10:20:37  alex
 * Order process classes and logic,
 * database creation script now inserts start-data, sql scripts
 * build script
 *
 ****************************************************************/
package ch.unartig.studioserver.model;

public class OrderItem extends GeneratedOrderItem
{
    
    public OrderItem()
    {
    }

    public OrderItem(Photo photo, Product product, int quantity, Order order)
    {
        setQuantity(quantity);
        setPhoto(photo);
        setProduct(product);
        setOrder(order);
        
    }


}
