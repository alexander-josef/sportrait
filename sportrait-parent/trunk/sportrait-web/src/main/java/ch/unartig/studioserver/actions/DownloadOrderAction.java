/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since 20.05.2006$
 *
 * Copyright (c) 2005 unartig AG  --  All rights reserved
 *
 * STATUS  :
 *    $Revision$, $State$, $Name$
 *
 *    $Author$, $Locker$
 *    $Date$
 *
 *************************************************
 * $Log$
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.3  2006/11/24 10:21:59  alex
 * download page fixes
 *
 * Revision 1.2  2006/10/28 21:57:09  alex
 * reformat
 *
 * Revision 1.1  2006/06/29 15:03:58  alex
 * reporting, download photos check in
 *
 ****************************************************************/
package ch.unartig.studioserver.actions;

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.u_core.exceptions.UnartigException;
import ch.unartig.u_core.util.HttpUtil;
import ch.unartig.u_core.beans.DownloadImageBean;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>This class contains the actions related to downloading the digital photos of an order
 * </p>
 * <p>First, list *every photo* that has been ordered on the download page and explain the available download:
 * <br>for download products the ordered product
 * <br>for print products a free 400 x 600 px download
 * </p><br>
 * <p>If a photo has been selected for download, stream the relevant digital product</p>
 * 
 * @author Alexander Josef, (c) 2006, unartig
 */
public class DownloadOrderAction extends Action
{
    Logger _logger = Logger.getLogger(getClass().getName());

    /**
     * This action handles the presentation of downloadable, digital images
     *
     * @param actionMapping action Mapping
     * @param actionForm action Form
     * @param request request
     * @param httpServletResponse response
     * @return a page view for downloading the images identified by the order hash
     */
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse httpServletResponse) throws UnartigException {
        String orderHash = actionMapping.getParameter(); // parameter set by the struts-config
        _logger.debug("preparing downloads for order with order-hash ["+orderHash +"]");
        DownloadImageBean downloadBean;
        String downloadUrl;
        // prepare a bean first
        try {
            downloadUrl= HttpUtil.getDownloadUrl(orderHash,request);
            downloadBean = new DownloadImageBean(orderHash,downloadUrl);
            if (downloadBean.getOrder()==null)
            {
                return actionMapping.findForward("dbProblem");
            }
            request.setAttribute("downloadBean",downloadBean);
            _logger.debug("setting download bean for order with id ["+downloadBean.getOrder().getOrderId().toString()+"] in request scope");
        } catch (UAPersistenceException e) {
            _logger.info("two entries with the same hash-key in order-hash table",e);
            return actionMapping.findForward("dbProblem");
        }

//        String photoId = request.getParameter("phId");
        String orderItemId = request.getParameter("OIID");
        if (request.getParameter("OIID") != null && !"".equals(orderItemId))
        {
            // prepare and check for download
            downloadBean.downloadPhoto(orderItemId, httpServletResponse);
            return null;
        } else
        {
            return actionMapping.findForward("success");
        }
    }

}
