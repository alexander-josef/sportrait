/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since 29.03.2006$
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
 * Revision 1.1  2006/04/06 18:31:22  alex
 * display fixed for sports album
 *
 ****************************************************************/
package ch.unartig.u_core.model;

import ch.unartig.u_core.model.Event;

public class EventRunner extends GeneratedEventRunner
{
    /**
     * mandatory default constructor
     */
    public EventRunner()
    {
    }

    public EventRunner(Event event, String startNumber, PhotoSubject photoSubject)
    {
        this.setEvent(event);
        this.setStartnumber(startNumber);
        this.setPhotoSubject(photoSubject);
    }
}
