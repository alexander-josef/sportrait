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
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.model.Album;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Uploader implements Runnable
{

    private final Logger _logger = Logger.getLogger(getClass().getName());

//    Enumeration enumeration = new String[]{"dd", "ss"};
    private String tempImageDirectory;
    private Long albumId;
    private final boolean applyLogoOnFineImages;
    private Boolean createThumbnailDisplay;
    private File tempSingleImageFile; // absolute filename to a image file in a temporary location. only used by the upload applet (JUploadAction)

    /**
     * If ImagePath = null or Album Image Path : ignore; else copy from temp tempImageDirectory to the album image path
     * @param tempFineImageServerPath Either the temporary fine directory on the server, or the fine folder under DATA if the archive has been extracted there.
     * @param albumId
     * @param processImages set to true if thumbnail and display images shall be created
     * @param applyLogoOnFineImages
     */
    public Uploader(String tempFineImageServerPath, Long albumId, Boolean processImages, boolean applyLogoOnFineImages)
    {
        this.tempImageDirectory = tempFineImageServerPath;
        this.albumId = albumId;
        this.applyLogoOnFineImages = applyLogoOnFineImages;
        if (processImages == null || processImages == Boolean.FALSE)
        {
            this.createThumbnailDisplay = Boolean.FALSE;
        } else
        {
            this.createThumbnailDisplay = Boolean.TRUE;
        }
    }


    /**
     * Starts new Thread for uploading and importing photos;
     *
     */
    public void run()
    {
        Album album;
        try
        {
            album = doImport();
            HibernateUtil.commitTransaction();
            HibernateUtil.currentSession().refresh(album.getEventCategory());
        } catch (IOException e)
        {
            _logger.error("IO-error in uploader thread. stopping thread", e);
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Error in Uploader Thread");
        }
    }

    /**
     * If temporary ImagePath = null or Album Image Path : ignore; else copy from tempImageDirectory to the album image path
     * @throws IOException
     * @throws UnartigException
     */
    private Album doImport() throws IOException, UnartigException
    {
        // why load album again? -> Album is loaded/created before in the SportsEvent class, but passed to the Uploader only as ID.
        // To prevent session timeout issues? Not important, this process is not done frequently.
        GenericLevelDAO glDao = new GenericLevelDAO();
        Album album = glDao.load(albumId, Album.class);



        if ((tempImageDirectory != null && !"".equals(tempImageDirectory)) && (tempSingleImageFile ==null || "".equals(tempSingleImageFile.getAbsolutePath())) )
        {
            // temp image path is not empty and is not a single image import: register all photos from a tempSourceDir
            album.registerPhotosFromTempLocation(tempImageDirectory, createThumbnailDisplay,applyLogoOnFineImages);
        } else if ((tempSingleImageFile ==null || "".equals(tempSingleImageFile.getAbsolutePath())) && (tempImageDirectory == null || "".equals(tempImageDirectory))) {
            // not a single image import, photos are already at file storage provider location (Uploaded via ZIP file). no temporary file path
            album.registerPhotos(createThumbnailDisplay,applyLogoOnFineImages);
        } else if (tempSingleImageFile != null)
        {
            // single image photo (only used by applet)
            // not implemented anymore
        } else
        {
            _logger.error("Uploader in unexpected state. Stopping import of photos");
            throw new RuntimeException("Uploader in unexpected state. Stopping import of photos");
        }
        return album;

    }
}
