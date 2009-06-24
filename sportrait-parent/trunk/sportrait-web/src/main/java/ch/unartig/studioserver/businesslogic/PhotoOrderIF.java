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
 * Revision 1.11  2006/12/27 13:33:45  alex
 * better email
 *
 * Revision 1.10  2006/11/05 16:41:43  alex
 * action messages work for order confirmation
 *
 * Revision 1.9  2006/11/03 13:15:19  alex
 * some changes
 *
 * Revision 1.8  2006/11/01 10:51:20  alex
 * cc interface check in, transactions work
 *
 * Revision 1.7  2006/11/01 10:14:45  alex
 * cc interface check in, transactions work
 *
 * Revision 1.6  2006/10/28 21:57:09  alex
 * reformat
 *
 * Revision 1.5  2006/08/25 23:27:58  alex
 * payment i18n
 *
 * Revision 1.4  2006/01/10 15:44:56  alex
 * vm1 config files, new property "simulateOrderOnly"
 *
 * Revision 1.3  2005/11/30 17:08:34  alex
 * order process bug fix
 *
 * Revision 1.2  2005/11/21 17:52:59  alex
 * no account action , photo order
 *
 * Revision 1.1  2005/11/21 09:58:11  alex
 * init. version
 *
 ****************************************************************/
package ch.unartig.studioserver.businesslogic;

import ch.unartig.u_core.exceptions.UnartigException;
import ch.unartig.u_core.model.Order;
import ch.unartig.u_core.model.Customer;

/**
 * <p>The purpose of this interface is to define a photo order</p>
 * <p>A photo order can be handeled internally, for example for sales of digital photos using credit cards
 * or a photo order can be implemented to talk to a partner lab.</p>
 * <p>Currently its only implementation is the colorplaza OIPS order interface</p>
 */
public interface PhotoOrderIF
{
    // all interface fields are constants
    static final int _SUCCESS = 0;
    static final int _CREDIT_CARD_REJECTED = 40;
    static final int _CREDIT_CARD_TRANSACTION_FAILED = 50;
    static final int _UNKNOWN_ERROR = 999;
    static final int _CREDIT_CARD_EXPIRY_DATE_ERROR = 4;
    // starting unartig errors with 55xx
    static final int _ORDER_CUSTOMER_MISSING = 5501;


    public void setUnartigCustomer(Customer customer);

    public void setOrder(Order order);
    public Order getOrder();
    /**
     * process the order and return true for success of false for problems (check error message)
     *
     * @return true for success of false for failure
     * @throws ch.unartig.u_core.exceptions.UnartigException  database problem
     */
    boolean processOrder() throws UnartigException;


    /**
     * Given the implementing class was constructed with an order object, this method tries to process a credit card payment for the given order
     *
     * @param ccDetails object containg all credit card information used for payment
     * @return true for succesful credit card payment or false
     * @throws ch.unartig.u_core.exceptions.UnartigException
     *          can be thrown if order is not set
     */
//    boolean creditCardPayment(CreditCardDetails ccDetails) throws UnartigException;

    /**
     * <p>set the credit card details to the photo order object
     * <p>a not-null CreditCardDetails object means that payment is done by credit card
     * @param ccDetail a complete creditCardDetails object
     */
    void setCreditCardDetails(CreditCardDetails ccDetail);

    /**
     *
     * @param errorCode the error code as type code, see constants
     */
    void setErrorCode(int errorCode);

    /**
     *
     * @return the error code as a type code, see constants in this class
     */
    int getErrorCode();
}
