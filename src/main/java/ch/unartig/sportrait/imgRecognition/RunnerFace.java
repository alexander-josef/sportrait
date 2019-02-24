package ch.unartig.sportrait.imgRecognition;

import com.amazonaws.services.rekognition.model.FaceRecord;

public class RunnerFace {
    private final FaceRecord faceRecord;
    private final String path;

    public RunnerFace(FaceRecord faceRecord, String path) {

        this.faceRecord = faceRecord;
        this.path = path;
    }
}
