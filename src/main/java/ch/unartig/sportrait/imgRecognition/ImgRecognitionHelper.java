package ch.unartig.sportrait.imgRecognition;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.FaceRecord;
import com.amazonaws.services.rekognition.model.SearchFacesRequest;
import com.amazonaws.services.rekognition.model.SearchFacesResult;

import java.util.List;

public class ImgRecognitionHelper {
    public static List<FaceMatch>  searchMatchingFaces(String faceCollectionId, AmazonRekognition rekognitionClient, FaceRecord faceRecord) {
        // search face record in collection
        SearchFacesRequest searchFacesRequest = new SearchFacesRequest()
                .withCollectionId(faceCollectionId)
                .withFaceId(faceRecord.getFace().getFaceId())
                .withFaceMatchThreshold(70F)
                .withMaxFaces(2);

        List<FaceMatch> retVal = rekognitionClient.searchFaces(searchFacesRequest).getFaceMatches();
        System.out.println("Face(s) in collection matching faceId [" + faceRecord.getFace().getFaceId()+"] found - number of matches : "+ retVal.size());
        return retVal;
    }
}
