package ch.unartig.studioserver.actions;


import ch.unartig.controller.Client;
import ch.unartig.studioserver.Registry;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.MappingDispatchAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * Created by alexanderjosef on 01.03.17.
 */
public class AuthenticationAction extends MappingDispatchAction {
    private static final String CLIENT_ID = "780630173968-29smq37pmuihjn34mgpflbi7393k3dgh.apps.googleusercontent.com";
    Logger _logger = Logger.getLogger(getClass().getName());


    /**
     * Action to sign out the user. Sets the client object in the session to null.
     * @param actionMapping
     * @param actionForm
     * @param request
     * @param httpServletResponse
     */
    public ActionForward signOut(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse httpServletResponse)
    {
        _logger.debug("signing Out");
        request.getSession(true).setAttribute(Registry._SESSION_CLIENT_NAME, null);
        return actionMapping.findForward("success");
    }

    /**
     * Authenticate a google sign in request in the back end
     * @param actionMapping
     * @param actionForm
     * @param request
     * @param httpServletResponse
     * @return
     */
    public ActionForward tokensignin(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse httpServletResponse)  {

        _logger.debug("Callling tokensignin() method for google auth");
        _logger.debug("Using client-id : " + CLIENT_ID);
        String idTokenString = request.getParameter("idtoken");
        _logger.debug("Using idtoken : " + idTokenString);
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                .Builder(Registry.getGoogleHttpTransport(), Registry.getGoogleJasonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))
                        // Or, if multiple clients access the backend:
                        //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

// (Receive idTokenString by HTTPS POST)

        _logger.debug("Calling Google verifier - verifier = " + verifier);

        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (GeneralSecurityException securityException) {
            _logger.error("Security Exception thrown while calling google verifier",securityException);
        } catch (IOException ioException) {
            _logger.error("IO Exception while calling google verifier",ioException);

        } catch (Throwable throwable) {
            _logger.error("Exception has been thrown : ",throwable);
        }
        _logger.debug("receiving google id token : " + idToken);
        if (idToken != null) {
            // verified
            // now setting token to session // adding authenticated client object to session

            Client client = new Client(request);

//            request.getSession().setAttribute("googleIdToken", idToken);
            // set username to client object and store in session:
            String username = idToken.getPayload().getSubject();
            _logger.debug("google subject : " + username);
            // todo: using the "username" currently on the userprofile table.
            // todo: column should be something like "externalSubjectID" ...
            if (client.init(username)) {
                request.getSession(true).setAttribute(Registry._SESSION_CLIENT_NAME,client);

                GoogleIdToken.Payload payload = idToken.getPayload();

                // Print user identifier
                String userId = payload.getSubject();
                System.out.println("User ID: " + userId);
                _logger.info("User Id from payload: " + userId);

                // Get profile information from payload
                String email = payload.getEmail();
                boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");
                String locale = (String) payload.get("locale");
                String familyName = (String) payload.get("family_name");
                String givenName = (String) payload.get("given_name");

                _logger.debug("sending response ...");
                // todo : this response will not be validated in the JS part (template.js) - send back result that will be checked by JS
                try {
                    httpServletResponse.getWriter().print("Authorized User Id ; " + userId);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else {
                // authenticated google user not found in db
                _logger.debug("client.init() results to false -- no user found in DB with given google id. Returning unauthorized");
                try {
                    httpServletResponse.getWriter().print("unauthorized");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        } else {
            // todo: what to do in this case?
            // logout?
            _logger.info("IdToken == null ");
            try {
                httpServletResponse.getWriter().print("Invalid ID token");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        _logger.debug("returning null");
        return null;
    }

}
