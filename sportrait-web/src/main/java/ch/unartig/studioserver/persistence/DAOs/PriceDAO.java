/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 07.03.2007$
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
 * Revision 1.1  2007/03/12 19:43:52  alex
 * product types for albums
 *
 ****************************************************************/
package ch.unartig.studioserver.persistence.DAOs;

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.studioserver.model.Price;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import org.hibernate.HibernateException;

public class PriceDAO
{

    public Price load(Long priceId) throws UAPersistenceException {
        try
        {
            return (Price) HibernateUtil.currentSession().load(Price.class, priceId);
        } catch (HibernateException e)
        {
            throw new UAPersistenceException("Could not load Price, see stack trace", e);
        }

    }
}
