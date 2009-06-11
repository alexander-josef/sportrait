/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 16.04.2007$
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
 * Revision 1.2  2007/05/05 12:21:24  alex
 * errorhandling no eventcategory
 *
 * Revision 1.1  2007/04/17 11:03:27  alex
 * dynamic pager added
 *
 ****************************************************************/
package ch.unartig.studioserver.persistence.DAOs;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.studioserver.model.EventCategory;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.apache.log4j.Logger;

public class EventCategoryDAO {
    Logger _logger = Logger.getLogger(getClass().getName());

    public EventCategory load(Long eventCategoryId) throws UAPersistenceException {
        try {
            return (EventCategory) HibernateUtil.currentSession().load(EventCategory.class, eventCategoryId);
        } catch (HibernateException e) {
            throw new UAPersistenceException("Could not load EventCategory, see stack trace", e);
        }
    }

    public void saveOrUpdate(EventCategory category) {
        try {
            HibernateUtil.currentSession().saveOrUpdate(category);
        } catch (HibernateException e) {
            _logger.error("Cannot save or update a Category, see stack trace", e);
            throw new RuntimeException("Cannot save or update a event category", e);
        }
    }

    

}