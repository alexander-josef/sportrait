package ch.unartig.studioserver.businesslogic;

import ch.unartig.studioserver.model.Album;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton implementation to hold various album import state data
 */
public final class AlbumImportStatus {
    private static final AlbumImportStatus _INSTANCE = new AlbumImportStatus(); // use lazy initialization if needed (?)
    /**
     * count all import errors for an album
     */
    Map<Album,Integer> importErrors = new ConcurrentHashMap<>();
    /**
     * count all imported photos for an album
     */
    Map<Album,Integer> photosImported = new ConcurrentHashMap<>();
    /**
     * keep a counter of remaining photos to be imported for an album
     */
    Map<Album,Integer> photosRemaining = new ConcurrentHashMap<>();
    /**
     * keep a counter on all images that are queued for number recognition via AWS rekognition
     */
    Map<Album,Integer> queuedForNumberRecognition = new ConcurrentHashMap<>();

    private AlbumImportStatus() {} // Singleton! private default constructor


    public static AlbumImportStatus getInstance() {
        return _INSTANCE;
    }


    public void setPhotosRemaining(Album album, int numberOfFineImageFiles) {
        photosRemaining.put(album,numberOfFineImageFiles);
    }

    public void incNumberRecognitionCounter(Album album) {
        Integer current = queuedForNumberRecognition.get(album);
        queuedForNumberRecognition.put(album, current==null?1:current +1);
    }

    /**
     *
     * @param album
     */
    public void photoImported(Album album) {
        Integer current = photosImported.get(album);
        photosImported.put(album,current==null?1:current+1);
        photosRemaining.put(album, photosRemaining.get(album) -1);
    }

    /**
     * Re-set / remove entry for album - import has finished for album
     * @param album
     */
    public void resetPhotosImported(Album album) {
        photosImported.remove(album);
        photosRemaining.remove(album);
    }

    public int getPhotosImported(Album album) {
        return photosImported.get(album);
    }

    public int getPhotosRemaining(Album album) {
        return photosRemaining.get(album);
    }

    public int getPhotosQueuedForNumberRecognition(Album album) {
        return queuedForNumberRecognition.get(album);
    }
}
