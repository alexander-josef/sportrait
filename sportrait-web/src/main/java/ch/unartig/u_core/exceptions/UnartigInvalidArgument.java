/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Oct 24, 2005$
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
 * Revision 1.1  2005/11/20 16:42:15  alex
 * sunntig obig
 *
 * Revision 1.1  2005/10/24 13:50:07  alex
 * upload of album
 * import in db 
 * processing of images
 *
 ****************************************************************/
package ch.unartig.u_core.exceptions;

public class UnartigInvalidArgument extends UnartigException
{
    public UnartigInvalidArgument(String message, Throwable throwable)
    {
        super(message,throwable);
    }
    public UnartigInvalidArgument(String message)
    {
        super(message);
    }
}
