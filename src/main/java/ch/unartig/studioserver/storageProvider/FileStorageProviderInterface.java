package ch.unartig.studioserver.storageProvider;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.model.Album;
import ch.unartig.studioserver.model.SportsAlbum;

import java.io.*;
import java.util.List;
import java.util.Set;

/**
 * Created by alexanderjosef on 19.07.15.
 *
 * This interface defines how the storage and retrieval of files are handled. We want a common interface regardless of what kind of storage
 * provider is being used, e.g. a local file system, amazon S3, google cloud storage or what else
 *
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
     * Store a file (given as output stream) to the fine image location for the album
     * @param album
     * @param fineImageAsOutputStream
     * @param fineImageFileName
     */
    void putFineImage(Album album, OutputStream fineImageAsOutputStream, String fineImageFileName);


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
     * Retrieve a stored file from the storage provider - only relevant for pre-image service operations
     *
     * @param album The album where the file is from
     * @param filename Name of the file (within the album)
     * @return Input Stream from File that matches the key in the given storage
     */
    public InputStream getFineImageFileContent(Album album, String filename) throws UAPersistenceException;

    /**
     * Method to register fine photos that are already in the correct place in the respective storage provider (i.e. local folder or a path on S3, identified by Album ID)
     * Use either registerFromTempPath(...) or this method
     * @param album The album that the fine photos, that will be registered, belong to. It contains the information to retrieve the storage location
     * @param createThumbnailDisplay Flag to indicate if thumbnails and display images shall be created as will
     * @param applyLogoOnFineImages
     * @return a Set of files that caused problems and could not be implemented
     */
    Set registerStoredFinePhotos(Album album, Boolean createThumbnailDisplay, boolean applyLogoOnFineImages);


    /**
     * Method to register (and import) photos that are located at at temp location withing the storage provide (i.e. local temp folder on server file system or temp path at S3)
     * Use either registerStoredFinePhotos(...) or this method
     * @param album
     * @param tempSourceDir
     * @param createThumbDisp
     * @param applyLogoOnFineImages
     */
    void registerFromTempPath(Album album, String tempSourceDir, boolean createThumbDisp, boolean applyLogoOnFineImages);

    /**
     * Return the number of stored fine images that belong to a folder - used for the temp upload path on S3
     * @return
     * @param folder folder - or key in S3 talk - to count (as a bucket, the current bucket will be used)
     */
    int getNumberOfFineImageFiles(String folder);

    /**
     * Delete a file from an album with the given key from the storage provider
     * @param key The key that identifies the file to be deleted in the bucket (??) of the storage provider
     * @param album the album the given key is part of (needed to determine storage location in case of S3, for example)
     */
    public void deleteFile(String key, Album album);


    /**
     * @deprecated use image service for thumbnail images
     * @param genericLevelId
     * @param filename
     * @return
     */
    String getThumbnailUrl(String genericLevelId, String filename);


    /**
     * @deprecated use image service for images of type display
     * @param genericLevelId
     * @param filename
     * @return
     */
    String getDisplayUrl(String genericLevelId, String filename);


    /**
     * Return a list of temporary upload paths; list consists of keys to the paths. todo define key
     * @return
     */
    List <String> getUploadPaths();

    /**
     * Delete all fine / thumbnail / display images from the storage provider
     * @param album
     * @throws UAPersistenceException
     */
    void deletePhotos(Album album) throws UAPersistenceException;

}
