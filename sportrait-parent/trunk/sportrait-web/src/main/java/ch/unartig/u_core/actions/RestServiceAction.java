package ch.unartig.u_core.actions;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Struts action for handling rest service requests
 */
public class RestServiceAction extends Action {

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse httpServletResponse)
    {
        String method = request.getMethod();
        System.out.println("method = " + method);
        System.out.println("request.getRequestURI() = " + request.getRequestURI());

        // todo check action mapping possibilities from Struts in manual

        // todo check put / delete / get / post requests
        // No ActionForward for this service
        return null;
    }

}
