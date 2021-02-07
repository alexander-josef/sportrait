package ch.unartig.controller;

import ch.unartig.studioserver.Registry;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.SecureTilesRequestProcessor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by alexanderjosef on 07.03.17.
 */
public class UnartigCustomSecurityRequestProcessor extends SecureTilesRequestProcessor {

    private Logger logger = Logger.getLogger(getClass().getName());

    protected boolean processRoles(HttpServletRequest request,
                                   HttpServletResponse response, ActionMapping mapping)
            throws IOException, ServletException
    {
        // Is this action protected by role requirements?
        logger.debug("Custom security request processor called. Request: " + request.getRequestURL());
        String[] roles = mapping.getRoleNames();
        if ((roles == null) || (roles.length < 1)) {
            // no role necessary for processing this action / request - return true always
            return (true);
        }

        // Check the current user against the list of required roles
        HttpSession session = request.getSession();
        Client client = (Client) session.getAttribute(Registry._SESSION_CLIENT_NAME);
        if (client == null) {
            response.sendError(403,"Please log in");
            return false;
        }
        logger.info("logged in client username : ["+ client.getUsername() + "]");
        logger.info("logged in client userprofileid : ["+ client.getUserProfile().getUserProfileId() + "]");
        logger.info("logged in client (userprofile) contains roles : ["+ client.getAuthorizedRoleNames()+ "]");
        for (String role : roles) {
            if (client.getAuthorizedRoleNames().contains(role)) {
                logger.info("found logged in user with role \""+role+"\"");
                return (true);
            }
        }
        logger.info("No user found with role \"" + Arrays.toString(roles) +"\" - Returning Not Authorized (403) Code");
        response.sendError(403,"Please log in");
//        response.sendError(HttpServletResponse.SC_BAD_REQUEST,getInternal().getMessage("notAuthorized",mapping.getPath()));
        return (false);
    }
}

