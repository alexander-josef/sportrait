package ch.unartig.studioserver.businesslogic;

import ch.unartig.studioserver.model.Album;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton implementation to hold various album import state data
 * Contains maps of
 * - importErrors
 * - photosImported counter
 * - photosRemaining to be imported counter
 * - queuedForNumberRecognition - counting photos that are pending to in queue for the image recognition service
 * - queuedForPostPostProcessing - counting photos to be post-processed after the initial import (face / number comparison)
 *
 * the key of the map being the album object, the value the counters (status) described above
 */
public final class ImportStatus {
    private static final Logger _logger = LogManager.getLogger();

    private static final ImportStatus _INSTANCE = new ImportStatus(); // use lazy initialization if needed (?)
    /**
     * count all import errors for an album
     */
    private final Map<Album,Integer> importErrors = new ConcurrentHashMap<>();
    /**
     * count all imported photos for an album
     */
    private final Map<Album,Integer> photosImported = new ConcurrentHashMap<>();
    /**
     * keep a counter of remaining photos to be imported for an album
     */
    private final Map<Album,Integer> photosRemaining = new ConcurrentHashMap<>();
    /**
     * keep a counter on all images that are queued for number recognition via AWS rekognition
     */
    private final Map<Album,Integer> queuedForNumberRecognition = new ConcurrentHashMap<>();
    /**
     * keep a counter on all images that are queued for number recognition via AWS rekognition
     */
    private final Map<Album, Integer> queuedForPostProcessing = new ConcurrentHashMap<>();

    private ImportStatus() {} // Singleton! private default constructor


    public static ImportStatus getInstance() {
        return _INSTANCE;
    }


    /**
     * Used by REST API to return a map of currently open imports.
     * Considers all open imports, incl. queued for number recognition and post-processing
     * (instead of just using 'getPhotosRemaining() previously used for the API)
     * @return a set
     */
    public Set<Album> getCurrentlyImportedAlbums() {
        Set<Album> currentlyImportedAlbums = new HashSet<>();// think of ordered (by ID ? by event category ?) set - use SortedSet
        currentlyImportedAlbums.addAll(photosRemaining.keySet());
        currentlyImportedAlbums.addAll(queuedForNumberRecognition.keySet());
        currentlyImportedAlbums.addAll(queuedForPostProcessing.keySet());
        if (!currentlyImportedAlbums.isEmpty()) {
            _logger.debug("returning currently imported albums : " + currentlyImportedAlbums);
        }
        return currentlyImportedAlbums;
    }

    public Map<Album, Integer> getPhotosRemaining() {
        return photosRemaining;
    }

    public int getPhotosRemaining(Album album) {
        return photosRemaining.isEmpty()?0:photosRemaining.get(album);
    }

    public void setPhotosRemaining(Album album, int numberOfFineImageFiles) {
        photosRemaining.put(album,numberOfFineImageFiles);
    }

    public void incNumberRecognitionCounter(Album album) {
        _logger.debug("calling 'incNumberRecognitionCounter' for album : " + album);
        // different instances of albums ? because import happens in different threads? because album is re-loaded again?
        // but maps should now allow for key objects that are equal (and equality for albums should be by their IDs ...)
        // there are lots of comments on the topic, see for instance :
        // https://docs.jboss.org/hibernate/stable/core.old/reference/en/html/persistent-classes-equalshashcode.html
        // or https://stackoverflow.com/questions/1638723/how-should-equals-and-hashcode-be-implemented-when-using-jpa-and-hibernate
        // or https://vladmihalcea.com/the-best-way-to-implement-equals-hashcode-and-tostring-with-jpa-and-hibernate/
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
     * Called when ...  todo
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
        _logger.debug("album = " + album);
        _logger.debug("photosImported = " + photosImported);
        return photosImported.isEmpty()?0:photosImported.get(album);
    }


    public int getPhotosQueuedForNumberRecognition(Album album) {
        _logger.debug("queuedForNumberRecognition (all entries) = " + queuedForNumberRecognition);
        _logger.debug("queuedForNumberRecognition for albumId ["+album.getGenericLevelId()+"] = " + queuedForNumberRecognition.get(album));
        return queuedForNumberRecognition.isEmpty()?0:queuedForNumberRecognition.get(album);
    }

    public int getImportErrors(Album album) {
        _logger.debug("album = " + album);
        _logger.debug("importErrors = " + importErrors);
        return importErrors.isEmpty()?0:importErrors.get(album);
    }

    /**
     * Called when an image recognition has been completed with a photo for the album passed as parameter
     * @param album album of the photo that has been processed
     */
    public void photoRecognitionProcessed(Album album) {
        // strange - how can we get null here for queue?
        // if message for number recognition is processed w/o the counter being increment first ??
        Integer current = queuedForNumberRecognition.get(album);
        _logger.debug("photoRecognitionProcessed called for album : " + album);
        _logger.debug("before decreasing photoRecognitionProcessed : current value :" + current);
        queuedForNumberRecognition.put(album, current==null?0:current -1); // in case the value in the map return null, put '0' as value - but how should that happen?
        if (queuedForNumberRecognition.get(album)==0) {
            queuedForNumberRecognition.remove(album);
        }
    }

    public void incPostProcessingCounter(Album album) {
        // todo: implement
    }


}
