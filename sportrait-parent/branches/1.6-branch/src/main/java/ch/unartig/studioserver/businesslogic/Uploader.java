/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Nov 24, 2005$
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
 * Revision 1.3  2007/05/02 14:27:55  alex
 * Uploading refactored, included fine-path server import for sportrait
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
 * Revision 1.3  2005/11/29 02:00:17  alex
 * bug fixes
 *
 * Revision 1.2  2005/11/27 19:39:10  alex
 * fast upload
 *
 * Revision 1.1  2005/11/25 10:56:58  alex
 *
 ****************************************************************/
package ch.unartig.studioserver.businesslogic;

import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.model.Album;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import ch.unartig.util.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Uploader extends Thread
{

    private Logger _logger = Logger.getLogger(getClass().getName());

//    Enumeration enumeration = new String[]{"dd", "ss"};
    private String imageDirectory;
    private Long albumId;
    private Boolean createThumbnailDisplay;
    private String tempSingleImagePath;

    /**
     * If ImagePath = null or Album Image Path : ignore; else copy from temp imageDirectory to the album image path
     * @param imageDirectory Either the temporary fine directory on the server, or the fine folder under DATA if the archive has been extracted there.
     * @param albumId
     * @param processImages set to true if thumbnail and display images shall be created using JAI
     */
    public Uploader(String imageDirectory, Long albumId, Boolean processImages)
    {
        this.imageDirectory = imageDirectory;
        this.albumId = albumId;
        if (processImages == null || processImages == Boolean.FALSE)
        {
            this.createThumbnailDisplay = Boolean.FALSE;
        } else
        {
            this.createThumbnailDisplay = Boolean.TRUE;
        }
    }

    /**
     * Simple constructor without image or directory path
     * @param albumId
     * @param processImages set to true if thumnail and display images shall be created using JAI
     */
    public Uploader(Long albumId, Boolean processImages)
    {
        this.albumId = albumId;
        if (processImages == null || processImages == Boolean.FALSE)
        {
            this.createThumbnailDisplay = Boolean.FALSE;
        } else
        {
            this.createThumbnailDisplay = Boolean.TRUE;
        }
    }

    /**
     *
     * @param tempSingleImagePath The complete Path of the temporary single image file to upload
     */
    public void uploadSingleImage(String tempSingleImagePath)
    {
        this.tempSingleImagePath = tempSingleImagePath;
        if (albumId!=null && tempSingleImagePath!=null && !"".equals(tempSingleImagePath))
        {
            imageDirectory=null;
            // this will start a separate thread and call the run method in this class.
            this.start();
            _logger.debug("Thread for registering single photo started. Image ["+tempSingleImagePath+"]");
        }
    }

    /**
     * Starts new Thread for uploading and importing a photo;
     *
     */
    public void run()
    {
        try
        {
            // create a transaction around the import:
            HibernateUtil.currentSession().beginTransaction();
            doImport();
            HibernateUtil.commitTransaction();
        } catch (IOException e)
        {
            _logger.error("IO-error in uploader thread. stopping thread", e);
        } catch (UnartigException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new RuntimeException("Error in Uploader Thread");
        }
    }

    /**
     * If ImagePath = null or Album Image Path : ignore; else copy from temp imageDirectory to the album image path
     * @throws IOException
     * @throws UnartigException
     */
    private void doImport() throws IOException, UnartigException
    {
        // todo why load album again?
        GenericLevelDAO glDao = new GenericLevelDAO();
        Album album = (Album) glDao.load(albumId, Album.class);

        if (imageDirectory != null && !"".equals(imageDirectory))
        {
            // copy images only if temp path available and not same as album path
            File sourceDir = new File(imageDirectory);
            // todo: can fine path be null and throw exception? --> log meaningful message
            if (!sourceDir.equals(album.getFinePath()))
            {
                _logger.debug("imageDir.isDirectory() = " + sourceDir.isDirectory());
                FileUtils.copyDir(sourceDir, album.getFinePath(), new FileUtils.JpgFileFilter());
            }
        }


        if (tempSingleImagePath ==null || "".equals(tempSingleImagePath))
        {
            // not a single image import: register all fine fotos this albums fine path
            album.registerPhotos(createThumbnailDisplay);
        }
        else if (tempSingleImagePath != null)
        {
            // single image photo
            Set problemFiles = new HashSet();
            File tempSingleImageFile = new File(tempSingleImagePath);

            // create the Fine File
            File finePhotoFile = new File(album.getFinePath(), tempSingleImageFile.getName());
            // Copy temp to fine file
            FileUtils.copyFile(tempSingleImageFile, finePhotoFile);

            // register using the fine file
            album.registerSinglePhoto(createThumbnailDisplay, problemFiles, finePhotoFile);
            _logger.debug("Done with registering photo [" + finePhotoFile.getAbsolutePath() + "]");
        } else
        {
            throw new RuntimeException("Uploader in unexcepted state. Stopping import of photos");
        }

    }
}
