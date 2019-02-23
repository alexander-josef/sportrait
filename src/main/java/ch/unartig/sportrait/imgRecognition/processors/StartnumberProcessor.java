package ch.unartig.sportrait.imgRecognition.processors;

import ch.unartig.sportrait.imgRecognition.Startnumber;
import com.amazonaws.services.rekognition.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class StartnumberProcessor implements SportraitImageProcessorIF {


    List<Startnumber> startnumbers;
    private List<FaceRecord> facesWithoutNumbers;

    public StartnumberProcessor(List<Startnumber> sn, List<FaceRecord> facesWithoutNumbers) {

        startnumbers = sn;
        this.facesWithoutNumbers = facesWithoutNumbers;
    }

    /**
     * Process text detections per file here
     * todo describe here strategy to get numbers
     * @param textDetections
     * @param photoFaceRecords
     * @param path
     */
    @Override
    public void process(List<TextDetection> textDetections, List<FaceRecord> photoFaceRecords, String path) {

        List<Startnumber> startnumbersForFile = new ArrayList<>(); // startnumbers-file mapping for this file

        TextDetection lastLine = null;
        System.out.println("*************************************");
        System.out.println("Detected lines and words for " + path);
        System.out.println("*************************************");
        // process all text detections and add found startnumbers to a local collection
        for (TextDetection text : textDetections) {

            // check for startnumber: (is a line, confidence > 80, parentID = null, previous LINE ID starts with SOLA AND/OR todo: next line ID similar ASVZ)
            // check for LINE with 1 to 3 digits (very simple - is that enough?)
            if (
                    text.getType().equals("LINE")
                            && text.getConfidence() > 80
                            && text.getParentId() == null
//                    && (lastLine != null && (lastLine.getDetectedText().startsWith("SOL") || lastLine.getDetectedText().startsWith("S0L") ))
                            && text.getDetectedText().matches("\\d{1,3}") // regex : matches if there's 1 2, or 3 digits
            )

            { // got a startnumber:

                Startnumber startnumber = new Startnumber(text, path); // todo: file mapping ?

                startnumbersForFile.add(startnumber);
                lastLine = text; // not needed with regex for 1 to 3 digits
                // todo: get matching face - maybe add geometry object to startNumbersFile entries and process later


            } else if (text.getType().equals("LINE")) { // not needed with regex
                lastLine = text;
            }


            System.out.println("Detected: " + text.getDetectedText());
            System.out.println("Confidence: " + text.getConfidence().toString());
            System.out.println("Id : " + text.getId());
            System.out.println("Parent Id: " + text.getParentId());
            System.out.println("Type: " + text.getType());
            System.out.println("Geometry: " + text.getGeometry().getBoundingBox());
            System.out.println();
        }

        System.out.println("*************************************");
        System.out.println("Adding faces");

        // process face records detected for the file
        for (FaceRecord faceRecord : photoFaceRecords) {
            boolean matchingNumber=false;

            System.out.println("** Processing FaceID " + faceRecord.getFace().getFaceId() );
            // for each number detected in the file:
            for (int i = 0; i < startnumbersForFile.size(); i++) {
                Startnumber startnumber = startnumbersForFile.get(i);
                System.out.println("startnumber = " + startnumber);
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
                }
            }
            if (!matchingNumber) {
                System.out.println("*** no number Match found for face " + faceRecord.getFace().getFaceId() + " - returning false");
                facesWithoutNumbers.add(faceRecord);
                // todo : add to list of faces without matching number;
                // this list need to be processed later to find matches from other fotos
            }

        }
        System.out.println("*************************************");
        System.out.println("*********** Done for File ***********");
        System.out.println("*************************************");

        startnumbers.addAll(startnumbersForFile);

    }


}
