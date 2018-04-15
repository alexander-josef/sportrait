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
import java.net.URL;
import java.util.Base64;

/**
 * Used by display view to download a (free) highres photo
 */
public class DownloadPhotoAction extends Action {

    Logger _logger = Logger.getLogger(getClass().getName());

    /**
     * Send back high res photo via http servlet response. Check if album of photo has free digital option
     * @param actionMapping
     * @param actionForm
     * @param request
     * @param httpServletResponse
     * @return
     * @throws UnartigException
     */
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse httpServletResponse) throws UnartigException {


        String photoId = request.getParameter("photoId");
        _logger.debug("preparing download of photo with photoID " + photoId);
        PhotoDAO photoDAO = new PhotoDAO();
        Photo photo = photoDAO.load(Long.valueOf(photoId));

        if (photo.getAlbum().isHasFreeHighResDownload()) {
            _logger.debug("photo [" + photo.getFilename() + "] will be downloaded");
            try {
                // use application/octet-stream instead to download and not display the image??
                httpServletResponse.setContentType("image/JPG");
                httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"" + photo.getFilename() + "\"");
                // What's the content length? set content-length header:
                // todo: if needed, add a method in Photo to receive content length.
                // httpServletResponse.setContentLength((int) photo.getFileContent().length());
                // copy the file to the outputstream:
                // digital negative copy the file to the output stream
                _logger.info("streaming the digital negative from imgix");
                // todo: how to protect high res photos available to imgix?


                // sponsor / logo location scheme by year. i.e. for example"/logos/2017/asvz-logo.png"
                int yearForLogoWatermark = photo.getAlbum().getEvent().getEventDateYear();

                // support non-Imgix solution before 2018:
                if (photo.isAfterImageServiceMigration())
                {
                    String asvzLogoRelativeUrl = "/logo/" + yearForLogoWatermark + "/asvz-logo-" + yearForLogoWatermark + ".png";
                    // todo get logo / sponsor bar from Registry
                    String sponsorBarRelativeUrl = "/logo/" + yearForLogoWatermark + "/sola-sponsors-bar-bottom-neu.png";

                    String base64LogoParams = "mark64="+Base64.getEncoder().encodeToString(asvzLogoRelativeUrl.getBytes()) + "&markalign=right%2Ctop&markpad=70&markscale=26";
                    String base64SponsorParams = "blend64="+Base64.getEncoder().encodeToString(sponsorBarRelativeUrl.getBytes()) + "&bm=normal&ba=bottom%2C%20center&bs=none&bw=1.0"; // change bw=0.9 in case there should be some padding left and right of the sponsor bar

                    URL imgixUrl = new URL(photo.getMasterImageUrlFromImageService()+"?"+base64LogoParams+"&"+base64SponsorParams);
                    _logger.debug("imgix URL = " + imgixUrl.toString());
                    FileUtils.copyFile(imgixUrl.openStream(), httpServletResponse.getOutputStream());


                    // FileUtils.copyFile(new URL("https://int-sportrait.imgix.net/fine-images/215/fine/sola15_e12_mf_2011.JPG?bm=normal&blend64=L2xvZ28vc29sYS1zcG9uc29ycy1iYXItYm90dG9tLW5ldS5wbmc&mark64=L2xvZ28vYXN2ei1sb2dvLTIwMTctcmVzaXplZC03MDBweC5wbmc&markalign=right%2Ctop&ba=bottom%2C%20center&bs=inherit").openStream(), httpServletResponse.getOutputStream());

                } else { // legacy solution before 2018 - Stream directly from S3
                    FileUtils.copyFile(photo.getFileContent(), httpServletResponse.getOutputStream());
                }

            } catch (IOException e) {
                // for example if high-res file was not found ...
                throw new UnartigException("cannot stream photo for download ...", e);
            }
        } else {
            _logger.debug("no free download available for photo [" + photo.getFilename() + "]");
        }



        // todo error page if photo not available?
        // todo possible option to download different version (smaller resolution) of the file?
        return null;
    }
}
