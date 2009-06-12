/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 22.06.2006$
 *
 * Copyright (c) 2006 Alexander Josef,unartig AG; All rights reserved
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
 * Revision 1.3  2006/10/17 08:07:06  alex
 * creating the order hashes
 *
 * Revision 1.2  2006/10/11 12:52:28  alex
 * typos, unartig AG replaces Westhous
 *
 * Revision 1.1  2006/06/29 15:03:58  alex
 * reporting, download photos check in
 *
 ****************************************************************/
package ch.unartig.studioserver.model;

import java.util.Date;


/**
 * this business class represents an order hash that is generated and stored upon a confirmed order<br>
 * this order hash is used to provide a user with a link to his downloadable photos
 * the link must fulfill the following:
 * - it shall not be possible to guess another, valid link to downloadable photos
 * - the link shall expire after some time
 */
public class OrderHash extends GeneratedOrderHash
{


    /**
     * default constructor
     */
    public OrderHash()
    {
    }

    /**
     * Full construktor
     * @param order the persistent order instance
     * @param expiryDate date
     * @param hash the has that will be stored in combination with this order
     */
    public OrderHash(Order order, Date expiryDate,String hash)
    {
        setOrder(order);
        setExpiryDate(expiryDate);
        setHash(hash);
    }
}
