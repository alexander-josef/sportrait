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
 * Revision 1.1  2007/03/18 13:28:20  urban
 * initial check in
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.3  2005/11/25 11:09:09  alex
 * removed system outs
 *
 * Revision 1.2  2005/10/24 13:50:07  alex
 * upload of album
 * import in db 
 * processing of images
 *
 * Revision 1.1  2005/09/26 18:37:45  alex
 * *** empty log message ***
 *
 * Revision 1.5  2005/02/27 17:13:37  alex
 * option of parentEvents

 * options und optioncollection tag
 *
 * Revision 1.4  2005/02/17 17:38:37  alex
 * option of parentEvents
 * options und optioncollection tag
 *
 * Revision 1.3  2005/01/02 15:04:41  alex
 * imaging start ..
 *
 * Revision 1.2  2004/12/28 21:54:58  alex
 * config of project root ... caution: network drive mappings are user specific ... tomcat did not see my mappings
 *
 * Revision 1.1  2004/09/27 19:23:48  alex
 * hbm2dll installed
 *
 * Revision 1.1  2004/09/21 10:12:12  alex
 * exception handling improved
 *
 ****************************************************************/
package ch.unartig.exceptions;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

public class UnartigException extends Exception
{
    ActionMessages actionMessages = new ActionMessages();

    public UnartigException(String message)
    {
        super(message);

        System.out.println("message = " + message);
        actionMessages.add(Globals.MESSAGE_KEY, new ActionMessage("error.generalError", message));
    }

    public UnartigException(String message, Throwable problem)
    {
        super(message, problem);
        System.out.println("message = " + message);
        problem.printStackTrace();
    }

    public UnartigException(Throwable problem)
    {
        super(problem);
        System.out.println("problem = " + problem);
    }
}
