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

import ch.unartig.exceptions.UnartigImagingException;
import ch.unartig.studioserver.Registry;
import com.sun.media.jai.codec.*;
import org.apache.log4j.Logger;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.BorderExtender;
import javax.media.jai.widget.ScrollingImagePanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.*;
import java.net.URL;

public class ImagingHelper
{
    static Logger _logger = Logger.getLogger(ImagingHelper.class.getName());

    /**
     * loads an image from disk and returns a RenderedOp
     *
     * @param file
     * @return image
     * @throws ch.unartig.exceptions.UnartigImagingException
     *          File not found or similar problem
     */
    public static RenderedOp load(File file) throws UnartigImagingException
    {

        RenderedOp retVal;
        FileSeekableStream stream;
        try
        {
            stream = new FileSeekableStream(file);
        } catch (IOException e)
        {
            _logger.error("Exception while loading image from file", e);
            throw new UnartigImagingException("Exception while loading image from file", e);
        }
        /* Create an operator to decode the image file. */
        // add border extender here?
        retVal = JAI.create("stream", stream);
        return retVal;
    }

    public static RenderedOp readImage(InputStream stream)
    {
        SeekableStream seekableStream;
        seekableStream = SeekableStream.wrapInputStream(stream, true);
        // seekableStream = new MemoryCacheSeekableStream(stream);
        return JAI.create("stream", seekableStream);
    }

    /**
     * @param sampledOp
     * @param os        output stream for encoder
     * @param quality
     */
    private static void renderJpg(RenderedOp sampledOp, OutputStream os, float quality)
    {
        // todo : robust exception handling
        // todo: check if still used. only used for streaming a 400x600 download in unartig.ch
        JPEGEncodeParam encParam = new JPEGEncodeParam();
        try
        {
            encParam.setQuality(quality);
            ImageEncoder encoder = ImageCodec.createImageEncoder("JPEG", os, encParam);
            encoder.encode(sampledOp);
            _logger.debug("ImagingHelper.createJpgImage : image encoded");
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }


    /**
     * Using the renderedOP save the image as a jpg file. Apply Watermark / Logo montage if "applyWatermark" parameter is true
     *
     * @param image original image to be scaled
     * @param quality : A setting of 0.0 produces the highest compression ratio, with a sacrifice to image quality. The default value is 0.75
     * @param applyWatermark set to true to apply a watermark over the resulting image
     * @return ByteArrayOutputStream with that contains the resulting image
     */
    public static OutputStream createJpgImage(RenderedOp image, float quality, boolean applyWatermark)
    {
        // return variable:
        OutputStream scaledImageResult = new ByteArrayOutputStream();
        try
        {
            BufferedImage sourceImage = image.getAsBufferedImage();

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
     * Todo error handling ! For example, if a small image is rescaled, an exception might happen ... try with an index pic
     * Re-Samples an image by the scaleFactor. Uses JAI
     * @param image
     * @param scaleFactor
     * @return image
     */
    public static RenderedOp reSample(RenderedOp image, Double scaleFactor)
    {

        ParameterBlock params = new ParameterBlock();
        // Rendering Hint needed in order to avoid a black border around the image.
        RenderingHints renderingHints  = new RenderingHints(null);
        renderingHints.put(JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY));
        image.setRenderingHint(JAI.KEY_BORDER_EXTENDER,BorderExtender.createInstance(BorderExtender.BORDER_COPY));
        _logger.debug("renderingHints = " + renderingHints);
        params.addSource(image);
        params.add(scaleFactor); // scale factor
        /* Create an operator to scale image. */
        return JAI.create("subsampleaverage", params,renderingHints);
    }

    /**
     * use unsharpen operation on the renderedOp
     *
     * @param image
     * @param factor = 0 : no effect;  factor> 0 : sharpening;factor -1 < gain < 0 : smoothing
     * @return a renderedOp
     */
    public static RenderedOp unSharpen(RenderedOp image, float factor)
    {

        float[] fA = new float[4];
        KernelJAI kernel = new KernelJAI(2, 2, fA);

        ParameterBlock unsharpParams = new ParameterBlock();
        unsharpParams.addSource(image);
        unsharpParams.add(null); // kernel: 3x3 average
        unsharpParams.add(factor);
        // rendering hint border copy to avoid black frame!!
        return JAI.create("unsharpMask", unsharpParams,new RenderingHints(JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY)));
//        return image;
    }

    public static void display(RenderedOp image)
    {
/* Get the width and height of image2. */
        int width = image.getWidth();
        int height = image.getHeight();

        ScrollingImagePanel panel = new ScrollingImagePanel(image, width, height);
        Frame window = new Frame("JAI Sample Program");
        window.add(panel);
        window.pack();
        window.show();
    }

    /**
     * @param renderedOp
     * @return max width or hight of picture
     */
    public static int getMaxWidthOrHightOf(RenderedOp renderedOp)
    {
        return Math.max(renderedOp.getHeight(), renderedOp.getWidth());
    }

    /**
     * Creates a new image based on the passed renderedOp. Image will be sharpened
     * @param fineImage        the source for the new image
     * @param scale
     * @param quality Quality factor for saving JPGs. From 0 - 1 (best quality). Defaults to 0.75f
     * @param imageSharpFactor
     * @param applyWatermark
     */
    public static OutputStream createNewImage(RenderedOp fineImage, Double scale, float quality, float imageSharpFactor, boolean applyWatermark)
    {
        _logger.debug("Going to create new Image from File : " + fineImage.toString() + " using scale :  " + scale);
        RenderedOp sharpScaledImage;
        RenderedOp scaledImage;
        try
        {
            scaledImage = reSample(fineImage, scale);
            sharpScaledImage = unSharpen(scaledImage, imageSharpFactor);
        } catch (Exception e)
        {
            _logger.info("rendering threw exception. probably rendering result is bigger than original; using original image instead");
            _logger.debug("Exception: ", e);
            sharpScaledImage = fineImage;
        }
        return createJpgImage(sharpScaledImage, quality, applyWatermark);
    }


    /**
     * generic resample function
     *
     * @param imageFileContent           the file to resample
     * @param resampleFactor
     * @param os             OutputStream
     * @param quality
     * @throws ch.unartig.exceptions.UnartigImagingException
     *          from load; file not found or similar
     */
    public static void reSample(InputStream imageFileContent, Double resampleFactor, OutputStream os, float quality) throws UnartigImagingException
    {
//        PipedOutputStream retVal = new PipedOutputStream();
        RenderedOp sampledOp = reSample(readImage(imageFileContent), resampleFactor);
        renderJpg(sampledOp, os, quality);
    }

    /**
     * Given an Image (renderedOP), create a scaled copy from that image.
     * Helper method to calculate scale factor
     * @param sourceImage      Source photo rendered op
     * @param longerSidePixels target image longer side in pixels
     * @param applyWatermark
     */
    public static OutputStream createScaledImage(RenderedOp sourceImage, double longerSidePixels, boolean applyWatermark) {
        Double scale;
        scale = longerSidePixels / (double) ImagingHelper.getMaxWidthOrHightOf(sourceImage);
        // public static void createScaledImage(String newImageFileName, RenderedOp sourceImage, double longerSidePixels, File path, boolean applyWatermark) {

        // File newFile = new File(path, newImageFileName);

        return createNewImage(sourceImage, scale, Registry._IMAGE_QUALITY_STANDARD, Registry._ImageSharpFactor, applyWatermark);
//        _logger.info("wrote new file " + newFile.getAbsolutePath());
    }
}
