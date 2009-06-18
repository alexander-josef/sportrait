package ch.unartig.studioserver.persistence.DAOs;

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.studioserver.model.Category;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import org.hibernate.HibernateException;
/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Oct 3, 2005$
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
 * Revision 1.2  2005/11/19 20:32:32  alex
 * price segments inserted
 *
 * Revision 1.1  2005/10/03 14:50:25  alex
 * first daos
 *
 ****************************************************************/

/**
 * @deprecated
 */
public class CategoryDAO
{

        public void saveOrUpdate(Category channel) throws UAPersistenceException
        {

            try
            {
                HibernateUtil.currentSession().saveOrUpdate(channel);
            } catch (HibernateException e)
            {
//                _logger.error("Cannot save or update a Category, see stack trace");
                throw new UAPersistenceException("Cannot save or update a Category, see stack trace", e);
            }
        }
}
