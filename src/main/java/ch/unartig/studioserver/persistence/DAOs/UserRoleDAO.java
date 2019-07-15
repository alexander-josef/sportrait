/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 07.06.2007$
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
 * Revision 1.1  2007/06/09 11:15:37  alex
 * photographer
 *
 ****************************************************************/
package ch.unartig.studioserver.persistence.DAOs;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.studioserver.model.UserRole;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public class UserRoleDAO
{

    /**
     * query to return a role by name
     * @param roleName
     * @return
     * @throws UAPersistenceException
     */
    public UserRole loadRoleByName(String roleName) throws UAPersistenceException
    {
        return HibernateUtil.currentSession().createQuery("select ur " +
                "from UserRole ur " +
                "where roleName = :roleName",UserRole.class)
                .setParameter("roleName",roleName)
                .setCacheable(true)
                .uniqueResult();

    }

    
}
