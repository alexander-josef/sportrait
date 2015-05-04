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
package ch.unartig.studioserver.persistence.DAOs;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.studioserver.model.Photographer;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;

import java.util.List;

public class PhotographerDAO
{
    Logger _logger = Logger.getLogger(getClass().getName());

    public List list() throws UAPersistenceException
    {
        _logger.debug("list() in PhotographerDAO ...");
        List allPhotographers = null;
        try {
            Criteria c = HibernateUtil.currentSession()
                    .createCriteria(Photographer.class);
            allPhotographers = c.list();
            _logger.debug("returning all photographers : " + allPhotographers);
        } catch (HibernateException e) {
            _logger.error("Error reading list of photographers",e);
            e.printStackTrace();
        }
        return allPhotographers;

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
