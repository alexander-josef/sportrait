package ch.unartig.studioserver.storageProvider;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.model.Album;
import ch.unartig.util.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * SPORTRAIT / unartig AG
 * Created by alexanderjosef on 01.10.15.
 */
public class LocalFileStorageProvider implements FileStorageProviderInterface{
    Logger _logger = Logger.getLogger(getClass().getName());


    public LocalFileStorageProvider() {
        // no need for constructor instructions
    }

    public void initStorageProvider() {

    }

    public void putFineImage(Album album, File photoFile) throws UAPersistenceException {

        // todo-files: not only for fine images!!
        File destFile = new File(album.getFinePath(),photoFile.getName());
        try {
            // only copy file if it's not already in the album directory
            if (!album.getFinePath().equals(photoFile.getParentFile()))
            {
                FileUtils.copyFile(photoFile, destFile);
            }
        } catch (IOException e) {
            _logger.error("Error while saving photo to local file system",e);
            throw new UAPersistenceException(e);
        }
    }

    public void putDisplayImage(Album album, OutputStream file, String name) throws UAPersistenceException {


        throw new RuntimeException("not implemented yet");
    }

    public void putThumbnailImage(Album album, OutputStream scaledThumbnailImage, String name) {
        // todo-files implement
    }

    public File getFineImageFile(Album album, String filename) {
        // the "Path" in an abstract sense. Here for local file storage provider it's the file system path, but could
        // be just some kind of identifying prefix in a could storage provider
        return new File(album.getFinePath().toString(), filename);
    }

    public void delete(String key) {

    }

    public String getThumbnailUrl(String genericLevelId, String filename) {
        return "/" + Registry.getWebImagesContext()+"/" + genericLevelId + "/" + Registry.getThumbnailPath() + filename;
    }

    public String getDisplayUrl(String genericLevelId, String filename) {
        return "/" + Registry.getWebImagesContext()+"/" + genericLevelId + "/" + Registry.getDisplayPath() + filename;
    }


}
