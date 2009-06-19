/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 16.10.2006$
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
 * Revision 1.1  2007/03/01 18:23:42  alex
 * initial commit maven setup no history
 *
 * Revision 1.1  2006/10/17 08:07:06  alex
 * creating the order hashes
 *
 ****************************************************************/
package ch.unartig.u_core.persistence.DAOs;

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.studioserver.model.OrderHash;
import ch.unartig.u_core.persistence.util.HibernateUtil;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

public class OrderHashDAO
{

    Logger _logger = Logger.getLogger(getClass().getName());

    public void save(OrderHash orderHash) throws UAPersistenceException
        {
        try
        {
            HibernateUtil.currentSession().saveOrUpdate(orderHash);
        } catch (HibernateException e)
        {
            _logger.error("Cannot save or update a OrderHash, see stack trace");
            throw new UAPersistenceException("Cannot save or update a OrderHash, see stack trace", e);
        }

    }

}
