/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 07.06.2007$
 *
 * Copyright (c) 2007 Alexander Josef,unartig AG; All rights reserved
 *
 * STATUS  :
 *    $Revision$, $State$, $Name$
 *
 *    $Author$, $Locker$
 *    $Date$
 *
 *************************************************
 * $Log$
 * Revision 1.1  2007/06/09 11:15:37  alex
 * photographer
 *
 ****************************************************************/
package ch.unartig.u_core.persistence.DAOs;

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.u_core.model.Photographer;
import ch.unartig.u_core.persistence.util.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;

import java.util.List;

public class PhotographerDAO
{
    public List list() throws UAPersistenceException
    {
        Criteria c = HibernateUtil.currentSession()
                .createCriteria(Photographer.class);
        return c.list();

    }

    public Photographer load(Long photographerId) throws UAPersistenceException
    {
        try
        {
            return (Photographer) HibernateUtil.currentSession().load(Photographer.class, photographerId);
        } catch (HibernateException e)
        {
            throw new UAPersistenceException("Could not load Customer, see stack trace", e);
        }

    }

    public void saveOrUpdate(Photographer photographer) throws UAPersistenceException
    {
        HibernateUtil.currentSession().saveOrUpdate(photographer);

    }
}
