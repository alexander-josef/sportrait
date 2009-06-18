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
 * Revision 1.1  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.2  2006/04/29 23:32:07  alex
 * many sola features, bugs, hibernate config
 *
 * Revision 1.1  2006/04/06 18:31:22  alex
 * display fixed for sports album
 *
 ****************************************************************/
package ch.unartig.studioserver.persistence.DAOs;

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.studioserver.model.EventRunner;
import ch.unartig.studioserver.persistence.util.HibernateUtil;

public class EventRunnerDAO
{
    /**
     * @param eventRunner
     * @throws UAPersistenceException
     */
    public void save(EventRunner eventRunner) throws UAPersistenceException
    {
        HibernateUtil.currentSession().save(eventRunner);
    }
}
