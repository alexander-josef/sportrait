/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since Sep 22, 2005$
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
 * Revision 1.4  2007/05/02 14:27:55  alex
 * Uploading refactored, included fine-path server import for sportrait
 *
 * Revision 1.3  2007/04/17 11:03:27  alex
 * dynamic pager added
 *
 * Revision 1.2  2007/04/03 06:49:23  alex
 * photographer added, cagtegory added to album
 *
 * Revision 1.1  2007/03/27 16:39:17  alex
 * refactored studioalbum into album
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.5  2007/03/14 03:18:36  alex
 * no more price segment
 *
 * Revision 1.4  2007/03/14 02:41:01  alex
 * initial checkin
 *
 * Revision 1.3  2007/03/13 16:55:03  alex
 * template for properties
 *
 * Revision 1.2  2007/03/09 23:44:24  alex
 * no message
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.42  2006/12/06 18:42:08  alex
 * no js necessary for basic shopping cart functionality
 *
 * Revision 1.41  2006/12/05 22:51:56  alex
 * album kann jetzt freigeschaltet werden oder geschlossen sein
 *
 * Revision 1.40  2006/11/10 16:01:37  urban
 * tree bug solves
 * main.css
 *
 * Revision 1.39  2006/11/10 14:24:13  alex
 * dynamic priceinfo
 *
 * Revision 1.38  2006/11/08 09:55:03  alex
 * dynamic priceinfo
 *
 * Revision 1.37  2006/04/30 16:21:27  alex
 * removing system.outs
 *
 * Revision 1.36  2006/04/29 23:32:07  alex
 * many sola features, bugs, hibernate config
 *
 * Revision 1.35  2006/04/19 21:31:53  alex
 * session will be restored with album-bean (i.e. for bookmarked urls or so...)
 *
 * Revision 1.34  2006/04/06 18:31:22  alex
 * display fixed for sports album
 *
 * Revision 1.33  2006/03/20 15:45:33  alex
 * first check in for new sports album logic and db changes
 *
 * Revision 1.32  2006/03/20 15:33:32  alex
 * first check in for new sports album logic and db changes
 *
 * Revision 1.31  2006/03/08 17:42:26  alex
 * small fixes
 *
 * Revision 1.30  2006/03/03 16:54:56  alex
 * minor fixes
 *
 * Revision 1.29  2006/02/28 15:06:35  alex
 * link in overview links to first page
 *
 * Revision 1.28  2006/02/28 14:57:46  alex
 * added more resources (email for order confirmation), small fixes
 *
 * Revision 1.27  2006/02/23 14:37:42  alex
 * admin tool: new category works now
 *
 * Revision 1.26  2006/02/22 16:10:25  alex
 * added back link
 *
 * Revision 1.25  2006/02/22 14:00:51  alex
 * new album nav concept works also in display
 *
 * Revision 1.24  2006/02/16 17:13:46  alex
 * admin interface: deletion of levels works now
 *
 * Revision 1.23  2006/02/15 15:57:03  alex
 * bug [968] fixed. admin interface does that now
 *
 * Revision 1.22  2006/02/13 16:15:28  alex
 * but [968]
 *
 * Revision 1.21  2006/02/10 14:21:49  alex
 * admin tool: edit level partly ...
 *
 * Revision 1.20  2006/02/08 18:04:50  alex
 * first steps for album type configuration
 *
 * Revision 1.19  2006/01/24 15:38:35  alex
 * file ending is now .html
 *
 * Revision 1.18  2005/11/30 10:47:38  alex
 * bug fixes
 *
 * Revision 1.17  2005/11/29 02:00:17  alex
 * bug fixes
 *
 * Revision 1.16  2005/11/28 17:52:16  alex
 * bug fixes
 *
 * Revision 1.15  2005/11/27 19:39:10  alex
 * fast upload
 *
 * Revision 1.14  2005/11/25 11:09:09  alex
 * removed system outs
 *
 * Revision 1.13  2005/11/25 10:56:58  alex
 *
 * Revision 1.12  2005/11/22 19:45:46  alex
 * admin actions, configurations
 *
 * Revision 1.11  2005/11/20 21:24:32  alex
 * side navigation fixes
 *
 * Revision 1.10  2005/11/19 11:08:20  alex
 * index navigation works, (extended GenericLevel functions)
 * wrong calculation of fixed checkout overview eliminated
 *
 * Revision 1.9  2005/11/18 19:15:52  alex
 * stuff ...
 *
 * Revision 1.8  2005/11/16 14:26:49  alex
 * validator works for email, new library
 *
 * Revision 1.7  2005/11/07 21:57:43  alex
 * admin interface refactored
 *
 * Revision 1.6  2005/11/07 17:38:26  alex
 * admin interface refactored
 *
 * Revision 1.5  2005/11/05 21:41:58  alex
 * overview und links in tree menu
 *
 * Revision 1.4  2005/10/26 14:34:32  alex
 * first version of album overview
 * new mappings in struts for the /album/** url
 *
 * Revision 1.3  2005/10/24 14:37:55  alex
 * small fixes, creating directories
 *
 * Revision 1.2  2005/10/24 13:50:07  alex
 * upload of album
 * import in db
 * processing of images
 *
 * Revision 1.1  2005/10/08 10:52:36  alex
 * jstl 1.1 integrated, new web.xml
 *
 * Revision 1.4  2005/10/06 14:30:09  alex
 * generating the nav tree recursivly works
 *
 * Revision 1.3  2005/10/06 11:06:33  alex
 * generating the nav tree
 *
 * Revision 1.2  2005/10/06 08:54:04  alex
 * cleaning up the model
 *
 * Revision 1.1  2005/09/26 18:37:45  alex
 * *** empty log message ***
 *
 ****************************************************************/
