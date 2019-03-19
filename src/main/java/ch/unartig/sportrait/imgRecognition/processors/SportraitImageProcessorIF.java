package ch.unartig.sportrait.imgRecognition.processors;

import com.amazonaws.services.rekognition.model.FaceRecord;
import com.amazonaws.services.rekognition.model.TextDetection;

import java.util.List;

public interface SportraitImageProcessorIF {

    /** Used for TEST
     *
     * @param labels
     * @param photoFaceRecords
     * @param path
     */
    void process(List<TextDetection> labels, List<FaceRecord> photoFaceRecords, String path);

    void process(List<TextDetection> photoTextDetections, List<FaceRecord> photoFaceRecords, String photoPath, String eventCategoryId, String photoId);
}
