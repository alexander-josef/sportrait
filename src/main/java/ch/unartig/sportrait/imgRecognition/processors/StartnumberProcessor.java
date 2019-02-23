package ch.unartig.sportrait.imgRecognition.processors;

import com.amazonaws.services.rekognition.model.*;

import java.util.List;

public class StartnumberProcessor implements SportraitImageProcessorIF {

    private List<String[]> startNumbersFile; // startnumber-file mapping


    public StartnumberProcessor(List<String[]> sn) {

        startNumbersFile = sn;
    }

    /**
     * todo describe here strategy to get numbers
     * @param textDetections
     * @param path
     */
    @Override
    public void process(List<TextDetection> textDetections, String path) {

        // todo : return startnumbers here ...

        TextDetection lastLine = null;
        System.out.println("Detected lines and words for " + path);
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

                startNumbersFile.add(new String[]{text.getDetectedText(), path}); // todo : add geometry
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


    }


}
