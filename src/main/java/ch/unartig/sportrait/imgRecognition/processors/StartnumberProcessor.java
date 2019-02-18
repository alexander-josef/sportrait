package ch.unartig.sportrait.imgRecognition.processors;

import com.amazonaws.services.rekognition.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StartnumberProcessor implements SportraitImageProcessorIF {

    private List<String[]> startNumbers;


    public StartnumberProcessor() {

        startNumbers = new ArrayList<>();
    }

    @Override
    public void process(List<TextDetection> textDetections, String path) {

        // todo : return startnumbers here ...

        TextDetection lastLine = null;
        System.out.println("Detected lines and words for " + path);
        for (TextDetection text : textDetections) {

            // check for startnumber: (is a line, confidence > 95, parentID = null, previous LINE ID starts with SOLA)
            if (text.getType().equals("LINE")
                    && text.getConfidence() > 95
                    && text.getParentId() == null
                    && (lastLine != null && (lastLine.getDetectedText().startsWith("SOL") || lastLine.getDetectedText().startsWith("S0L") ))) {
                startNumbers.add(new String[]{text.getDetectedText(), path});
                lastLine = text;

            } else if (text.getType().equals("LINE")) {
                lastLine = text;
            }


            System.out.println("Detected: " + text.getDetectedText());
            System.out.println("Confidence: " + text.getConfidence().toString());
            System.out.println("Id : " + text.getId());
            System.out.println("Parent Id: " + text.getParentId());
            System.out.println("Type: " + text.getType());
            System.out.println();
        }


        for (int i = 0; i < startNumbers.size(); i++) {
            String[] strings = startNumbers.get(i);
            System.out.println("startnummer = " + Arrays.toString(strings));
        }
    }


}
