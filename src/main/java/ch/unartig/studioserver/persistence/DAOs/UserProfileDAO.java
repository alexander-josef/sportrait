/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 05.06.2007$
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
import ch.unartig.studioserver.model.UserProfile;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserProfileDAO
{
    Logger _logger = LogManager.getLogger(getClass().getName());

    /**
     * @param userName
     * @return
     */
    public UserProfile load(String userName)
    {
        // caution: make sure that the rolename exists in the UserRoles table - foreign key is linked to the rolename column. The query otherwise fails with a not-so-clear null pointer exception.
        UserProfile userProfile = (UserProfile) HibernateUtil.currentSession().createQuery("select up from UserProfile as up " +
                "where up.userName = :userName")
                .setParameter("userName", userName)
                .setCacheable(true)
                .uniqueResult();
        return userProfile;
    }


    public void saveOrUpdate(UserProfile userProfile) throws UAPersistenceException
    {
        HibernateUtil.currentSession().saveOrUpdate(userProfile);
    }

    public UserProfile load(Long userProfileId) throws UAPersistenceException
    {
        return (UserProfile)HibernateUtil.currentSession().load(UserProfile.class,userProfileId);
    }
}
