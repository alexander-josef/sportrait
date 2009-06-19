/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 03.11.2006$
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
 * Revision 1.1  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.1  2006/11/06 11:05:36  alex
 * text = no check in
 *
 ****************************************************************/
package ch.unartig.u_core.exceptions;

public class CreditCardException extends UnartigException
{
    /**
     * something with Credit Card validation went wrong
     * @param message
     */
    public CreditCardException(String message)
    {
        super(message);
    }

    public CreditCardException(CreditCardException exception)
    {
        super(exception);
    }
}
