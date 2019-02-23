package ch.unartig.sportrait.imgRecognition.processors;

import com.amazonaws.services.rekognition.model.FaceRecord;
import com.amazonaws.services.rekognition.model.TextDetection;

import java.util.List;

public interface SportraitImageProcessorIF {
    /**
     *
     * @param labels
     * @param photoFaceRecords
     * @param path
     */
    public void process(List<TextDetection> labels, List<FaceRecord> photoFaceRecords, String path);
}
