package ch.unartig.studioserver.storageProvider;

import java.io.File;

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
     * @param credentials
     */
    public void initStorageProvider(Object credentials);

    /**
     * Store a file
     * @param file file to be stored
     */
    // key == filename??
    public void putFile(File file);

    /**
     * Retrieve a stored file from the storage provider
     * @param key
     * @return File that matches the key in the given storage (bucket?)
     */
    public File getFile(String key);

    /**
     * Delete a file with the given key from the storage provider
     * @param key The key that identifies the file to be deleted in the bucket (??) of the storage provider
     */
    public void delete(String key);

}
