/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 28.09.2007$
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
 ****************************************************************/
package ch.unartig.u_core.exceptions;

/**
 * This class is a dependant of UnartigException and collects all Exceptions that deal with authorization and authentication problems.
 * 
 */
public class NotAuthorizedException extends UnartigException
{
    public NotAuthorizedException(String s)
    {
        super(s);
    }
}
