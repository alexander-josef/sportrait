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
package ch.unartig.u_core.persistence.DAOs;

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.studioserver.model.UserRole;
import ch.unartig.u_core.persistence.util.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public class UserRoleDAO
{

    public UserRole loadRoleByName(String adminRoleName) throws UAPersistenceException
    {
        Criteria c = HibernateUtil.currentSession()
                .createCriteria(UserRole.class)
                .add(Restrictions.eq("roleName", adminRoleName));
        return (UserRole)c.uniqueResult();
    }

    
}
