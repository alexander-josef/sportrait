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
package ch.unartig.u_core.imaging;

import ch.unartig.u_core.exceptions.UnartigImagingException;
import ch.unartig.u_core.Registry;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;
import org.apache.log4j.Logger;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.imageio.ImageIO;
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
    // todo add to resource file, application config
    private static final String _RESOURCE_WATERMARK_LANDSCAPE = "/images/watermark2_quer.png";
    private static final String _RESOURCE_WATERMARK_PORTRAIT = "/images/watermark2_hoch.png";

    /**
     * loads an image from disk and returns a RenderedOp
     *
     * @param file
     * @return image
     * @throws ch.unartig.u_core.exceptions.UnartigImagingException
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

        return JAI.create("stream", stream);
    }

    /**
     * @param sampledOp
     * @param quality
     * @return The rendered image as a byte array
     */
    private static byte[] renderJpg(RenderedOp sampledOp, float quality)
    {
        // todo : robust exception handling
        ByteArrayOutputStream baos = new org.apache.commons.io.output.ByteArrayOutputStream();
        JPEGEncodeParam encParam = new JPEGEncodeParam();
        try
        {
            encParam.setQuality(quality);
            ImageEncoder encoder = ImageCodec.createImageEncoder("JPEG", baos, encParam);
            encoder.encode(sampledOp);
            _logger.debug("ImagingHelper.saveJpg : image encoded");
            return baos.toByteArray();
        } catch (FileNotFoundException e)
        {
            throw new RuntimeException("Cannot render JPG image");
        } catch (IOException e)
        {
            throw new RuntimeException("Cannot render JPG image");
        }

    }


    /**
     * Using the renderedOP save the image as a jpg file.
     *
     * @param image
     * @param quality : A setting of 0.0 produces the highest compression ratio, with a sacrifice to image quality. The default value is 0.75
     * @param file    File to save
     * @param applyWatermark set to true to apply a watermark over the resulting image
     * @return true for success
     */
    public static boolean saveJpg(RenderedOp image, float quality, File file, boolean applyWatermark)
    {
        try
        {
            BufferedImage sourceImage = image.getAsBufferedImage();

            int sourceWidth = sourceImage.getWidth();
            int sourceHeight = sourceImage.getHeight();
            BufferedImage result = new BufferedImage(sourceWidth, sourceHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = result.createGraphics();

            graphics2D.drawImage(sourceImage, 0, 0, null);
            _logger.debug("Using a watermark ??? : " + applyWatermark);
            if (applyWatermark)
            {
                graphics2D.drawImage(getWatermark(sourceWidth,sourceHeight), 0, 0, null);
            }
            ImageIO.write(result, "jpg", file);

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return false;
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
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
            _logger.debug("Trying to read landscape watermark image.... ");
            URL test = ImagingHelper.class.getResource(_RESOURCE_WATERMARK_LANDSCAPE);
            _logger.debug("Resource as File :  Path : " + test.getFile());
            InputStream is = ImagingHelper.class.getResourceAsStream(_RESOURCE_WATERMARK_LANDSCAPE);
            BufferedImage bufferedImage = ImageIO.read(is);
            _logger.debug("Read landscape watermark image!!");
            return bufferedImage;
        } else
        {
            _logger.debug("Trying to read portrait watermark image.... ");
            URL test = ImagingHelper.class.getResource(_RESOURCE_WATERMARK_PORTRAIT);
            _logger.debug("Resource as File :  Path : " + test.getFile());
            InputStream is = ImagingHelper.class.getResourceAsStream(_RESOURCE_WATERMARK_PORTRAIT);
            BufferedImage bufferedImage = ImageIO.read(is);
            _logger.debug("Read portrait watermark image!!");
            return bufferedImage;
        }
    }

    /**
     * Todo error handling ! For example, if a small image is rescaled, an exception might happen ... try with an index pic
     *
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
     * Creates a new image based on the passed renderedOp.
     *
     * @param fineImage        the source for the new image
     * @param scale
     * @param newImageFile
     * @param quality
     * @param imageSharpFactor
     * @param applyWatermark
     */
    public static void createNewImage(RenderedOp fineImage, Double scale, File newImageFile, float quality, float imageSharpFactor, boolean applyWatermark)
    {
        _logger.debug("Going to create new Image [" + newImageFile.getName() + "]from File : " + fineImage.toString() + " using scale :  " + scale);
        RenderedOp sharpThumbImage;
        RenderedOp thumbImage;
        try
        {
            thumbImage = reSample(fineImage, scale);
            sharpThumbImage = unSharpen(thumbImage, imageSharpFactor);
        } catch (Exception e)
        {
            _logger.info("rendering threw exception. probably rendering result is bigger than original; using original image instead");
            _logger.debug("Exception: ", e);
            sharpThumbImage = fineImage;
        }
        // todo check return value; report problem images
        saveJpg(sharpThumbImage, quality, newImageFile, applyWatermark);
    }

    /**
     * todo: this does not perform well ... find a better method to find photo dimensions (external EXIF library ?)
     *
     * @param photoFile
     * @return # of width-pixels for passed photo
     */
    public static Integer getPixelsWidth(File photoFile) throws UnartigImagingException
    {
        return load(photoFile).getWidth();
    }

    public static Integer getPixelsHeight(File photoFile) throws UnartigImagingException
    {
        return load(photoFile).getHeight();
    }

    /**
     * generic resample function
     *
     * @param file           the file to resample
     * @param resampleFactor
     * @param os             OutputStream
     * @param quality
     * @throws ch.unartig.u_core.exceptions.UnartigImagingException
     *          from load; file not found or similar
     */
    public static void reSample(File file, Double resampleFactor, OutputStream os, float quality) throws UnartigImagingException
    {
//        PipedOutputStream retVal = new PipedOutputStream();
        RenderedOp sampledOp = reSample(load(file), resampleFactor);
        renderJpg(sampledOp, quality);
    }

    public static byte[] reSample(File file, Double resampleFactor,  float quality) throws UnartigImagingException
    {
        RenderedOp sampledOp = reSample(load(file), resampleFactor);
        return renderJpg(sampledOp, quality);
    }

    /**
     * Given an Image, create a scaled copy from that image.
     *
     * @param newImageFileName
     * @param sourceImage      Source photo rendered op
     * @param longerSidePixels target image longer side in pixels
     * @param path             Path to create new image in
     * @param applyWatermark
     */
    public static void createScaledImage(String newImageFileName, RenderedOp sourceImage, double longerSidePixels, File path, boolean applyWatermark) {
        Double scale;
        scale = longerSidePixels / (double) ImagingHelper.getMaxWidthOrHightOf(sourceImage);
        File newFile = new File(path, newImageFileName);
        createNewImage(sourceImage, scale, newFile, Registry._imageQuality, Registry._ImageSharpFactor, applyWatermark);
//        _logger.info("wrote new file " + newFile.getAbsolutePath());
    }
}