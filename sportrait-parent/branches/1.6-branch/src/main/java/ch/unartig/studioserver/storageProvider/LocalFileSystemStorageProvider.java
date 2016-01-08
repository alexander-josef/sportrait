package ch.unartig.studioserver.storageProvider;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.model.Album;
import ch.unartig.studioserver.model.SportsAlbum;
import ch.unartig.util.FileUtils;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * SPORTRAIT / unartig AG
 * Created by alexanderjosef on 01.10.15.
 */
public class LocalFileSystemStorageProvider implements FileStorageProviderInterface{
    Logger _logger = Logger.getLogger(getClass().getName());


    public LocalFileSystemStorageProvider() {
        // no need for constructor instructions
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

        saveFile((ByteArrayOutputStream) scaledImage, name, path);
    }


    public void putThumbnailImage(Album album, OutputStream scaledImage, String name) {

        File path = new File(getAlbumWebImagesPath(album), Registry.getThumbnailPath());

        saveFile((ByteArrayOutputStream) scaledImage, name, path);
    }

    public File getFineImageFile(Album album, String filename) {

        return new File(getFinePath(album).toString(), filename);
    }

    public File[] getFineImages(Album album) {

        throw new RuntimeException("not implemented yet");
    }

    public void delete(String key) {

    }

    public String getThumbnailUrl(String genericLevelId, String filename) {
        return "/" + Registry.getWebImagesContext()+"/" + genericLevelId + "/" + Registry.getThumbnailPath() + filename;
    }

    public String getDisplayUrl(String genericLevelId, String filename) {
        return "/" + Registry.getWebImagesContext()+"/" + genericLevelId + "/" + Registry.getDisplayPath() + filename;
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
        if (!finePath.exists()) {
            finePath.mkdirs();
        }
        return finePath;
    }

    private File getAlbumWebImagesPath(Album album) {
        // todo-files : refactor Album.java to not use this method anymore
        // check usage . This method should not be used anymore and replaced by a method of the storage provider interface
        return new File(Registry.getWebImagesDocumentRoot(), album.getGenericLevelId().toString());
    }
}
