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
package ch.unartig.u_core.controller;

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.u_core.util.HttpUtil;
import ch.unartig.u_core.model.Photographer;
import ch.unartig.u_core.model.UserProfile;
import ch.unartig.u_core.model.UserRole;
import ch.unartig.u_core.persistence.DAOs.UserProfileDAO;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.ArrayList;

/**
 * The client object.
 */
public class Client
{
    Logger _logger = Logger.getLogger(getClass().getName());

    private String serverUrl;
    private String username;
    private List<String> authorizedRoleNames;

    /**
     * Default Constructor
     *
     * @param request The HttpServletRequest is needed to extract the server URL that is to be stored with this client.
     */
    public Client(HttpServletRequest request)
    {
        serverUrl = HttpUtil.getWebApplicationUrl(request);
        authorizedRoleNames = new ArrayList<String>();
    }


    /**
     * Set the username as field to this session object, set the role names as list of strings
     * @param username username of authenticated user
     * @throws UAPersistenceException
     */
    public void init(String username)
    {
        UserProfileDAO userprofileDao = new UserProfileDAO();

        UserProfile userProfile = userprofileDao.load(username);
        this.username = username;
        for (Object o : userProfile.getRoles()) {
            UserRole userRole = (UserRole) o;
            this.authorizedRoleNames.add(userRole.getRoleName());
        }
        _logger.debug("found userprofile for user : " + userProfile.getFirstName() + " " + userProfile.getLastName());
    }

    /**
     * Hook for making checks
     *
     * @return true or false
     */
    public boolean isValid()
    {
        return true;
    }

    public boolean isAdmin()
    {
        return authorizedRoleNames.contains(UserRole._ADMIN_ROLE_NAME);
    }


    public UserProfile getUserProfile()
    {
        UserProfileDAO userProfileDao = new UserProfileDAO();
        return userProfileDao.load(username);
    }


    /**
     * A one-to-one mapping for a userprofile to a photographer
     * @return Photographer object, loaded from userprofile. Userprofile loaded from DAO.
     */
    public Photographer getPhotographer()
    {
        UserProfileDAO userProfileDao = new UserProfileDAO();
        return userProfileDao.load(username).getPhotographer();
    }

    /**
     * This server's URL; to be used with the download url, for exmaple, or the client applet.
     *
     * @return String with client's URL to the server
     */
    public String getServerUrl()
    {
        return serverUrl;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getAuthorizedRoleNames() {
        return authorizedRoleNames;
    }
}
