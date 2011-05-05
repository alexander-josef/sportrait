package ch.unartig.studioserver.actions;

import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.model.Photo;
import ch.unartig.studioserver.persistence.DAOs.PhotoDAO;
import ch.unartig.util.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Used by display view to download a (free) highres photo
 */
public class DownloadPhotoAction extends Action {

    Logger _logger = Logger.getLogger(getClass().getName());

    /**
     * Send back high res foto via http servlet repsonse. Check if album of photo has free digital option
     * @param actionMapping
     * @param actionForm
     * @param request
     * @param httpServletResponse
     * @return
     * @throws UnartigException
     */
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse httpServletResponse) throws UnartigException {

        String photoId = request.getParameter("photoId");
        PhotoDAO photoDAO = new PhotoDAO();
        Photo photo = photoDAO.load(Long.valueOf(photoId));

        // todo check if photo is allowed to be downloaded

        if (photo.getAlbum().isHasFreeHighResDownload()) {
            _logger.debug("photo [" + photo.getFilename() + "] will be downloaded");
            try {
                // use application/octet-stream instead to download and not display the image??
                httpServletResponse.setContentType("image/JPG");
                httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"" + photo.getFilename() + "\"");
                // What's the content length? set content-length header:
                httpServletResponse.setContentLength((int) photo.getFile().length());
                // copy the file to the outputstream:
                // digital negativ copy the file to the output stream
                _logger.info("streaming the digital negativ");
                FileUtils.copyFile(photo.getFile(), httpServletResponse.getOutputStream());
            } catch (IOException e) {
                // for example if high-res file was not found ...
                throw new UnartigException("cannot stream photo for download ...", e);
            }
        }
        // todo error page if photo not available?
        // todo possible option to download different version (smaller resolution) of the file?
        return null;
    }
}