package ch.unartig.studioserver.model;

import ch.unartig.controller.Client;
import ch.unartig.exceptions.NotAuthorizedException;
import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.businesslogic.AlbumType;
import ch.unartig.studioserver.businesslogic.GenericLevelVisitor;
import ch.unartig.studioserver.imaging.ExifData;
import ch.unartig.studioserver.imaging.ImagingHelper;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;
import ch.unartig.studioserver.persistence.DAOs.OrderItemDAO;
import ch.unartig.studioserver.persistence.DAOs.PhotoDAO;
import ch.unartig.studioserver.persistence.DAOs.PriceDAO;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import ch.unartig.util.FileUtils;

import javax.media.jai.RenderedOp;
import java.io.*;
import java.util.*;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;


/**
 *
 */
public class Album extends GeneratedAlbum {
    private Set problemFiles;
    /**
     * this String defines the action url that is to be called for viewing this album
     */
    private String actionString;
    /**
     * this will be overwritten be inheriting classes
     */
    private String actionStringPart = "/album/";

    /**
     * default constructor
     */
    public Album() {
    }

    /**
     * full constructor needed because of hbm2java generation
     */
    public Album(String navTitle, String longTitle, String description, String quickAccess, Boolean aPrivate, Boolean publish, String privateAccessCode, String albumTypeString, Photographer photographer, Event event, EventCategory eventCategory, Set photos, Set products) {
    }

    /**
     * actionString must be lazily initialized
     * <br>cannot be initialized during object creation ... null pointer exception
     *
     * @return action url string
     */
    private String lazyInitActionString() {
        if (actionString == null) {
            actionString = getActionStringPart() + getGenericLevelId().toString() + "/" + getNavTitle() + ".html";
        }
        _logger.debug("returning :" + actionString);
        return actionString;
    }

