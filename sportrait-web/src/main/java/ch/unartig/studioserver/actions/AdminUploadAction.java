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

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.u_core.exceptions.UnartigException;
import ch.unartig.u_core.util.FileUtils;
import ch.unartig.u_core.Registry;
import ch.unartig.studioserver.businesslogic.Uploader;
import ch.unartig.studioserver.model.Album;
import ch.unartig.u_core.persistence.DAOs.GenericLevelDAO;
import org.apache.commons.fileupload.DiskFileUpload;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;

/**
 *
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
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
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
     * @throws ch.unartig.u_core.exceptions.UnartigImagingException
     */
    public ActionForward importDirectory(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UnartigException, IOException {
        GenericLevelDAO glDao = new GenericLevelDAO();
        DynaActionForm dynaForm = (DynaActionForm)form;
        Long albumId = (Long)dynaForm.get("albumId");

        String imagePath = dynaForm.getString("imagePath");
        Album album = (Album)glDao.load(albumId, Album.class);
        Boolean processImages = (Boolean) dynaForm.get("processImages");

        // giving control to new thread and return.
        Thread uploader  = new Uploader(imagePath,album.getGenericLevelId(),processImages);
        uploader.start();

        return mapping.findForward("showSingleAlbumUpload");
    }

    /**
     * methods gets a post request containing the zip file to upload to the server
     * <br> plus creates the thumbnails
     * <br> plus creates the display-images
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     * @deprecated Use the {@see Uploader
     */
    public ActionForward uploadSingleAlbum(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        GenericLevelDAO genLevelDao = new GenericLevelDAO();
        Long albumId = uploadSingleAlbum(request);
        Album album = (Album) genLevelDao.load(albumId, Album.class);

        album.registerPhotos(false);
        // put problem photos to request to be used in the view
        request.setAttribute("uploadedAlbum",album);
        return mapping.findForward("showSingleAlbumUpload");
    }

    /**
     * file-upload using Jakarta commons file upload
     * html used for this:
     * <pre>
     * <form action="/ProjectImport.do" method="post" enctype="multipart/form-data">
     * <p/>
     * W&auml;hle eine Zip-Datei von Ihrem Rechner aus:<br>
     * <input name="Datei" type="file" size="50" maxlength="100000" accept="application/zip">
     * <p/>
     * <br>
     * <input type="submit" name="uploadEvent" value="Upload single Event"/>
     * <p/>
     * </p>
     * </form>
     * </pre>
     *
     * @param request
     * @return eventPath where the files will be uploaded
     * @throws Exception
     */
    private Long uploadSingleAlbum(HttpServletRequest request) throws Exception
    {
        Long albumId = null;
        if (_logger.isDebugEnabled())
        {
            _logger.debug("if you wish to display the request stream check in the code for commented call debugRequest(request)");
//            debugRequest(request);
        }

        DiskFileUpload fu = new DiskFileUpload();
        // set max size ; -1 unlimited
        fu.setSizeMax(-1);
        List fileItemList = fu.parseRequest(request);
        // store all sent multi part content in input stream array
        InputStream[] inputStreamArray = new InputStream[fileItemList.size()];
        _logger.debug("Number of Items in Multipart Request: " + fileItemList.size());
        int j = 0;
        for (int i = 0; i < fileItemList.size(); i++)
        {
            FileItem fileItem = (FileItem) fileItemList.get(i);
            if (_logger.isDebugEnabled())
            {
//                debugFiles(fileItem);
            }
            // file item can be something else than file ..
            if (!fileItem.isFormField())
            {// set the input stream
                inputStreamArray[j] = fileItem.getInputStream();
                j++;
            } else if (Registry._ALBUM_ID_NAME.equals(fileItem.getFieldName()))
            {
                _logger.debug("found album id in file item = " + fileItem.getString());
                try
                {
                    albumId = new Long(fileItem.getString());
                } catch (NumberFormatException e)
                {
                    throw new UnartigException("album id is of wrong format (cannot convert to Long)", e);

                }
            }
        }
        if (albumId == null || "".equals(albumId))
        {
            throw new UnartigException("album id cannot be empty");
        }
        if (j != 1)
        {
            throw new UnartigException("Either Zero or more than one file has been sent; exactly one file needed");
        }
        File albumFinePath = ((Album) new GenericLevelDAO().load(albumId, Album.class)).getFinePath();
        FileUtils.extractFlatZipArchive(inputStreamArray[0], albumFinePath);
        return albumId;
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
