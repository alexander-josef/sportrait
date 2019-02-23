package ch.unartig.sportrait.imgRecognition;

import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.FaceRecord;
import com.amazonaws.services.rekognition.model.TextDetection;

import java.util.List;

public class Startnumber {
    private final Float leftPosition;
    private final Float width;
    private final Float middlePosition;
    private final String startnumberText; // the real startnumber !
    private final String filePath;
    private FaceRecord face; // todo : maybe just faceId ?

    public Startnumber(TextDetection textDetection, String path) {
        startnumberText = textDetection.getDetectedText();
        leftPosition = textDetection.getGeometry().getBoundingBox().getLeft();
        width = textDetection.getGeometry().getBoundingBox().getWidth();
        middlePosition = leftPosition+(width/2);
        filePath = path;
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
                ", faceID=" + face.getFace().getFaceId() +
                '}';
    }

    /**
     * Helper method to avoid null pointer problems when face not set
     * @return
     */
    public String getFaceId() {
        if (face != null) {
            return face.getFace().getFaceId();
        } else {
            return "";
        }
    }
}
