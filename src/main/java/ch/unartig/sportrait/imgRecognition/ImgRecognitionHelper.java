package ch.unartig.sportrait.imgRecognition;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.*;
import org.apache.log4j.Logger;

import java.util.List;

public class ImgRecognitionHelper {
    public static final Float MIN_FACES_SHARPNESS =  10F; // see Apple notes for comparison of OK and not OK images of faces
    public static final Float MIN_FACES_CONFIDENCE = 99.9F;
    public static final Float MIN_FACES_BRIGHTNESS = 80F;
    public static final int MAX_FACES_RETURNED_FROM_SEARCH = 3;
    public static final float FACE_MATCH_THRESHOLD_FOR_SEARCH = 95F;
    public static final String FACE_COLLECTION_ID = "sportraitFaces2019";
    private static Logger _logger = Logger.getLogger("ImgRecognitionHelper");

    public static List<FaceMatch>  searchMatchingFaces(String faceCollectionId, AmazonRekognition rekognitionClient, String faceId) {
        // search face record in collection
        SearchFacesRequest searchFacesRequest = new SearchFacesRequest()
                .withCollectionId(faceCollectionId)
                .withFaceId(faceId)
                .withFaceMatchThreshold(FACE_MATCH_THRESHOLD_FOR_SEARCH) // defaulted to 70F
                .withMaxFaces(MAX_FACES_RETURNED_FROM_SEARCH); // used to be 2

        List<FaceMatch> retVal = rekognitionClient.searchFaces(searchFacesRequest).getFaceMatches();
        _logger.debug("Face(s) in collection matching faceId [" + faceId+"] found - number of matches : "+ retVal.size());
        return retVal;
    }

    /**
     * @param rekognitionClient
     * @param bucket
     * @param key
     * @return
     */
    static List<TextDetection> getTextDetectionsFor(AmazonRekognition rekognitionClient, String bucket, String key) {
        // text detection:
        DetectTextRequest textRequest = new DetectTextRequest()
                .withImage(new Image()
                        .withS3Object(new S3Object()
                                .withName(key)
                                .withBucket(bucket)));


        // create list of number/filename and print it out a the end

        DetectTextResult textResult;
        try {
            textResult = rekognitionClient.detectText(textRequest);
            return textResult.getTextDetections();
        } catch (InvalidS3ObjectException e) {
            _logger.warn("S3 Object does not exist or ist invalid - see stack trace",e);
            e.printStackTrace();
            return null;
        }
    }


    static void createFacesCollection(AmazonRekognition rekognitionClient) {
        _logger.info("Creating collection: " + ImgRecognitionHelper.FACE_COLLECTION_ID);

        CreateCollectionRequest request = new CreateCollectionRequest()
                .withCollectionId(ImgRecognitionHelper.FACE_COLLECTION_ID);

        CreateCollectionResult createCollectionResult = null;
        try {
            createCollectionResult = rekognitionClient.createCollection(request);
            _logger.info("CollectionArn : " +
                    createCollectionResult.getCollectionArn());
            _logger.info("Status code : " +
                    createCollectionResult.getStatusCode().toString());
        } catch (ResourceAlreadyExistsException e) {
            _logger.info("Ignoring - Collection already existed");
        }
    }


    /**
     * Remove a list of faces from the collection - faces have been identified to be of low quality
     * @param faceIds the FaceIds for the faces to be removed from the collection
     * @param rekognitionClient
     */
    public static void removeFromCollection(List<String> faceIds, AmazonRekognition rekognitionClient) {
        if (!faceIds.isEmpty()) {
            DeleteFacesRequest deleteFacesRequest = new DeleteFacesRequest().
                    withCollectionId(FACE_COLLECTION_ID).
                    withFaceIds(faceIds);
            DeleteFacesResult result = rekognitionClient.deleteFaces(deleteFacesRequest);
            _logger.debug("   Face(s) deleted from collection  : ");
            for (int i = 0; i < result.getDeletedFaces().size(); i++) {
                String deletedFaceId = result.getDeletedFaces().get(i);
                _logger.debug("FaceId :" + deletedFaceId);
            }
        } else {
            _logger.debug("no faces to remove from collection - ignoring");
        }

    }
}
