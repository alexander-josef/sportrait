/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 28.03.2007$
 *
 * Copyright (c) 2007 Alexander Josef,unartig AG; All rights reserved
 *
 * STATUS  :
 *    $Revision$, $State$, $Name$
 *
 *    $Author$, $Locker$
 *    $Date$
 *
 *************************************************
 * $Log$
 * Revision 1.8  2007/05/29 19:50:34  alex
 * adding character encoding for file
 *
 * Revision 1.7  2007/05/02 14:27:55  alex
 * Uploading refactored, included fine-path server import for sportrait
 *
 * Revision 1.6  2007/04/20 14:29:11  alex
 * shopping cart, photographer album edit page
 *
 * Revision 1.5  2007/04/17 11:03:27  alex
 * dynamic pager added
 *
 * Revision 1.4  2007/04/03 06:49:23  alex
 * photographer added, cagtegory added to album
 *
 * Revision 1.3  2007/03/30 20:39:26  alex
 * check in
 *
 * Revision 1.2  2007/03/29 13:33:44  alex
 * working on photo upload
 *
 * Revision 1.1  2007/03/28 13:52:58  alex
 * edit events page
 *
 ****************************************************************/
package ch.unartig.sportrait.web.actions;

import ch.unartig.u_core.controller.Client;
import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.u_core.exceptions.UnartigException;
import ch.unartig.u_core.Registry;
import ch.unartig.u_core.model.SportsEvent;
import ch.unartig.sportrait.web.beans.UploadBean;
import ch.unartig.u_core.persistence.DAOs.GenericLevelDAO;
import ch.unartig.u_core.persistence.util.HibernateUtil;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.actions.MappingDispatchAction;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Todo describe
 */
public class SportEventsAction extends MappingDispatchAction
{
    Logger _logger = Logger.getLogger(getClass().getName());


