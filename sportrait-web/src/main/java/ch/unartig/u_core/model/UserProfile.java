/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 14.03.2007$
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
 * Revision 1.2  2007/06/09 11:15:37  alex
 * photographer
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/15 21:45:27  alex
 * no more price segment
 *
 ****************************************************************/
package ch.unartig.u_core.model;

import ch.unartig.u_core.model.GeneratedUserProfile;
import ch.unartig.u_core.model.Photographer;

import java.util.Collection;

/**
 * @author Alexander Josef, unartig 2007
 * UserProfile represents an object for authenticated users; the persistent information stored in the table is queried by the DataSourceRealm authenticator
 */
public class UserProfile extends GeneratedUserProfile {


    public UserProfile(UserRole role)
    {
        getRoles().add(role);
    }

    public UserProfile(Collection roles)
    {
        getRoles().addAll(roles);
    }


    /**
     *  default constructor; use Constructors with roles
     */
    public UserProfile()
    {
    }


    public void addPhotographer(Photographer eachPhotographer) {
        setPhotographer(eachPhotographer);
    }
}
