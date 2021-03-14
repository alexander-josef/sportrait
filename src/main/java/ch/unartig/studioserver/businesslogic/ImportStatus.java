package ch.unartig.studioserver.businesslogic;

import ch.unartig.studioserver.model.Album;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton implementation to hold various album import state data
 */
public final class ImportStatus {
    private static final ImportStatus _INSTANCE = new ImportStatus(); // use lazy initialization if needed (?)
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
    /**
     * keep a counter on all images that are queued for number recognition via AWS rekognition
     */
    Map<Album, Integer> queuedForPostProcessing = new ConcurrentHashMap<>();

    private ImportStatus() {} // Singleton! private default constructor


    public static ImportStatus getInstance() {
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
        importErrors.remove(album);
    }


    public void importError(Album album) {
        Integer current = importErrors.get(album);
        importErrors.put(album,current==null?1:current+1);
    }

    public int getPhotosImported(Album album) {
        System.out.println("album = " + album);
        System.out.println("photosImported = " + photosImported);
        return photosImported.isEmpty()?0:photosImported.get(album);
    }

    public int getPhotosRemaining(Album album) {
        return photosRemaining.isEmpty()?0:photosRemaining.get(album);
    }

    public Map<Album, Integer> getPhotosRemaining() {
        return photosRemaining;
    }

    public int getPhotosQueuedForNumberRecognition(Album album) {
        System.out.println("album = " + album);
        // problem ? Test ...
        System.out.println("queuedForNumberRecognition = " + queuedForNumberRecognition);
        return queuedForNumberRecognition.isEmpty()?0:queuedForNumberRecognition.get(album);
    }

    public int getImportErrors(Album album) {
        System.out.println("album = " + album);
        System.out.println("importErrors = " + importErrors);
        return importErrors.isEmpty()?0:importErrors.get(album);
    }

    public void photoRecognitionProcessed(Album album) {
        // strange - how can we get null here for queue?
        // if message for number recognition is processed w/o the counter being increment first ??
        Integer current = queuedForNumberRecognition.get(album);
        queuedForNumberRecognition.put(album, current==null?1:current -1);
        if (queuedForNumberRecognition.get(album)==0) {
            queuedForNumberRecognition.remove(album);
        }
    }

    public void incPostProcessingCounter(Album album) {
        // todo: implement
    }
}
