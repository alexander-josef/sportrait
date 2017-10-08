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
 * Revision 1.2  2007/03/27 16:39:17  alex
 * refactored studioalbum into album
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.12  2006/10/28 21:57:09  alex
 * reformat
 *
 * Revision 1.11  2006/04/30 16:21:27  alex
 * removing system.outs
 *
 * Revision 1.10  2006/04/06 18:31:22  alex
 * display fixed for sports album
 *
 * Revision 1.9  2006/02/22 14:00:51  alex
 * new album nav concept works also in display
 *
 * Revision 1.8  2005/11/27 19:39:10  alex
 * fast upload
 *
 * Revision 1.7  2005/11/21 17:52:59  alex
 * no account action , photo order
 *
 * Revision 1.6  2005/11/19 16:31:43  alex
 * bookmarks of displays should work now
 *
 * Revision 1.5  2005/10/27 20:21:39  alex
 * album overview
 *
 * Revision 1.4  2005/10/26 20:40:12  alex
 * first view impl
 *
 * Revision 1.3  2005/10/24 13:50:07  alex
 * upload of album
 * import in db 
 * processing of images
 *
 ****************************************************************/
package ch.unartig.studioserver.model;

import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.storageProvider.FileStorageProviderInterface;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Photo extends GeneratedPhoto
{
    // todo: store thumbnail and display width and height pixels . here? globally?

    /**
     * default empty constructor
     */
    public Photo()
    {
    }

    private SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat hoursMinutesSecondsDateFormatter = new SimpleDateFormat("HH:mm:ss");


    /**
     * complete constructor; sets picture upload date to now
     *
     * @param filename
     * @param album
     * @param width
     * @param height
     * @param pictureTakenDate
     */
    public Photo(String filename, Album album, Integer width, Integer height, Date pictureTakenDate, String displayTitle)
    {
        setFilename(filename);
        setAlbum(album);
        setWidthPixels(width);
        setHeightPixels(height);
        setPictureTakenDate(pictureTakenDate);
        setUploadDate(new Date());
        setDisplayTitle(displayTitle);
    }

    /**
     * @return the suffix that is used for the orientation mode
     */
    public String getOrientationSuffix()
    {
        String retVal;
        if (isOrientationLandscape())
        {
            retVal = Registry._LANDSCAPE_MODE_SUFFIX;
        } else if (isOrientationPortrait())
        {
            retVal = Registry._PORTRAIT_MODE_SUFFIX;
        } else
        {
            return Registry._PORTRAIT_MODE_SUFFIX;
        }

        return retVal;
    }



    /**
     * @return true if orientation is portrait
     */
    public boolean isOrientationPortrait()
    {
        return (getWidthPixels().compareTo(getHeightPixels()) < 0);
    }

    /**
     * true if orientation if landscpae
     *
     * @return inverse of isOrientationPortrait
     */
    public boolean isOrientationLandscape()
    {
        return !isOrientationPortrait();
    }

    /**
     * this getter will be called from the view to present the picture taken time
     *
     * @return String with HH:mm
     */
    public String getShortTimeString()
    {
        return hoursMinutesSecondsDateFormatter.format(getPictureTakenDate());
    }

    public String getThumbnailUrl()
    {
        return Registry.getFileStorageProvider().getThumbnailUrl(getAlbum().getGenericLevelId().toString(), getFilename());
    }

    public String getDisplayUrl()
    {
        return Registry.getFileStorageProvider().getDisplayUrl(getAlbum().getGenericLevelId().toString(), getFilename());
    }

    public String getHighResUrl()
    {
        String highResUrl = "todo";
//        todo : concatenate high res url (not via fileStorageProvider)
        // check if free or paid image
        // used for social sharing
        return highResUrl;
    }


    public boolean equals(Object obj)
    {
        return this.getPhotoId().equals(((Photo) obj).getPhotoId());
    }



    /**
     * Return photo file by given fileStorageProvider (changed since S3 Migration)
     *
     * @return The fine file content as InputStream from the storage provider
     */
    public InputStream getFileContent()
    {
        FileStorageProviderInterface fileStorageProvider = Registry.getFileStorageProvider();
        return fileStorageProvider.getFineImageFileContent(getAlbum(), getFilename());
    }

    /**
     * convenience method to handle a new PhotoSubject
     * @param subj
     */
    public void addPhotoSubject(PhotoSubject subj)
    {
        Set subjects = getPhotoSubjects();
        if (subjects==null)
        {
            subjects = new HashSet();
        }
        boolean result = subjects.add(subj);
    }

    /**
     * convenience getter for photographer in class album
     * @return
     */
    public Photographer getPhotographer()
    {
        return getAlbum().getPhotographer();
    }
}
