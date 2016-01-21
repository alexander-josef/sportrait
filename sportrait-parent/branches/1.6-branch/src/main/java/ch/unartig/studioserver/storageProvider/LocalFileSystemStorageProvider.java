package ch.unartig.studioserver.storageProvider;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.model.Album;
import ch.unartig.studioserver.model.SportsAlbum;
import ch.unartig.util.FileUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SPORTRAIT / unartig AG
 * Created by alexanderjosef on 01.10.15.
 */
public class LocalFileSystemStorageProvider implements FileStorageProviderInterface{
    Logger _logger = Logger.getLogger(getClass().getName());


    public LocalFileSystemStorageProvider() {
        // no need for constructor instructions (i.e. initialization of File System Storage)
    }

    public void deletePhotos(Album album) throws UAPersistenceException {
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(getFinePath(album));
            org.apache.commons.io.FileUtils.deleteDirectory(getDisplayPath(album));
            org.apache.commons.io.FileUtils.deleteDirectory(getThumbnailPath(album));

        } catch (IOException e) {
            _logger.error("Error while deleting photos: ",e);
            throw new UAPersistenceException(e);
        }
    }



    public void registerFromTempPath(Album album, String tempImageDirectory, boolean createThumbDisp) {

        File tempSourceDir = null;


        if (tempImageDirectory != null && !"".equals(tempImageDirectory))
        {
            tempSourceDir = new File(tempImageDirectory);
            _logger.debug("imageDir.isDirectory() = " + tempSourceDir.isDirectory());
        }


        File[] filesInTempSourceDir = tempSourceDir.listFiles(new FileUtils.JpgFileFilter());

        Set problemFiles = new HashSet();

        int i;
        // include performance measure
        long base = System.currentTimeMillis();
        for (i = 0; i < filesInTempSourceDir.length; i++) {
            _logger.debug("register Photo " + i + ", " + System.currentTimeMillis());
            File photoFile = filesInTempSourceDir[i];
            try {
                album.registerSinglePhoto(createThumbDisp, problemFiles, new FileInputStream(photoFile), photoFile.getName());
            // copy file to final location (given by storage provider)
            Registry.fileStorageProvider.putFineImage(album, photoFile);
            } catch (FileNotFoundException e) {
                _logger.error("Could not register photo from temporary location, skipping : " + photoFile.getAbsolutePath(), e);
            }
        }


        album.setProblemFiles(problemFiles);

        // if createThumbDisp call the batch job to montage a logo on the fine files
        // only after photos have been imported and scaled down to thumbnails and display images!
        // todo: introduce separate flag for logo montage˙

        /////////////////////////////////
        // Copy Logo on fine images   //
        ///////////////////////////////
        if (createThumbDisp) {
            try {
                // find solution for logo montage with S3 implementation

                // String logoScriptPath = "/Users/alexanderjosef/scripts/copyLogosComposite.sh";
                String logoScriptPath = Registry.getLogosScriptPath();
                _logger.info("calling logo script : " + logoScriptPath);
                _logger.info("with param 1 (albumId) : " + album.getGenericLevelId().toString());
                _logger.info("with param 2 (fine images directory) : " + Registry.getFineImagesDirectory());
                _logger.info("*** Output of script will be written to StdOut ***");


                ProcessBuilder pb = new ProcessBuilder(logoScriptPath, album.getGenericLevelId().toString(),Registry.getFineImagesDirectory());
                Process p = pb.inheritIO().start();     // Start the process.
                p.waitFor();                // Wait for the process to finish.
                _logger.info("Script executed successfully");
            } catch (Exception e) {
                _logger.error("Error while executing script", e);
            }
        }
        _logger.info("**********************");
        _logger.info("Import time (Java or Script): " + ((System.currentTimeMillis() - base) / 1000 + " seconds"));
        _logger.info("**********************");
    }

    public void initStorageProvider() {

    }

    public void putFineImage(Album album, File photoFile) throws UAPersistenceException {

        File destFile = new File(getFinePath(album),photoFile.getName());
        try {
            // only copy file if it's not already in the album directory
            if (!getFinePath(album).equals(photoFile.getParentFile()))
            {
                FileUtils.copyFile(photoFile, destFile);
            }
        } catch (IOException e) {
            _logger.error("Error while saving photo to local file system",e);
            throw new UAPersistenceException(e);
        }
    }

    public void putDisplayImage(Album album, OutputStream scaledImage, String name) throws UAPersistenceException {


        File path = new File(getAlbumWebImagesPath(album), Registry.getDisplayPath());
        // make sure path exists
        path.mkdir();
        saveFile((ByteArrayOutputStream) scaledImage, name, path);
    }


    public void putThumbnailImage(Album album, OutputStream scaledImage, String name) {

        File path = new File(getAlbumWebImagesPath(album), Registry.getThumbnailPath());
        // make sure path exists
        path.mkdir();
        saveFile((ByteArrayOutputStream) scaledImage, name, path);
    }

    public InputStream getFineImageFileContent(Album album, String filename) throws UAPersistenceException {

        try {
            return new FileInputStream(new File(getFinePath(album).toString(), filename));
        } catch (FileNotFoundException e) {
            _logger.error("Cannot read fine image file from local file system. Filename : " + filename,e);
            throw new UAPersistenceException(e);
        }
    }


    public Set registerStoredFinePhotos(Album album, Boolean createThumbnailDisplay) {
        // todo test
        // loop through album directory on local file system with uploaded files
        File[] files = getFinePath(album).listFiles(new FileUtils.JpgFileFilter());

        int i;
        for (i = 0; i < files.length; i++) {
            _logger.debug("register Photo "+i+", " + System.currentTimeMillis());
            File photoFile = files[i];

            // todo : check if photo is already registered for album in DB?


            try {
                album.registerSinglePhoto(createThumbnailDisplay, album.getProblemFiles(), new FileInputStream(photoFile), photoFile.getName());
            } catch (FileNotFoundException e) {
                _logger.info("Photo can not be loaded : " + photoFile.getAbsolutePath()+"; skipping",e);
            }
        }

        // todo: what about logo montage? check album#registerTempPhotos

        return null;
    }


    public int getNumberOfFineImageFiles(String key) {

        // todo implement : use to show how many files in temp location

        return  0;
    }

    public void delete(String key) {

        // todo implement
    }

    public String getThumbnailUrl(String genericLevelId, String filename) {
        return "/" + Registry.getWebImagesContext()+"/" + genericLevelId + "/" + Registry.getThumbnailPath() + filename;
    }

    public String getDisplayUrl(String genericLevelId, String filename) {
        return "/" + Registry.getWebImagesContext()+"/" + genericLevelId + "/" + Registry.getDisplayPath() + filename;
    }

    public List<String> getUploadPaths() {
        // todo implement

        return Collections.emptyList();
    }

    public void putFilesFromArchive(SportsAlbum sportsAlbum, InputStream fileInputStream) throws UnartigException {
        File finePath = getFinePath(sportsAlbum);
        _logger.debug("Extracting files to fine path : " + finePath);
        FileUtils.extractFlatZipArchive(fileInputStream, finePath);
    }


    private void saveFile(ByteArrayOutputStream scaledImage, String name, File path) {
        File destFile = new File (path,name);
        try {
            // check piped output stream. performance? memory?
            FileUtils.copyFile(scaledImage.toByteArray(), destFile);
        } catch (IOException e) {
            _logger.error("problem saving display image to local file system, see thrown exception",e);
            throw new UAPersistenceException("Problem saving display image to local file system",e);
        }
    }

    /**
     * New method in local file storage provider to return a local fine images path
     * @return
     * @param album
     */
    private File getFinePath(Album album) {

        File albumFinePath = new File(Registry.getFineImagesDirectory(), album.getGenericLevelId().toString());
        File finePath = new File(albumFinePath, Registry.getFinePath());
        finePath.mkdirs();
        return finePath;
    }

    private File getThumbnailPath(Album album) {

        return new File(getAlbumWebImagesPath(album), Registry.getThumbnailPath());
    }

    private File getDisplayPath(Album album) {

        return new File(getAlbumWebImagesPath(album), Registry.getDisplayPath());
    }

    /**
     *
     * @param album
     * @return
     * @throws UAPersistenceException
     */
    private File getAlbumWebImagesPath(Album album) throws UAPersistenceException {

        File path = new File(Registry.getWebImagesDocumentRoot(), album.getGenericLevelId().toString());
        path.mkdirs();
        return path;
    }
}
