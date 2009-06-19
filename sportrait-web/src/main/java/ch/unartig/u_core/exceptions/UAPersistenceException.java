/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since Sep 21, 2004$
 *
 * Copyright (c) 2004 Alexander Josef, unartig studios; All rights reserved
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
 * Revision 1.2  2005/10/24 13:50:07  alex
 * upload of album
 * import in db 
 * processing of images
 *
 * Revision 1.1  2005/09/26 18:37:45  alex
 * *** empty log message ***
 *
 * Revision 1.2  2005/02/17 17:38:37  alex
 * option of parentEvents

 * options und optioncollection tag
 *
 * Revision 1.1  2004/09/27 19:23:48  alex
 * hbm2dll installed
 *
 * Revision 1.1  2004/09/21 10:12:12  alex
 * exception handling improved
 *
 ****************************************************************/
package ch.unartig.u_core.exceptions;

public class UAPersistenceException extends RuntimeException
{
    public UAPersistenceException(String message, Throwable problem)
    {
        super(message, problem);

    }

    public UAPersistenceException(String message)
    {
        super(message);
    }

    public UAPersistenceException(Throwable problem)
    {
        super(problem.getMessage(), problem);
    }
}
