package ch.unartig.sportrait.imgRecognition.processors;

import ch.unartig.sportrait.imgRecognition.ImgRecognitionHelper;
import ch.unartig.sportrait.imgRecognition.RunnerFace;
import ch.unartig.sportrait.imgRecognition.Startnumber;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class StartnumberProcessor implements SportraitImageProcessorIF {


    List<Startnumber> startnumbers;
    private List<RunnerFace> facesWithoutNumbers; // list of detected faces from runners without a matche, detected startnumber
    private final AmazonRekognition rekognitionClient;
    private final String faceCollectionId;

    public StartnumberProcessor(List<Startnumber> sn, List<RunnerFace> facesWithoutNumbers, AmazonRekognition rekognitionClient, String faceCollectionId) {
        startnumbers = sn;
        this.facesWithoutNumbers = facesWithoutNumbers;
        this.rekognitionClient = rekognitionClient;
        this.faceCollectionId = faceCollectionId;
    }

    /**
     * Process text detections per file here
     * todo describe here strategy to get numbers
     * @param textDetections list of textDetections
     * @param photoFaceRecords list of recognized faces on this file / photo
     * @param path complete path for this file / photo
     */
    @Override
    public void process(List<TextDetection> textDetections, List<FaceRecord> photoFaceRecords, String path) {

        List<Startnumber> startnumbersForFile = getStartnumbers(textDetections, path);

        mapFacesToStartnumbers(photoFaceRecords, path, startnumbersForFile);

        System.out.println("*************************************");
        System.out.println("*********** Done for File ***********");
        System.out.println("*************************************");

        startnumbers.addAll(startnumbersForFile);

    }

    /**
     * for every image, map the detected and indexed faces to a startnumber
     * @param photoFaceRecords
     * @param path
     * @param startnumbersForFile
     */
    public void mapFacesToStartnumbers(List<FaceRecord> photoFaceRecords, String path, List<Startnumber> startnumbersForFile) {
        System.out.println("*************************************");
        System.out.println("Mapping added/indexed faces to startnumbers");

        // process face records detected for the file
        for (FaceRecord faceRecord : photoFaceRecords) {
            boolean matchingNumber=false;

            System.out.println("** Processing FaceID " + faceRecord.getFace().getFaceId() );
            // for each number detected in the file:
            for (int i = 0; i < startnumbersForFile.size(); i++) {
                Startnumber startnumber = startnumbersForFile.get(i);
                System.out.println("  startnumber = " + startnumber);
                BoundingBox faceBoundingBox = faceRecord.getFaceDetail().getBoundingBox();
                float faceBoundingBoxRightPosition = faceBoundingBox.getLeft() + faceBoundingBox.getWidth();
                System.out.println("     startnumber middle position = " + startnumber.getMiddlePosition());
                System.out.println("     Face left position = " + faceBoundingBox.getLeft());
                System.out.println("     Face right position = " + faceBoundingBoxRightPosition);
                if ((startnumber.getMiddlePosition() > faceBoundingBox.getLeft()) && (startnumber.getMiddlePosition() < faceBoundingBoxRightPosition)) {
                    matchingNumber=true;
                    startnumber.setFace(faceRecord);
                    System.out.println("******* Found a match for "+ startnumber.getStartnumberText()+ " - faceID : " + faceRecord.getFace().getFaceId());
                    // todo : no need to continue here - improve. return after match is true
                    // todo : check for better number with matching face from different images
                    mapBetterNumbersForMatchingFaces(startnumber,faceRecord);
                }



            }
            if (!matchingNumber) { // face w/o matching number -> add to list
                System.out.println("*** no number Match found for face " + faceRecord.getFace().getFaceId() + " - returning false");
                facesWithoutNumbers.add(new RunnerFace(faceRecord,path));
                System.out.println("*** added to list of faces without numbers for later processing");

                // this list need to be processed later to find matches from other fotos
                // todo : remove this face from the collection?
            }

        }
    }

    /**
     * After binding a recognized face to a detected startnumber, check the collection for a face that matches and, if there's a better (more digits) number, replace the number
     * @param detectedStartnumber startnumber object, containing the text of the detected startnumber so far
     * @param faceRecord // todo : can we assign a unique faceId ?
     */
    private void mapBetterNumbersForMatchingFaces(Startnumber detectedStartnumber, FaceRecord faceRecord) {
        List<FaceMatch> faceImageMatches = ImgRecognitionHelper.searchMatchingFaces(faceCollectionId, rekognitionClient, faceRecord);


        for (FaceMatch matchingFace: faceImageMatches) { // check for a startnumber instance that contains the matching faceID and has a valid startnumber
            // put in different method, extract number and return with 1st match
            System.out.println("     matching face = " + matchingFace.getFace().getFaceId() + " -- in image : "+matchingFace.getFace().getExternalImageId()); // we do have the link to the path of the image!!!

            // try a bit more fancy: (also check for startnumber.getStartnumberText not empty)
            Stream<Startnumber> stream = startnumbers.stream()
                    .filter(existingStartnumber
                            -> ((existingStartnumber.getFaceId().equals(matchingFace.getFace().getFaceId())) && !existingStartnumber.getStartnumberText().isEmpty()));

            Optional<Startnumber> firstNumber = stream.findFirst(); // todo : not only first, but all and loop??

            if (firstNumber.isPresent()) {
                System.out.println("           Found first match in startnumber = " + firstNumber);
                String existing = firstNumber.map(Startnumber::getStartnumberText).get();
                if (existing.length() > detectedStartnumber.getStartnumberText().length()) {
                    // detected number most probably cut - existing number has more digits -> replace the detected number with the old one
                    System.out.println("           ***** Existing better ! replacing detected startnumber ["+detectedStartnumber.getStartnumberText()+"] with better, existing one :  " + existing);
                    detectedStartnumber.setStartnumberText(existing);
                } else if (detectedStartnumber.getStartnumberText().length() > existing.length()) {
                    // detected number > than an existing one found -> detected one is better, replace existing with new detected one
                    System.out.println("           ***** Detected better ! replacing existing startnumber [" + existing + "] with better, newly detected one :  " + detectedStartnumber.getStartnumberText());
                    firstNumber.ifPresent(startnumber -> startnumber.setStartnumberText(detectedStartnumber.getStartnumberText()));
                } else {
                    System.out.println("           matching number not better - continuing");
                }
                return;
            }
            System.out.println("   --- no startnumber found // checking next matching face");
            // continue with next matching face
        }
        // no match
        System.out.println("No match");
    }

    /**
     * Produce a list of Startnumber objects that contain the startnumber text according a ruleset defined in this method
     * @param textDetections
     * @param path
     * @return list of Startnumber objects with startnumber text set
     */
    public List<Startnumber> getStartnumbers(List<TextDetection> textDetections, String path) {
        List<Startnumber> startnumbersForFile = new ArrayList<>(); // startnumbers-file mapping for this file

        TextDetection lastLine = null;
        System.out.println("*************************************");
        System.out.println("Detected lines and words for " + path);
        System.out.println("*************************************");
        // process all text detections and add found startnumbers to a local collection
        for (TextDetection text : textDetections) {

            // check for startnumber: (is a line, confidence > 80, parentID = null, previous LINE ID starts with SOLA AND/OR todo: next line ID similar ASVZ)
            // check for LINE with 1 to 3 digits (very simple - is that enough?)
            BoundingBox boundingBox = text.getGeometry().getBoundingBox();
            if (
                    text.getType().equals("LINE")
                            && text.getConfidence() > 80
                            && text.getParentId() == null
//                    && (lastLine != null && (lastLine.getDetectedText().startsWith("SOL") || lastLine.getDetectedText().startsWith("S0L") ))
                            && text.getDetectedText().matches("\\d{1,3}") // regex : matches if there's 1 2, or 3 digits
                            // improve search and exclude bounding boxes that start or end outside the photo (left < 0 || left + width > 1)
                            && (boundingBox.getLeft() > 0 && (boundingBox.getLeft() + boundingBox.getWidth()) < 1)
            )

            { // got a startnumber:
                Startnumber startnumber = new Startnumber(text, path);
                startnumbersForFile.add(startnumber);
                lastLine = text; // not needed with regex for 1 to 3 digits
            } else if (text.getType().equals("LINE")) { // not needed with regex
                lastLine = text;
            }


            System.out.println("Detected: " + text.getDetectedText());
            System.out.println("Confidence: " + text.getConfidence().toString());
            System.out.println("Id : " + text.getId());
            System.out.println("Parent Id: " + text.getParentId());
            System.out.println("Type: " + text.getType());
            System.out.println("Geometry: " + boundingBox);
            System.out.println();
        }
        return startnumbersForFile;
    }


}
