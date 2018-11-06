/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since Oct 24, 2005$
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
 * Revision 1.1  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.9  2006/11/22 14:39:05  alex
 * small fixes
 *
 * Revision 1.8  2006/11/21 13:58:45  alex
 * small fixes
 *
 * Revision 1.7  2006/11/05 22:10:02  alex
 * credit card order works
 *
 * Revision 1.6  2006/10/28 21:57:09  alex
 * reformat
 *
 * Revision 1.5  2005/11/27 19:39:10  alex
 * fast upload
 *
 * Revision 1.4  2005/11/25 10:56:58  alex
 *
 * Revision 1.3  2005/11/16 14:26:49  alex
 * validator works for email, new library
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
package ch.unartig.studioserver.imaging;

import ch.unartig.studioserver.Registry;
import org.apache.log4j.Logger;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;
import com.imgix.URLBuilder;

public class    ImagingHelper
{
    static Logger _logger = Logger.getLogger(ImagingHelper.class.getName());






    /**
     * Using the renderedOP save the image as a jpg file. Apply Watermark / Logo montage if "applyWatermark" parameter is true
     *
     * @param sourceImage original image to be scaled
     * @param quality : A setting of 0.0 produces the highest compression ratio, with a sacrifice to image quality. The default value is 0.75
     * @param applyWatermark set to true to apply a watermark over the resulting image
     * @return ByteArrayOutputStream with that contains the resulting image
     */
    public static OutputStream createJpgImage(BufferedImage sourceImage, float quality, boolean applyWatermark)
    {
        // return variable:
        OutputStream scaledImageResult = new ByteArrayOutputStream();
        try
        {
            int sourceWidth = sourceImage.getWidth();
            int sourceHeight = sourceImage.getHeight();
            BufferedImage result = new BufferedImage(sourceWidth, sourceHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = result.createGraphics();

            graphics2D.drawImage(sourceImage, 0, 0, null);
            _logger.debug("Using a watermark ? : " + applyWatermark);
            if (applyWatermark)
            {
                // get scaled watermark : see next line, width / height - quality of scaled image?
                //graphics2D.drawImage(getWatermark(sourceWidth, sourceHeight), 0, 0,sourceWidth,sourceHeight, null);
                // montage the 2 watermark elements separately - just apply two "drawImage" operations!
                // no scaling of the overlaying images. The logos / sponsor bar needs to have the right dimension to fit on the high res image
                // todo: introduce scaling of high res logo / overlay images to fit the high res photo images nicely
                BufferedImage logoImage = getLogoImage();
                graphics2D.drawImage(logoImage, 0, 0, null); // position: upper right corner
                BufferedImage sponsorBar = getSponsorBar();
                if (sponsorBar.getWidth() >= sourceWidth) {
                    _logger.error("sponsor bar width >= width of source image!");
                } else {
                    _logger.debug("Putting sponsor bar on image ...");
                    int width = (sourceWidth - sponsorBar.getWidth()) / 2; // sponsor bar width must be smaller than image width!
                    int height = sourceHeight - sponsorBar.getHeight();
                    graphics2D.drawImage(sponsorBar, width, height,null);// position : height = height of source - sponsorBar height; width: (source width - sponsorbar width) /2
                }
            }

            // write to an output stream that is returned

            // use ImageWriter to set quality of produced jpg
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
            ImageOutputStream ios = ImageIO.createImageOutputStream(scaledImageResult);
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT); // Needed see javadoc
            param.setCompressionQuality(quality); // from 0 - 1 ; defaults to 0.75f
            writer.write(null,new IIOImage(result,null,null),param); // param needs to be passed


        } catch (IOException e)
        {
            // todo: error treatment. If only logo montage fails, continue and save image
            e.printStackTrace();
            _logger.error("Error saving JPG image - continue with image processing",e);
        }
        return scaledImageResult;
    }


    /**
     * Return the logo image that can be put onto the images
     * @return
     * @throws IOException
     */
    private static BufferedImage getLogoImage() throws IOException {
        String logoImageFile = Registry.getLogoImageFile();
        _logger.debug("Read logo image for montage : " + logoImageFile);
        return ImageIO.read(new FileInputStream(logoImageFile));
    }


    /**
     * Return the sponsors bar that will be put at the bottom of the images
     * @return
     * @throws IOException
     */
    private static BufferedImage getSponsorBar() throws IOException {
        String sponsorBarFile = Registry.getSponsorBarFile();
        _logger.debug("Read sponsor-bar image for montage : " + sponsorBarFile);
        return ImageIO.read(new FileInputStream(sponsorBarFile));
    }


    /**
     * Return a alpha-transparent Watermark in either landscape or portrait orientation, depending on width and height of the source
     * @param sourceWidth in Pixels ?
     * @param sourceHeight in Pixels ?
     * @return Alpha-Transparent Image to overlay with a source image.
     * @throws IOException
     */
    private static BufferedImage getWatermark(int sourceWidth, int sourceHeight) throws IOException
    {
         if (sourceWidth > sourceHeight)
        {
            _logger.debug("Trying to read landscape watermark/logos image from file system ... ");
            InputStream is = new FileInputStream(Registry.getLogosOverlayLandscapeFile());
            BufferedImage bufferedImage = ImageIO.read(is);
            _logger.debug("Read landscape watermark image : " + Registry.getLogosOverlayLandscapeFile());
            return bufferedImage;
        } else
        {
            _logger.debug("Trying to read portrait watermark/logos image from file system ... ");
            InputStream is = new FileInputStream(Registry.getLogosOverlayPortraitFile());
            BufferedImage bufferedImage = ImageIO.read(is);
            _logger.debug("Read portrait watermark image ; " + Registry.getLogosOverlayPortraitFile());
            return bufferedImage;
        }
    }




    /**
     * @param renderedOp
     * @return max width or hight of picture
     */
//    public static int getMaxWidthOrHightOf()
//    {
//        return Math.max(renderedOp.getHeight(), renderedOp.getWidth());
//    }

    /**
     * Construct signed imgix URL
     * todo  : imgix Sign Key
     * @return signed imgix url including the needed parameters
     */
    public static String getSignedImgixUrl(Map<String, String> params, String path)
    {
        String domain = Registry.getApplicationEnvironment() + "-sportrait.imgix.net";
        String imgixSignKey = Registry.getImgixSignKey();
        //String imgixSignKey = "6rTyMFEnEmuCmGg6"; // dev env sign key gotten from imgix admin web page
        // todo find solution to securely store sign keys for all environments
        URLBuilder builder = new URLBuilder(domain);
        builder.setUseHttps(true); // use https
        builder.setSignKey(imgixSignKey); // set sign key
        // params.put("w", "100");
        // params.put("h", "100");
        return builder.createURL(path, params);
    }


}
