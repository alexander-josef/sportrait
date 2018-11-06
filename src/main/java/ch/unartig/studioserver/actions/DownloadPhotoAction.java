package ch.unartig.studioserver.actions;

import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.imaging.ImagingHelper;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Used by display view to download a (free) highres photo
 */
public class DownloadPhotoAction extends Action {

    Logger _logger = Logger.getLogger(getClass().getName());

    /**
     * Send back high res photo via http servlet response. Check if album of photo has free digital option
     * expects the sportrait logo and sponsor bar at the following locations / in the following format:
     * /logo/[year of event]/asvz-logo-[year of event].png
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

                // support Imgix solution only after 2018: (old images with integrated watermark will work as before)
                if (photo.isAfterImageServiceMigration())
                {
                    String asvzLogoRelativeUrl = "/logo/" + yearForLogoWatermark + "/asvz-logo-" + yearForLogoWatermark + ".png";
                    // todo get logo / sponsor bar from Registry
                    String sponsorBarRelativeUrl;

                    // todo extract image params as configurations params, remove from code
                    String blendWidthFactor;
                    String markScalePercentage;

                    if (photo.isOrientationPortrait())
                    { // portrait format
                        blendWidthFactor = "1.0"; // use "0.75" if bar shall not completely cover width
                        markScalePercentage = "26";
                        // todo get logo / sponsor bar from Registry
                        sponsorBarRelativeUrl = "/logo/" + yearForLogoWatermark + "/sola-sponsors-bar-bottom-neu-8000px.png";

                    }else
                        // landscape format, logos and sponsor bar need to have smaller factor compared to image
                    {
                        // blendWidthFactor = "0.5"; // bar doesn't cover whole width
                        blendWidthFactor = "1"; // bar covers whole with (in combination with wide bar png) - change bw=0.9 in case there should be some padding left and right of the sponsor bar
                        markScalePercentage = "15";
                        // todo get logo / sponsor bar from Registry
                        sponsorBarRelativeUrl = "/logo/" + yearForLogoWatermark + "/sola-sponsors-bar-bottom-neu-gross-landscape.png";
                        // sola-sponsors-bar-bottom-neu-gross-landscape.png
                    }

                    // create imgix URL using the library and the sign key

                    Map<String,String> imgixParams = new HashMap<String,String>();
                    imgixParams.put("mark64",asvzLogoRelativeUrl);
                    imgixParams.put("markalign","right,top");
                    imgixParams.put("markpad","70");
                    imgixParams.put("markscale",markScalePercentage);
                    imgixParams.put("blend64",sponsorBarRelativeUrl);
                    imgixParams.put("bm","normal");
                    imgixParams.put("ba","bottom,center");
                    imgixParams.put("bs","none");
                    imgixParams.put("bw",blendWidthFactor); // change bw=0.9 in case there should be some padding left and right of the sponsor bar

                    URL imgixUrl = new URL(ImagingHelper.getSignedImgixUrl(imgixParams,photo.getPathForImageService())); // todo : think about directly returning the URL



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
