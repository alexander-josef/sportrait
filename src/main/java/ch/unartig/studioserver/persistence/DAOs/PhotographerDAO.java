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

import java.util.Collections;
import java.util.List;

public class PhotographerDAO
{
    Logger _logger = Logger.getLogger(getClass().getName());

    public List list() throws UAPersistenceException
    {
        // on production: error is thrown, probably here
        return Collections.emptyList();

        // todo:
/*
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
*/

    }

    public Photographer load(Long photographerId) throws UAPersistenceException
    {
        try
        {
            return HibernateUtil.currentSession().load(Photographer.class, photographerId);
        } catch (HibernateException e)
        {
            throw new UAPersistenceException("Could not load Customer, see stack trace", e);
        }

    }

    public void saveOrUpdate(Photographer photographer) throws UAPersistenceException
    {
        try {
            HibernateUtil.currentSession().clear();
            HibernateUtil.currentSession().saveOrUpdate(photographer);
        } catch (Exception e) {
            // todo : this seems to work - but still check why there are multiple instances of photographer and why merge is needed !
            e.printStackTrace();
            HibernateUtil.currentSession().merge(photographer);
            _logger.debug("merged photographer ...");
        }

    }
}
