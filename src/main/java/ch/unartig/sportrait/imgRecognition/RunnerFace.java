package ch.unartig.sportrait.imgRecognition;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.FaceRecord;
import com.amazonaws.services.rekognition.model.SearchFacesRequest;
import com.amazonaws.services.rekognition.model.SearchFacesResult;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class RunnerFace {
    private final FaceRecord faceRecord;
    private final String faceId;
    private final String path;

    public RunnerFace(FaceRecord faceRecord, String path) {
        this.faceRecord = faceRecord; // maybe replace by faceId
        this.path = path;
        this.faceId = faceRecord.getFace().getFaceId();
    }

    public RunnerFace(String faceId, String path) {
        this.faceId = faceId;
        this.path = path;
        this.faceRecord=null;
    }

    /**
     *
     * @param faceCollectionId
     * @param rekognitionClient
     * @param startnumbers
     */
    void addStartnumberFromMatchingFacesInCollection(String faceCollectionId, AmazonRekognition rekognitionClient, List<Startnumber> startnumbers) {
        // todo :  improve accuracy by checking if there's already a startnumber match for this face -
        //     if yes :
        //            - replace startnumber if the old startnumber contains less digies (for example 2 instead of 3) - or if accurcy is different?

        // search face record in collection
        List<FaceMatch> faceImageMatches = ImgRecognitionHelper.searchMatchingFaces(faceCollectionId,rekognitionClient, faceId);

        String startnumber;
        startnumber = getFirstStartnumberFromMatchingFaces(faceImageMatches, startnumbers); // check why "no startnumber found" is also returned

        if (startnumber != null) {
            System.out.println("**************************************************************************************************");
            System.out.println("*********** Found startnumber for unmapped FaceID " + faceRecord.getFace().getFaceId() + " --> Startnumber : " + startnumber);
            System.out.println("**************************************************************************************************");
            // add startnumber and file from unknownFace to startnumbers list
            // todo return value?
            startnumbers.add(new Startnumber(startnumber,path));


        } else {
            System.out.println("      No Match found for face "+ faceRecord.getFace().getFaceId() +" in file " + path);
        }
        System.out.println("");

        // todo:         delete entry from list ? delete list at the end?

    }

    /**
     * a list of matching faces matches will be compared against mapped Runners (number / faceId) and the first matching number will be returned, or null
     * @param faceImageMatches
     * @param startnumbers
     * @return 1st matching number or null
     */
    private String getFirstStartnumberFromMatchingFaces(List<FaceMatch> faceImageMatches, List<Startnumber> startnumbers) {

        for (FaceMatch matchingFace: faceImageMatches) { // check for a startnumber instance that contains the matching faceID and has a valid startnumber
            // put in different method, extract number and return with 1st match
            System.out.println("     matching face = " + matchingFace.getFace().getFaceId() + " -- in image : "+matchingFace.getFace().getExternalImageId()); // we do have the link to the path of the image!!!

            // this should work, but we need only the 1st result
            // List <Startnumber> matchingNumbers = startnumbers.stream()
            //        .filter(startnumber -> startnumber.getFaceId().equals(matchingFace.getFace().getFaceId())).collect(Collectors.toList());

            // try a bit more fancy: (also check for startnumber.getStartnumberText not empty)
            Stream<Startnumber> stream = startnumbers.stream()
                    .filter(startnumber
                            -> ((startnumber.getFaceId().equals(matchingFace.getFace().getFaceId())) && !startnumber.getStartnumberText().isEmpty()));

            Optional<Startnumber> firstNumber = stream.findFirst();

            if (firstNumber.isPresent()) {
                System.out.println("           Found first match in startnumber = " + firstNumber);
                return firstNumber.map(Startnumber::getStartnumberText).orElseGet(this::getDefaultValue); // alternative value should never be supplied
            }
            System.out.println("   --- no startnumber found // checking next matching face");
            // continue with next matching face
        }
        // no match
        System.out.println("No match found, returning null");
        return null;
    }

    private String getDefaultValue() {
        return "**********";
    }

    public FaceRecord getFaceRecord() {
        return faceRecord;
    }

    public String getPath() {
        return path;
    }
}
