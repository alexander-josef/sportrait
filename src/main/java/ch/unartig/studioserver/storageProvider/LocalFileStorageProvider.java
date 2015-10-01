package ch.unartig.studioserver.storageProvider;

import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.model.Album;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * SPORTRAIT / unartig AG
 * Created by alexanderjosef on 01.10.15.
 */
public class LocalFileStorageProvider implements FileStorageProviderInterface{
    Logger _logger = Logger.getLogger(getClass().getName());


    public LocalFileStorageProvider() {
        // no need for constructor instructions
    }

    public void getFinePath() {

    }

    public void initStorageProvider() {

    }

    public void putFile(File file) {

    }

    public File getFile(Album album, String filename) {
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