    /**
     * Action creates an album and uploads photo into it
     * a) create album
     * b) upload from zip or copy from server path
     * c) import in db 
     * d) success or failure message
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws ch.unartig.u_core.exceptions.UnartigException
     */
    @SuppressWarnings({"JavaDoc", "UnusedDeclaration"})
    public ActionForward createUpload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UnartigException
    {
        UploadBean uploadBean = new UploadBean();
        try
        {
            Client client = (Client)request.getSession().getAttribute(Registry._SESSION_CLIENT_NAME);
            GenericLevelDAO glDao = new GenericLevelDAO();
            DynaActionForm dynaForm = (DynaActionForm) form;
            String eventId = dynaForm.getString("eventId");
            String eventCategoryId = dynaForm.getString("eventCategoryId");
            String fineImageServerPath = dynaForm.getString("imagePath");
            FormFile file = (FormFile) dynaForm.get("content");
            FormFile importDataFile = (FormFile) dynaForm.get("importData");
            SportsEvent event = (SportsEvent) glDao.load(new Long(eventId), SportsEvent.class);
            _logger.debug("params : [" + eventId + "] [" + eventCategoryId + "]");

            if (file != null && !"".equals(file.getFileName()) && (fineImageServerPath == null || "".equals(fineImageServerPath)))
            {
                _logger.debug("Going to create album from Zip file");
                // todo FIXME : use importer here and reduce cyclic dependency on model:
                event.createRegisterSportsAlbum(new Long(eventCategoryId), file.getInputStream(),client, true);
            } else if ((fineImageServerPath != null && !"".equals(fineImageServerPath)) && (file == null || file.getFileSize()==0) )
            {
                _logger.debug("Going to create album from temporary path on server");
                event.createRegisterSportsAlbum(new Long(eventCategoryId), fineImageServerPath,client);
            } else if (importDataFile!=null && (fineImageServerPath == null || "".equals(fineImageServerPath))  )
            {
                _logger.debug("Going to create album from import data file only (no fine images)");
                String fileName = importDataFile.getFileName();
                event.importAlbumFromImportDataOnly(new Long(eventCategoryId), importDataFile.getInputStream(),client, fileName.toLowerCase().endsWith(".zip"));
            } else
            {
                _logger.error("cannot define both, file and image path");
                throw new UnartigException("cannot define both, file and image path");
            }
            uploadBean.setSportsEvent(event);
            // todo insert success message to be shown with the new page
            request.setAttribute("uploadBean", uploadBean);
        } catch (NumberFormatException e)
        {
            _logger.error("error setting ID", e);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (Exception e)
        {
            _logger.error("unartig exception ", e);
            throw new RuntimeException("Error during creation or upload of an album",e);
        }
        return mapping.findForward("success");
    }

    /**
     * Photographer calls page where he has the possibility to upload an album
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws UnartigException
     */
    @SuppressWarnings({"JavaDoc", "UnusedDeclaration"})
    public ActionForward accountPhUpload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UnartigException
    {
        UploadBean uploadBean = new UploadBean();
        String eventId = request.getParameter("eventId");
        if (eventId != null && !"".equals(eventId))
        {
            uploadBean.setSportsEventById(eventId);
        }
        request.setAttribute("uploadBean", uploadBean);
        return mapping.findForward("success");
    }

    /**
     * Use a colon separated textfile to bulk-import events
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws UnartigException
     */
    @SuppressWarnings({"JavaDoc", "UnusedDeclaration"})
    public ActionForward bulkImportEvents(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UnartigException
    {
        _logger.debug("bulk import of events");
        DynaActionForm startnumberMappingForm = (DynaActionForm) form;
        FormFile file = (FormFile) startnumberMappingForm.get("content");
        try
        {
            // charset : ISO-8859-1, UTF-8 ?
            BufferedReader in = new BufferedReader(new InputStreamReader(file.getInputStream(), "ISO-8859-1"));
            String line;
            //read line by line except for comments:
            while ((line = in.readLine()) != null)
            {
                _logger.debug("parsing line as ISO-8859-1 : " + line);
                // only process non-comment lines:
                if (!line.startsWith("#"))
                {
                    processEventLine(line);
                }
            }
            in.close();
        } catch (IOException e)
        {
            _logger.error("Error parsing event line", e);
            throw new UnartigException("error reading bulk import file", e);
        }
        return mapping.findForward("success");
    }

    /**
     * Load a sports event
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws UnartigException
     */
    @SuppressWarnings({"JavaDoc", "UnusedDeclaration"})
    public ActionForward event(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    {
        String forward = "success";

        GenericLevelDAO glDao = new GenericLevelDAO();
        String sportsEventId = request.getParameter("sportsEventId");
        if (sportsEventId != null)
        {
            SportsEvent sportsEvent = null;
            try
            {
                sportsEvent = (SportsEvent) glDao.load(new Long(sportsEventId), SportsEvent.class);
            } catch (Exception e)
            {
                // could be number format exception or database probelm
                _logger.info("cannot instantiate a sports event");
                forward = "noEvent";
            }
            request.setAttribute("sportsEvent", sportsEvent);
        } else
        {
            forward = "noEvent";
        }
        // only for debugging:
        Client client = (Client)request.getSession().getAttribute(Registry._SESSION_CLIENT_NAME);
        return mapping.findForward(forward);
    }

    /**
     * process each line from the column separated sportsevent file and save the created sportsevent
     *
     * @param line column separated line with event information to be parsed
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     *          if saving the sporsevent fails
     * @throws ch.unartig.u_core.exceptions.UnartigException Rollback
     */
    private void processEventLine(String line) throws UnartigException
    {
        _logger.debug("processing line ....");
        GenericLevelDAO glDao = new GenericLevelDAO();
        HibernateUtil.beginTransaction();
        try
        {
            SportsEvent sportsEvent = new SportsEvent(line);
            glDao.saveOrUpdate(sportsEvent);
            HibernateUtil.commitTransaction();
        } catch (UAPersistenceException e)
        {
            HibernateUtil.rollbackTransaction();
            _logger.error("Cannot save Sports album", e);
            throw new UAPersistenceException(e);
        }
    }


}
