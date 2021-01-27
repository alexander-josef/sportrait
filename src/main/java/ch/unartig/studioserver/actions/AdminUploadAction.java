/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since Oct 23, 2005$
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
 * Revision 1.3  2007/03/28 13:52:58  alex
 * edit events page
 *
 * Revision 1.2  2007/03/27 16:39:17  alex
 * refactored studioalbum into album
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.7  2006/01/11 21:22:53  alex
 * admin upload menu works
 *
 * Revision 1.6  2005/11/27 19:39:10  alex
 * fast upload
 *
 * Revision 1.5  2005/11/25 11:09:09  alex
 * removed system outs
 *
 * Revision 1.4  2005/11/25 10:56:58  alex
 *
 * Revision 1.3  2005/11/22 19:45:46  alex
 * admin actions, configurations
 *
 * Revision 1.2  2005/10/24 14:37:55  alex
 * small fixes, creating directories
 *
 * Revision 1.1  2005/10/24 13:50:07  alex
 * upload of album
 * import in db
 * processing of images
 *
 ****************************************************************/
package ch.unartig.studioserver.actions;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.businesslogic.Uploader;
import ch.unartig.studioserver.model.Album;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;
import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.actions.MappingDispatchAction;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

/**
 * Todo check where this class is still used
 */
public class AdminUploadAction extends MappingDispatchAction
{
    Logger _logger = Logger.getLogger(getClass().getName());

    /**
     * prepare list of albums to be presented in the upload page
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward for the upload page
     * @throws ch.unartig.exceptions.UAPersistenceException
     *
     */
    public ActionForward getSingleAlbumUploadPage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UAPersistenceException
    {
        System.out.println("AdminUploadAction.getSingleAlbumUploadPage *******************************");
        GenericLevelDAO glDao = new GenericLevelDAO();
        List albumList = glDao.listGenericLevel(Album.class);
        request.setAttribute("albumList", albumList);
        return mapping.findForward("singleAlbumUploadPage");
    }

    /**
     * A path with fine images exists locally to the server. this action imports the photos in the db<br/>
     * 1. copy all photos to the destination path under DATA
     * 2. register photos with db
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     *
     * @throws ch.unartig.exceptions.UnartigImagingException
     */
    public ActionForward importDirectory(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UnartigException, IOException {
        GenericLevelDAO glDao = new GenericLevelDAO();
        DynaActionForm dynaForm = (DynaActionForm)form;
        Long albumId = (Long)dynaForm.get("albumId");

        String imagePath = dynaForm.getString("imagePath");
        Album album = (Album)glDao.load(albumId, Album.class);
        Boolean processImages = (Boolean) dynaForm.get("processImages");

        // giving control to new thread and return.
        Uploader uploader  = new Uploader(imagePath,album.getGenericLevelId(),processImages, false);
        Thread uploaderThread  = new Thread(uploader);
        uploaderThread.start();

        return mapping.findForward("showSingleAlbumUpload");
    }





    /**
     * helper for showing content of uploading files
     *
     * @param fileItem
     */
    private void debugFiles(FileItem fileItem)
    {
        _logger.debug("fileItem.isFormField() = " + fileItem.isFormField());
        _logger.debug("fileItem.getName() = " + fileItem.getName());
        _logger.debug("fileItem.getFieldName() = " + fileItem.getFieldName());
        _logger.debug("fileItem.getSize() = " + fileItem.getSize());
    }

    /**
     * helper for showing the content of the request
     *
     * @param request
     */
    public void debugRequest(HttpServletRequest request)
    {

        Enumeration requestParamNames = request.getParameterNames();
        _logger.debug("******** Method = " + request.getMethod());
        _logger.debug("******** ContentType = " + request.getContentType());
        _logger.debug("******** ContentType = " + request.getContentType());
        while (requestParamNames.hasMoreElements())
        {
            String s = (String) requestParamNames.nextElement();
            _logger.debug("******** Parameter [" + s + "] =  " + request.getParameter(s));
        }

        try
        {
            ServletInputStream sis = request.getInputStream();
            _logger.debug("-----------------------------------------------");
            int c;
            StringBuffer requestString = new StringBuffer();
            requestString.append((char) (Character.LINE_SEPARATOR));
            while ((c = sis.read()) != -1)
            {
                requestString.append((char) c);
            }
            _logger.debug(requestString.toString());
            _logger.debug("-----------------------------------------------");
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
