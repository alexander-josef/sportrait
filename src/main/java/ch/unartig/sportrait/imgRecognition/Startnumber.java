package ch.unartig.sportrait.imgRecognition;

import com.amazonaws.services.rekognition.model.Face;
import com.amazonaws.services.rekognition.model.FaceRecord;
import com.amazonaws.services.rekognition.model.TextDetection;
import org.apache.log4j.Logger;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.unartig.sportrait.imgRecognition.processors.StartnumberRecognitionDbProcessor.REG_EXP_STARTNUMBER_RECOGNITION;

public class Startnumber {
    private Logger _logger = Logger.getLogger(getClass().getName());
    private final Float leftPosition;
    private final Float width;
    private final Float middlePosition;



    private String startnumberText; // the real startnumber !
    private final String filePath;
    private FaceRecord face; // todo : maybe just faceId ?

    /**
     * Constructor used to create a startnumber instance from a detected startnumber
     * @param textDetection
     * @param path
     */
    public Startnumber(TextDetection textDetection, String path) {
        // now split according to the group in the regexp:
        Pattern p = Pattern.compile(REG_EXP_STARTNUMBER_RECOGNITION);
        Matcher m = p.matcher(textDetection.getDetectedText());
        if (m.matches()) {
            _logger.debug("Entire text recognized [group(0)] : " + Optional.of(m.group(0)).orElse("n/a") + "for :" + path); // must not be null
            _logger.debug("Optional text before number [group(1)] : " + Optional.ofNullable(m.group(1)).orElse("n/a")); // can be null
            _logger.debug("** Startnumber text recognized [group(2)] : " + Optional.of(m.group(2)).orElse("n/a")); // must not be null
            _logger.debug("Optional text after number [group(3)] : " + Optional.ofNullable(m.group(3)).orElse("n/a")); // can be null
            startnumberText = m.group(2); // 0 is the entire string, 1 the character - divided by space - before the number

        } else {
            _logger.warn("regexp pattern matcher didn't match for :" + textDetection.getDetectedText());
        }
        leftPosition = textDetection.getGeometry().getBoundingBox().getLeft();
        width = textDetection.getGeometry().getBoundingBox().getWidth();
        middlePosition = leftPosition + (width / 2);
        filePath = path;
    }

    /**
     * Use this constructor if no startnumber was detected, but a match with a face identified the runner with startnumber on file
     * @param startnumber
     * @param filePath
     */
    public Startnumber(String startnumber, String filePath) {
        this.filePath = filePath;
        this.startnumberText = startnumber;
        this.leftPosition = null; // not available
        this.middlePosition = null; // not available
        this.width = null; // not available
    }


    public Float getLeftPosition() {
        return leftPosition;
    }

    public Float getWidth() {
        return width;
    }

    public Float getMiddlePosition() {
        return middlePosition;
    }

    public String getStartnumberText() {
        return startnumberText;
    }

    public void setStartnumberText(String startnumberText) {
        this.startnumberText = startnumberText;
    }

    public FaceRecord getFace() {
        return face;
    }

    public void setFace(FaceRecord face) {
        this.face = face;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public String toString() {
        return "Startnumber{" +
                "startnumberText=" + startnumberText +
                ", filePath='" + filePath + '\'' +
                ", faceID=" + Optional.ofNullable(face).map(FaceRecord::getFace).map(Face::getFaceId).orElse("not set") +
                '}';
    }

    /**
     * Helper method to avoid null pointer problems when face not set
     * (see solution with Optional in toString() )
     * @return
     */
    public String getFaceId() {
        if (face != null) {
            return face.getFace().getFaceId();
        } else {
            return "not set";
        }
    }
}
