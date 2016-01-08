package ch.unartig.studioserver.storageProvider;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.model.Album;
import ch.unartig.studioserver.model.SportsAlbum;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by alexanderjosef on 19.07.15.
 *
 * This interface defines how the storage and retrieval of files are handled. We want a common interface regardless of what kind of storage
 * provider is being used, e.g. a local file system, amazon S3, google cloud storage or what else
 *
 * todo: configure bucket ?
 */
public interface FileStorageProviderInterface {


    /**
     * Authenticate (if necessary) and set default settings (e.g. region) for storage provider
     */
    public void initStorageProvider();

    /**
     * Store a file
     * @param album
     * @param file file to be stored
     */
    // key == filename??
    public void putFineImage(Album album, File file) throws UAPersistenceException;


    /**
     * Given a zip archive as InputStream, upload the photo files in the archive to the file storage
     * @param sportsAlbum
     * @param fileInputStream
     * @throws UnartigException
     */
    void putFilesFromArchive(SportsAlbum sportsAlbum, InputStream fileInputStream) throws UnartigException;


    /**
     * Store a display file based on an output stream
     * @param album Reference to album
     * @param file File to be stored as Output Stream
     * @param name Name of the file to be stored (only last part, without path)
     * @throws UAPersistenceException
     */
    public void putDisplayImage(Album album, OutputStream file, String name) throws UAPersistenceException;

    /**
     * Store a thumbnail file based on an scaled output stream
     * @param album Reference to album
     * @param scaledThumbnailImage File to be stored as Output Stream
     * @param name Name of the file to be stored (only last part, without path)
     * @throws UAPersistenceException
     */
    void putThumbnailImage(Album album, OutputStream scaledThumbnailImage, String name);

    /**
     * Retrieve a stored file from the storage provider
     *
     * @param album The album where the file is from
     * @param filename Name of the file (within the album)
     * @return File that matches the key in the given storage (bucket?) // Todo: return a InputStream instead ?
     */
    public File getFineImageFile(Album album, String filename);

    /**
     * Return an array of fine images that belong to an album
     * @return
     * @param album
     */
    File[] getFineImages(Album album);

    /**
     * Delete a file with the given key from the storage provider
     * @param key The key that identifies the file to be deleted in the bucket (??) of the storage provider
     */
    public void delete(String key);

    String getThumbnailUrl(String genericLevelId, String filename);


    String getDisplayUrl(String genericLevelId, String filename);



}
