package ch.unartig.sportrait.imgRecognition;

import com.amazonaws.services.rekognition.model.Face;
import com.amazonaws.services.rekognition.model.FaceRecord;
import com.amazonaws.services.rekognition.model.TextDetection;

import java.util.Optional;

public class Startnumber {
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
        startnumberText = textDetection.getDetectedText();
        leftPosition = textDetection.getGeometry().getBoundingBox().getLeft();
        width = textDetection.getGeometry().getBoundingBox().getWidth();
        middlePosition = leftPosition+(width/2);
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
