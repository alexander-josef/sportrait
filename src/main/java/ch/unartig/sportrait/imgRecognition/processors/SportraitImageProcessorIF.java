package ch.unartig.sportrait.imgRecognition.processors;

import com.amazonaws.services.rekognition.model.TextDetection;

import java.util.List;

public interface SportraitImageProcessorIF {
    public void process(List<TextDetection> labels, String path);
}