    /**
     * @param visitor Visitor to use
     */
    public void accept(GenericLevelVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * @param navTitle
     * @param longTitle
     * @param description
     */
    public Album(String navTitle, String longTitle, String description) {
        setNavTitle(navTitle);
        setLongTitle(longTitle);
        setDescription(description);
    }



    public List listChildrenForNavTree() {
        return Collections.EMPTY_LIST;
    }

    public List listChildren() {
        return null;
    }

    public Class getParentClazz() {
        return Event.class;
    }

    public String[] getIndexNavEntry() {
        return new String[]{getIndexNavLink(), getNavTitle()};
    }

    public String getEventDateDisplay() {
        return getEvent().getEventDateDisplay();
    }


    /**
     * convenience method to add an event
     *
     * @param event
     */
    public void setParentLevel(GenericLevel event) {
        _logger.debug("$$$$$$$$$$$$$$$$$$$$$$$$$  addParentLevel NARROW  $$$$$$$$$$$$$$$$$$$$$$$$4");
        Event e = (Event) event;
        setEvent(e);
        e.getAlbums().add(this);
    }

    public GenericLevel getParentLevel() {
        return getEvent();
    }

    /**
     * set the hibernate - property albumTypeString using the AlbumType
     *
     * @param albumType
     */
    public void setAlbumType(AlbumType albumType) {
        this.setAlbumTypeString(albumType.getDesignator());
    }

    public AlbumType getAlbumType() {
        return AlbumType.getAlbumType(this.getAlbumTypeString());
    }

    /**
     * Register Photos only from the importData stream;
     * The import data stream comes from a file with the following format:
     * <p/>
     * [filename];[width pixels];[height pixels];[date in the format MM/dd/yy]
     * <p/>
     * example:
     * CIMG1114.JPG;3264;2448;2/18/07
     *
     * ZIP archive contains import.txt as well as display / thumbnail folders
     * optionally only import.txt is uploaded in archive
     *
     * @param importDataStream
     * @param isZipArchive
     */
    public void registerPhotosFromImportData(InputStream importDataStream, boolean isZipArchive) {
        BufferedReader br;
        try {
            // todo create fine and web images anyway!!
            if (isZipArchive) { // deal with zip archive that contains images and import.txt
                ZipEntry zipEntry;
                // importDataStream is zip file
                ZipInputStream zis = new ZipInputStream(importDataStream);
                // todo-files: no need for file system directories:
                // make sure paths for thumbnails and displays exists:
                boolean thumbOk = true;
                if (!getThumbnailPath().exists()) {
                    thumbOk = getThumbnailPath().mkdirs();
                }
                boolean displayOk = true;
                if (!getDisplayPath().exists()) {
                    displayOk = getDisplayPath().mkdirs();
                }
                if (!(thumbOk && displayOk)) {
                    throw new RuntimeException("Error importing from Zip file, can not create directories for thumbnail or display");
                }
                while ((zipEntry = zis.getNextEntry()) != null) {
                    if (zipEntry.getName().toLowerCase().startsWith("fine/")) {
                        throw new RuntimeException("fine images not yet supported");
                    }
                    // todo-files: replace with storage-provider method
                    FileUtils.copyFile(zis, new File(this.getAlbumWebImagesPath(), zipEntry.getName()), false, true);
                }
                File importFile = new File(this.getAlbumWebImagesPath(), "import.txt");
                if (importFile.exists()) {
                    br = new BufferedReader(new FileReader(importFile));
                } else {
                    throw new RuntimeException("import.txt must exist for importing photos.");
                }
            } else { // only import.txt has been uploaded:
                InputStreamReader reader = new InputStreamReader(importDataStream);
                br = new BufferedReader(reader);
            }

            // process import.txt: 
            while (br.ready()) {
                // for each line
                String line = "";
                try {
                    line = br.readLine();
                    String parts[] = line.split(";");
                    if (parts.length != 4) {
                        _logger.info("probably last line in file: skipping invalid line while importing from import.txt : [" + line + "]");
                        continue;
                    }
                    Photo photo = new Photo(parts[0], this, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), new Date(Long.parseLong(parts[3])), parts[0]);
                    add(photo);
                } catch (IOException e) {
                    _logger.error("The following line causes problems while importing a photo from import.txt : [" + line + "]");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error importing from zip file or import.txt");
        }
    }


    /**
     * Register all photos from the temporary 'fine' Path in the db; reads the EXIF picture-take date and enters it for every photo record<br/>
     * <p/> Needed: temporary fine photos
     * <p/> copies the temp file to it's final location according to the given fileStorageProvider
     * <p/> This method shall fail gracefully with a exception message in case a photo can not be registered (corrupt file, not a foto-file etc.)
     *
     * @param tempSourceDir This is the path given in the import UI as temporary location where the files to be imported have been uploaded
     * @param createThumbDisp set to true to create the display and thumbnail images
     */
    public void registerPhotos(File tempSourceDir, boolean createThumbDisp) {
        _logger.debug("start registerPhotos, " + System.currentTimeMillis());

        // todo-files
        // solve with listing from storage provider. create new list method in interface


        // loop through temp directory on local file system with uploaded files (independent of file storage provider for the temporary photo location)
        File[] filesInTempSourceDir = tempSourceDir.listFiles(new FileUtils.JpgFileFilter());

        Set problemFiles = new HashSet();

        int i;
        // include performance measure
        long base = System.currentTimeMillis();
        for (i = 0; i < filesInTempSourceDir.length; i++) {
            _logger.debug("registerPhoto "+i+", " + System.currentTimeMillis());
            File photoFile = filesInTempSourceDir[i];
            registerSinglePhoto(createThumbDisp, problemFiles, photoFile);
            // copy file to final location (given by storage provider)
            Registry.fileStorageProvider.putFineImage(this, photoFile);
        }


        setProblemFiles(problemFiles);

        // if createThumbDisp call the batch job to montage a logo on the fine files for the registering album
        // todo: introduce separate flag for logo montage˙

        /////////////////////////////////
        // Copy Logo on display images //
        /////////////////////////////////
        if (createThumbDisp) {
            try {
                // todo-files
                // find solution for logo montage

                // String logoScriptPath = "/Users/alexanderjosef/scripts/copyLogosComposite.sh";
                String logoScriptPath = Registry.getLogosScriptPath();
                _logger.info("calling logo script : " + logoScriptPath);
                _logger.info("with param 1 (albumId) : " + getGenericLevelId().toString());
                _logger.info("with param 2 (fine images directory) : " + Registry.getFineImagesDirectory());
                _logger.info("*** Output of script will be written to StdOut ***");

                getDisplayPath().mkdirs();
                getThumbnailPath().mkdirs();

                ProcessBuilder pb = new ProcessBuilder(logoScriptPath, getGenericLevelId().toString(),Registry.getFineImagesDirectory());
                Process p = pb.inheritIO().start();     // Start the process.
                p.waitFor();                // Wait for the process to finish.
                _logger.info("Script executed successfully");
            } catch (Exception e) {
                _logger.error("Error while executing script", e);
            }
        }
        _logger.info("**********************");
        _logger.info("Import time (Java or Script): " + ((System.currentTimeMillis() - base)/1000 + " seconds"));
        _logger.info("**********************");

    }

    /**
     * Registers a single photo in the db and creates the thumb and disp images if the first argument is true
     * @param createThumbDisp set to true to create the display and thumbnail images
     * @param problemFiles    A set of accumulated files that caused problems during import
     * @param photoFile       The image file to import (in its temporary location) // todo-files: temporary?
     */
    public void registerSinglePhoto(boolean createThumbDisp, Set problemFiles, File photoFile) {


        // todo-files: if param photoFile comes from temp local file system we are fine. Otherwise a new solution needs to be found.

        Integer pictureWidth;
        Integer pictureHeight;
        Date pictureTakenDate;
        String filename;
        try {
            // this causes eof problems ....
//            FileUtils.copyFile(photoFile,new File(getFinePath(),photoFile.getName()));

            // either use JAI or ImgScalr:

// *** JAI:


            // todo-files : use bufferedImage instead of file?
            RenderedOp fineImage = ImagingHelper.load(photoFile);
            pictureWidth = fineImage.getWidth();
            pictureHeight = fineImage.getHeight();





// **** ImgSclr :

/*
            BufferedImage finePhotoBufferedImage = ImageIO.read(photoFile);
            pictureWidth = finePhotoBufferedImage.getWidth();
            pictureHeight = finePhotoBufferedImage.getHeight();
            RenderedOp fineImage = null; // just not to cause not a compilation problem
*/


            ExifData exif = new ExifData(photoFile);


            _logger.debug("read width and height");
            Date exifDate;
            exifDate = exif.getPictureTakenDate();
            _logger.info("registerPhoto 3, " + System.currentTimeMillis());

            if (exifDate != null) {
                pictureTakenDate = new Date(exifDate.getTime());
            } else {
                pictureTakenDate = new Date(0);
                _logger.error("Unable to determine date of file : " + photoFile.getName());
                //noinspection unchecked
                problemFiles.add(photoFile);
            }
            filename = photoFile.getName();
            String displayTitle = filename;

            _logger.debug("filename : " + filename);
            _logger.debug("***********  " + pictureTakenDate + "  **************");

            Photo photo = new Photo(filename, this, pictureWidth, pictureHeight, pictureTakenDate, displayTitle);
            // add to db:
            add(photo);
            // check if photo already exists?
            // copy file?
            // process: choose a (local?) directory with photos to import and a project
            // import routine checks if photo already exists
            // once imported thumbnailer will run ... (and the ones already 'thumbnailed' ?

            if (createThumbDisp ) {
                // todo-files: should not be necessary here / put in storage-provider implementation
                getDisplayPath().mkdirs();
                getThumbnailPath().mkdirs();

                //
/*

// ******  use imgscalr to produce thumb/disp images :


                BufferedImage display = Scalr.resize(finePhotoBufferedImage,Scalr.Method.SPEED, Registry.getDisplayPixelsLongerSide());
                BufferedImage thumbnail = Scalr.resize(finePhotoBufferedImage,Scalr.Method.SPEED, Registry.getThumbnailPixelsLongerSide());

                ImageIO.write(display,"jpg",new File(getDisplayPath(), filename));
                ImageIO.write(thumbnail,"jpg",new File(getThumbnailPath(), filename));

                // first results: really bad: 121 s instead of 70s with JAI only
                _logger.info("wrote thumb/disp images via ImgScalr for image : "+ filename);

// ******

*/

                // create thumbnail/display (with JAI operations)

                // now trying new method and commenting following line out ...
                OutputStream scaledDisplayImage = ImagingHelper.createScaledImage(fineImage, Registry.getDisplayPixelsLongerSide().doubleValue(), false);
                Registry.fileStorageProvider.putDisplayImage(this, scaledDisplayImage, photoFile.getName());
                // create thumbnail
                OutputStream scaledThumbnailImage = ImagingHelper.createScaledImage(fineImage, Registry.getThumbnailPixelsLongerSide().doubleValue(), false);
                Registry.fileStorageProvider.putThumbnailImage(this, scaledThumbnailImage, photoFile.getName());



                // ImagingHelper.createScaledImage(filename, fineImage, Registry.getDisplayPixelsLongerSide().doubleValue(), getDisplayPath(), false);



            }

        } catch (IOException e) {
            _logger.info("Problems while copying the file or accessing the EXIF data of file " + photoFile.getName(), e);
            //noinspection unchecked
            problemFiles.add(photoFile);
/*
        } catch (UnartigImagingException e1) {
            _logger.info("Problem processing the image; continue with next image", e1);
            //noinspection unchecked
            problemFiles.add(photoFile);
*/
        } catch (UAPersistenceException e2) {
            _logger.info("Problem saving image; continue with next image", e2);
            //noinspection unchecked
            problemFiles.add(photoFile);
        } catch (Exception e3) {
            _logger.info("unknown error; continue with next image", e3);
            //noinspection unchecked
            problemFiles.add(photoFile);
        }
    }


    /**
     * Process all images in the fine folder and create:
     * - display folder and images
     * - thumbnail folder and images<br/>
     * <p>Fine Folder must exist!</p>
     *
     * @throws ch.unartig.exceptions.UnartigException
     *
     */
    public void processImages() throws UnartigException {
        getDisplayPath().mkdirs();
        getThumbnailPath().mkdirs();
        // all fine fineImages:
        if (getFinePath() == null || !getFinePath().isDirectory()) {
            throw new UnartigException("fine Path does not exist or is not directory");
        }
        _logger.debug("Going to process fineImages in directory : " + getFinePath().getAbsolutePath());
        // todo-files
        File[] fineImages = getFinePath().listFiles(new FileUtils.JpgFileFilter());
        Double displayScale;
        Double thumbnailScale;
        for (int i = 0; i < fineImages.length; i++) {
            File image = fineImages[i];
            _logger.debug(" ... image : " + image.getName());
            RenderedOp fineImage = ImagingHelper.load(image);

            // DISPLAY : (apply watermark)
            displayScale = Registry.getDisplayPixelsLongerSide().doubleValue() / (double) ImagingHelper.getMaxWidthOrHightOf(fineImage);
            File displayFile = new File(getDisplayPath(), image.getName());
            ImagingHelper.createNewImage(fineImage, displayScale, Registry._imageQuality, Registry._ImageSharpFactor, true);

            // THUMBNAIL:
            thumbnailScale = Registry.getThumbnailPixelsLongerSide().doubleValue() / (double) ImagingHelper.getMaxWidthOrHightOf(fineImage);
            File thumbnailFile = new File(getThumbnailPath(), image.getName());
            ImagingHelper.createNewImage(fineImage, thumbnailScale, Registry._imageQuality, Registry._ImageSharpFactor, false);
        }
    }

    /**
     * overriden for album: go to the album-action
     *
     * @return album action link
     */
    public String getIndexNavLink() {
        return getActionString() + "?page=1";
    }

    /**
     * @return the action url that is to called for viewing this album
     */
    public String getActionLink() {
        return getActionString();
    }


    public String getLevelType() {
        return Registry._NAME_ALBUM_LEVEL_TYPE;
    }

    /**
     * Convenience method to add and save a new photo that belongs to this album;
     * Commits a transaction and opens a new one for each photo that is stored.
     *
     * @param photo
     * @throws UAPersistenceException
     */
    private void add(Photo photo) throws UAPersistenceException {
        PhotoDAO phDao = new PhotoDAO();
        try {
            HibernateUtil.beginTransaction();
            getPhotos().add(photo);
            phDao.saveOrUpdate(photo);
            HibernateUtil.commitTransaction();
            _logger.info("wrote photo with id : " + photo.getPhotoId().toString());
        } catch (UAPersistenceException e) {
            HibernateUtil.rollbackTransaction();
            _logger.error("error while saving photo", e);
        }
        // make sure a new session is opened for a transaction:

        HibernateUtil.currentSession().beginTransaction();

    }

    /**
     * Returns the absolute 'fine'-path for the directory of the high-res images of this album<br/>
     * Creates all directories if needed
     *
     * @return a directory
     * @deprecated
     */
    public File getFinePath() {

        // todo-files : what to return in case of storage-provider implementation? Check usage.
        // --> return file  in case of local file storage? return prefix in case of cloud storage (and also file storage?)
        // check usage . This method should not be used anymore and replaced by a method of the storage provider interface
        File albumFinePath = new File(Registry.getFineImagesDirectory(), getGenericLevelId().toString());
        File finePath = new File(albumFinePath, Registry.getFinePath());
        if (!finePath.exists()) {
            finePath.mkdirs();
        }
        return finePath;
    }

    /**
     *
     * @return
     * @deprecated
     */
    private File getThumbnailPath() {
        // todo-files : what to return in case of storage-provider implementation?
        // check usage . This method should not be used anymore and replaced by a method of the storage provider interface
        return new File(getAlbumWebImagesPath(), Registry.getThumbnailPath());
    }


    /**
     *
     * @return
     * @deprecated
     */
    private File getDisplayPath() {
        // todo-files : what to return in case of storage-provider implementation?
        // check usage . This method should not be used anymore and replaced by a method of the storage provider interface
        return new File(getAlbumWebImagesPath(), Registry.getDisplayPath());
    }

    /**
     *
     * @return
     * @deprecated
     */
    private File getAlbumWebImagesPath() {
        // todo-files : what to return in case of storage-provider implementation?
        // check usage . This method should not be used anymore and replaced by a method of the storage provider interface
        return new File(Registry.getWebImagesDocumentRoot(), getGenericLevelId().toString());
    }

    public void setProblemFiles(Set problemFiles) {
        this.problemFiles = problemFiles;
    }

    public Set getProblemFiles() {
        return problemFiles;
    }

    public List getPhotosAsList() {
        return new ArrayList(getPhotos());
    }

    /**
     * overridden from GenericLevel for checking the type of level
     *
     * @return true for album
     */
    public boolean isAlbumLevel() {
        return true;
    }

    /**
     * Delete Album:
     * delete all photos in db<br>
     * remove references in orderitems
     * delete files
     * all order items that have a photo of this album need to set their photofilename and set the photoid to null
     */
    public void deleteLevel() throws UAPersistenceException {
        OrderItemDAO oiDao = new OrderItemDAO();

        Set orderItemsForAlbum = new HashSet();
        // todo  this does not perform  ... create a HQL expression to update all orderitems
        for (Object o : getPhotos()) {
            Photo photo = (Photo) o;
            orderItemsForAlbum.addAll(photo.getOrderItems());
            photo.setOrderItems(Collections.EMPTY_SET);
        }

        // removing reference to photo from orderItems
        // todo check performance?
        for (Object anOrderItemsForAlbum : orderItemsForAlbum) {
            OrderItem orderItem = (OrderItem) anOrderItemsForAlbum;
            orderItem.setPhotoFileName(orderItem.getPhoto().getFilename());
            orderItem.setPhoto(null);
            orderItem.setProduct(null);
            _logger.debug("trying to save order item : " + orderItem);
            _logger.debug("trying to save Product : " + orderItem.getProduct());
            try {
                // save includes cascaded save of product
                oiDao.saveOrUpdate(orderItem);
            } catch (UAPersistenceException e) {
                throw new UAPersistenceException("can not save orderitem", e);
            }

        }


        // now delete the album image directories on disk
        try {
            // todo-files
            org.apache.commons.io.FileUtils.deleteDirectory(getFinePath());
            org.apache.commons.io.FileUtils.deleteDirectory(getDisplayPath());
            org.apache.commons.io.FileUtils.deleteDirectory(getThumbnailPath());
        } catch (IOException e) {
            _logger.error(e);
        }

        // eventCategory contains reference to album (this caused a temporary problem if caching is turned on for albums in eventcategories)
        getEventCategory().getAlbums().remove(this);

    }

    /**
     * Convenience method to access last photo in album
     * todo: the term 'album' is not specific enough anymore with sportsalbums ... there, the last photo of one 'view' is not necessarily the last photo in the whole album
     *
     * @return last photo in album
     * @throws ch.unartig.exceptions.UnartigException
     *
     */
    public Photo getLastPhotoInAlbumAndSelection() throws UnartigException {
        _logger.debug("Album.getLastPhotoInCategoryAndSelection xxxx");
        // reload this album
        GenericLevelDAO glDao = new GenericLevelDAO();
        List photoList = new ArrayList(((Album) glDao.load(this.getGenericLevelId(), Album.class)).getPhotos());
        Photo retVal = (Photo) (photoList.get(getNumberOfPhotos() - 1));
        _logger.debug("returning photo : " + retVal);
        return retVal;
    }

    /**
     * method to get first photo in album
     *
     * @return first photo in album
     * @throws ch.unartig.exceptions.UnartigException
     *
     */
    public Photo getFirstPhotoInAlbum() throws UnartigException {
        PhotoDAO photoDao = new PhotoDAO();
        return photoDao.getFirstPhotoFor(this);
    }

    public String getActionString() {
        _logger.debug("calling action string ...");
        lazyInitActionString();
        return actionString;
    }

    public String getActionStringPart() {
        return actionStringPart;
    }

    public void setActionStringPart(String actionStringPart) {
        this.actionStringPart = actionStringPart;
    }


    /**
     * Given the map of productTypeids (key) and the priceids (value) set the collections of products for this album
     *
     * @param productPrices
     * @throws ch.unartig.exceptions.UAPersistenceException
     *
     */
    public void setProductPricesMap(Map productPrices) throws UAPersistenceException {

        // todo deprecate once we have to zk solution that handles single product changes?
        // product entries per album: only one per productType
        Set productTypeIds = productPrices.keySet();
        PriceDAO priceDao = new PriceDAO();
        for (Iterator iterator = productTypeIds.iterator(); iterator.hasNext();) {
            String productTypeIdString = (String) iterator.next();
            String priceIdString = (String) productPrices.get(productTypeIdString);
            //producttypes with priceid <=0 are not set for this album
            final Long productTypeId = Long.valueOf(productTypeIdString);
            final Long priceId = Long.valueOf(priceIdString);
            if (priceId > 0 && !getAvailableProductTypes(false).keySet().contains(productTypeId)) {
                // producttype does not yet exist for this album; create new product
                getProducts().add(new Product(productTypeId, priceId, this));
            }
            // product exists, and producttype is already available for album. update?
            else if (priceId > 0) {
                final Product availableProduct = getProductFor(productTypeId);
                final Price newPrice = priceDao.load(priceId);
                _logger.debug("available product : [" + availableProduct + "] old price: [" + availableProduct.getPrice() + "] new Price [" + newPrice + "]");
                availableProduct.setPrice(newPrice);
                // update newPrice
            } else {
                removeProductFor(productTypeId);
            }


        }
    }

    /**
     * if a product entry exists that has the passed productTypeId as identifier for its productType, delete it?
     *
     * @param productTypeId
     * @return
     */
    private boolean removeProductFor(Long productTypeId) {
        return getProducts().remove(getProductFor(productTypeId));
    }

    /**
     * There is zero or one product for a given product-type per album.
     *
     * @param productTypeId The ID of the ProductType
     * @return <p>The product that has the productType identified by the productTypeId.</p>
     *         <p>NULL, if no product exists with the given productType.</p>
     */
    public Product getProductFor(Long productTypeId) {
        // make a query or use the collection???
        for (Object productO : getProducts()) {
            Product product = (Product) productO;
            if (product.getProductType().getProductTypeId().equals(productTypeId)) {
                return product;
            }
        }
        _logger.debug("no product found with given productTypeId"); // this is fine. It will then show as not available in the overview.
        return null;
    }

    /**
     * Return a map containing the productTypeId as key and the productType as Value
     *
     * @return
     * @param onlyActiveProducts set to true to filter for active products, in the shopping cart, for example.
     * For product configuration, more likely all products are needed
     */
    public Map getAvailableProductTypes(boolean onlyActiveProducts) {

        Map productTypeMap = new HashMap();
        Set products  = onlyActiveProducts ? getActiveProducts():getProducts();
        for (Iterator iterator = products.iterator(); iterator.hasNext();) {
            Product product = (Product) iterator.next();
            ProductType productType = product.getProductType();
            productTypeMap.put(productType.getProductTypeId(), productType);
        }
        return productTypeMap;
    }


    /**
     * Write access check for an album; client needs to be either admin or owner of the album.
     *
     * @param client
     * @throws NotAuthorizedException
     */
    protected void checkWriteAccessFor(Client client) throws NotAuthorizedException {
        _logger.debug("checking access for user [" + client.getUserProfile().getUserName() + "] with roles [" + client.getUserProfile().getRoles() + "]");
        _logger.debug("client is admin? " + client.isAdmin());
        _logger.debug("Photographer : " + getPhotographer());
        // special case no photographer:
        if (getPhotographer() == null && client.isAdmin()) {
            // album without an album ...
            return;
        } else if (getPhotographer() == null && !client.isAdmin()) {
            throw new RuntimeException("Unexpected state : no photographer album shown to a non-admin!!");
        }
        // regular check:
        if (!(client.isAdmin() || getPhotographer().equals(client.getPhotographer()))) {
            throw new NotAuthorizedException("Not Administrator rights");
        }

    }

    /**
     * Only products that are not flagged for inactive shall be shown in the product information or shopping cart.
     *
     * @return Active products (products that don't have the inactvie flag)
     */
    public Set getActiveProducts() {
        Set<Product> activeProducts = new HashSet<Product>();
        Set allProductsForAlbum = getProducts();
        for (Object anAllProductsForAlbum : allProductsForAlbum) {
            Product product = (Product) anAllProductsForAlbum;
            if (product.getInactive() == null || !product.getInactive()) { // either null or NOT inactive
                activeProducts.add(product);
            }
        }
        return activeProducts;
    }

    /**
     *  
     * @return true In case a free download is offered on the display page offers this method
     */
    public boolean isHasFreeHighResDownload() {
        for (Object o : getActiveProducts()) {
            Product product = (Product) o;
            if (product.isDigitalProduct() && (product.getPrice().getPriceCHF().floatValue()==0.0f)) {
                return true;
            }
        }
        return false;
    }


    /**
     * sorts the albums for a nice list when showning all albums sorted by event and category (sales report)
     * Album comparator using Generics;
     */
    public static class EventCategoryComparator implements Comparator<Album> {// needs to be static

        public int compare(Album album, Album albumToCompare) {

            // first order is event name
            final int eventTitleComparison = album.getEvent().getLongTitle().compareTo(albumToCompare.getEvent().getLongTitle());
            if (eventTitleComparison != 0) {
                return eventTitleComparison;
            }
            // events are equal: we sort by the eventcategory id (should be in a sensible order thanks to the list-index of eventCategory)
            return album.getEventCategory().getEventCategoryId().compareTo(albumToCompare.getEventCategory().getEventCategoryId());
        }
    }
}
