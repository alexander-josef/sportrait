package ch.unartig.sportrait.imgRecognition;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.*;

import java.util.List;

public class ImgRecognitionHelper {
    public static List<FaceMatch>  searchMatchingFaces(String faceCollectionId, AmazonRekognition rekognitionClient, FaceRecord faceRecord) {
        // search face record in collection
        SearchFacesRequest searchFacesRequest = new SearchFacesRequest()
                .withCollectionId(faceCollectionId)
                .withFaceId(faceRecord.getFace().getFaceId())
                .withFaceMatchThreshold(95F) // defaulted to 70F
                .withMaxFaces(3); // used to be 2

        List<FaceMatch> retVal = rekognitionClient.searchFaces(searchFacesRequest).getFaceMatches();
        System.out.println("Face(s) in collection matching faceId [" + faceRecord.getFace().getFaceId()+"] found - number of matches : "+ retVal.size());
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

        DetectTextResult textResult = rekognitionClient.detectText(textRequest);
        return textResult.getTextDetections();
    }
}
