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
package ch.unartig.studioserver.model;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.studioserver.persistence.DAOs.UserRoleDAO;
import org.apache.log4j.Logger;

/**
 *
 * @author Alexander Josef, unartig AG 2007
 * Update 2017-05-01 : DataSourceRealm authenticator does not exist anymore
 */
public class UserRole extends GeneratedUserRole{
    static Logger _logger = Logger.getLogger(UserRole.class);
    public static final String _ADMIN_ROLE_NAME = "unartigadmin";
    public static final String _PHOTOGRAPHER_ROLE_NAME = "photographer";

}
