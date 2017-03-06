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
package ch.unartig.controller;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.studioserver.model.Photographer;
import ch.unartig.studioserver.model.UserProfile;
import ch.unartig.studioserver.model.UserRole;
import ch.unartig.studioserver.persistence.DAOs.UserProfileDAO;
import ch.unartig.util.HttpUtil;
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
     * Default Constructor; sets server URL and initializes the authorized roles list object
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
     * @param username username of authenticated user. Used to be from the Principal object, or from verified google login
     * @throws UAPersistenceException
     */
    public void init(String username)
    {
        UserProfileDAO userprofileDao = new UserProfileDAO();

        // todo: check if username exists in DB!
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
        // todo: fixme: why load again? set as object in init() call
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
     * This server's URL; to be used with the download url, for example, or the client applet.
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
