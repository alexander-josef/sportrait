/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 02.04.2007$
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
 * Revision 1.1  2007/04/03 06:54:05  alex
 * added category and photographer
 *
 ****************************************************************/
package ch.unartig.studioserver.model;

import ch.unartig.studioserver.persistence.DAOs.UserRoleDAO;

/**
 * 
 */
public class Photographer extends GeneratedPhotographer
{

    /**
     * default constructor
     * @param userProfile UserProfile to construct photogapher with
     */
    public Photographer(UserProfile userProfile)
    {
        setUserProfile(userProfile);
    }

    /**
     * default constructor. Use constructor with a userprofile
     */
    public Photographer()
    {

    }

    /**
     * Convenience getter to return this userprofile's full name
     * @return The full name of the photographer
     */
    public String getFullName()
    {
        return getUserProfile().getFirstName() + " " + getUserProfile().getLastName();
    }

    public boolean isAdmin()
    {
        UserRoleDAO userRoleDao = new UserRoleDAO();
        return getUserProfile().getRoles().contains(userRoleDao.loadRoleByName(UserRole._ADMIN_ROLE_NAME));
    }

    public String toString()
    {
        return getPhotographerId()+";"+getFullName();
    }
}
