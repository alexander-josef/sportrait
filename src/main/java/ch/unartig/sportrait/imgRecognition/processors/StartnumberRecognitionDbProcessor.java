package ch.unartig.sportrait.imgRecognition.processors;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.sportrait.imgRecognition.ImgRecognitionHelper;
import ch.unartig.sportrait.imgRecognition.RunnerFace;
import ch.unartig.sportrait.imgRecognition.Startnumber;
import ch.unartig.sportrait.imgRecognition.StartnumberProcessor;
import ch.unartig.studioserver.model.Photo;
import ch.unartig.studioserver.model.PhotoSubject;
import ch.unartig.studioserver.persistence.DAOs.PhotoDAO;
import ch.unartig.studioserver.persistence.DAOs.PhotoSubjectDAO;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import org.apache.bcel.generic.NEW;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Processor that stores the mapped startnumbers to the DB
 */
public class StartnumberRecognitionDbProcessor implements SportraitImageProcessorIF {
    private Logger _logger = Logger.getLogger(getClass().getName());


    List<Startnumber> startnumbers = new ArrayList<>(); // reference to numbers stored in startnumberprocessor ?
    private List<RunnerFace> facesWithoutNumbers = new ArrayList<>(); // list of detected faces from runners without a match, detected startnumber
    private final AmazonRekognition rekognitionClient;

    public StartnumberRecognitionDbProcessor() {
        rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
        // faceCollectionId according to album or eventcategory
    }

    @Override
    public void process(List<TextDetection> labels, List<FaceRecord> photoFaceRecords, String path) {
        _logger.warn("Method not implemented");
    }

    /**
     * Process text detections per file here
     * todo describe here strategy to get numbers
     * @param textDetections list of textDetections
     * @param photoFaceRecords list of recognized faces on this file / photo
     * @param path complete path for this file / photo
     * @param collectionId - used also as a collection ID for faces
     * @param photoId
     */
    @Override
    public void process(List<TextDetection> textDetections, List<FaceRecord> photoFaceRecords, String path, String collectionId, String photoId) {

        List<Startnumber> startnumbersForFile = getStartnumbers(textDetections, path);

        List<RunnerFace> runnerFaces = mapFacesToStartnumbers(photoFaceRecords, path, startnumbersForFile, collectionId);
        facesWithoutNumbers.addAll(runnerFaces);

        persistStartnumbers(startnumbersForFile, path, photoId);

        _logger.debug("*************************************");
        _logger.debug("*********** Done for File ***********");
        _logger.debug("*************************************");


        startnumbers.addAll(startnumbersForFile);

    }

    private void persistStartnumbers(List<Startnumber> startnumbersForFile, String path, String photoId) {
        PhotoSubjectDAO photoSubjectDAO = new PhotoSubjectDAO();
        PhotoDAO photoDAO = new PhotoDAO();

        try {
            HibernateUtil.beginTransaction();
            Photo photo = photoDAO.load(new Long(photoId));
            for (Startnumber startnumber : startnumbersForFile) {
                PhotoSubject ps = photoSubjectDAO.findOrCreateSubjectByStartNumber(startnumber.getStartnumberText(),photo.getAlbum());
                photoDAO.initializePhotoSubjects(photo);
                photo.addPhotoSubject(ps);
                _logger.debug("                  added photoSubject : " + ps.getPhotoSubjectId().toString());
            }
            HibernateUtil.commitTransaction();
            _logger.debug("                  committed !");
        } catch (UAPersistenceException e) {
            _logger.warn("Cannot persist Startnumber, see stacktrace",e);
        } catch (NumberFormatException e) {
            _logger.warn("Number format exception for Photo ID - cannot persist Startnumber",e);
        }


    }

    /**
     * For every image, map the detected and indexed faces to a startnumber
     * @param photoFaceRecords
     * @param path
     * @param startnumbersForFile
     * @param collectionId
     */
    public List<RunnerFace> mapFacesToStartnumbers(List<FaceRecord> photoFaceRecords, String path, List<Startnumber> startnumbersForFile, String collectionId) {
        _logger.debug("*************************************");
        _logger.debug("Mapping added/indexed faces to startnumbers");
        List<RunnerFace> unknownFaces = new ArrayList<>(); // return value - local list to collect unknown faces

        // process face records detected for the file
        for (FaceRecord faceRecord : photoFaceRecords) {
            boolean matchingNumber=false;

            _logger.debug("** Processing FaceID " + faceRecord.getFace().getFaceId() );
            // for each number detected in the file:
            for (Startnumber startnumber : startnumbersForFile) {
                _logger.debug("  startnumber = " + startnumber);
                BoundingBox faceBoundingBox = faceRecord.getFaceDetail().getBoundingBox();
                float faceBoundingBoxRightPosition = faceBoundingBox.getLeft() + faceBoundingBox.getWidth();
                _logger.debug("     startnumber middle position = " + startnumber.getMiddlePosition());
                _logger.debug("     Face left position = " + faceBoundingBox.getLeft());
                _logger.debug("     Face right position = " + faceBoundingBoxRightPosition);

                if ((startnumber.getMiddlePosition() > faceBoundingBox.getLeft()) && (startnumber.getMiddlePosition() < faceBoundingBoxRightPosition)) {
                    _logger.debug("******* Found a match for " + startnumber.getStartnumberText() + " - faceID : " + faceRecord.getFace().getFaceId());
                    matchingNumber = true;
                    startnumber.setFace(faceRecord);

                    // todo : then can this done most efficiently ? needs other records
                    _logger.warn("*****************************************************");
                    _logger.warn("******** Skipping mapBetterNumbersForMatching Faces() call - todo later  ******");
                    _logger.warn("*****************************************************");
                    // mapBetterNumbersForMatchingFaces(startnumber, collectionId, startnumbers);


                    // todo : no need to continue here - improve. return after match is true
                }


            }
            if (!matchingNumber) { // face w/o matching number -> add to list
                _logger.debug("*** no number Match found for face " + faceRecord.getFace().getFaceId() + " - returning false");
                unknownFaces.add(new RunnerFace(faceRecord,path));
                _logger.debug("*** added to list of faces without numbers for later processing");
            }
        }
        return unknownFaces;
    }

    /**
     * For a detected startnumber, check the faces collection for a face that matches and, if there's a better (more digits) number, replace the number
     * @param detectedStartnumber startnumber object, containing the text of the detected startnumber so far
     * @param collectionId collection ID string for faces collection
     * @param startnumbers list of all startnumber objects that should be compared
     */
    private void mapBetterNumbersForMatchingFaces(Startnumber detectedStartnumber, String collectionId, List<Startnumber> startnumbers) {
        List<FaceMatch> faceImageMatches = ImgRecognitionHelper.searchMatchingFaces(collectionId, rekognitionClient, detectedStartnumber.getFace());
        for (FaceMatch matchingFace: faceImageMatches) { // check for a startnumber instance that contains the matching faceID and has a valid startnumber
            // put in different method, extract number and return with 1st match
            _logger.debug("     matching face = " + matchingFace.getFace().getFaceId() + " -- in image : "+matchingFace.getFace().getExternalImageId()); // we do have the link to the path of the image!!!

            Stream<Startnumber> stream = startnumbers.stream()
                    .filter(existingStartnumber
                            -> ((existingStartnumber.getFaceId().equals(matchingFace.getFace().getFaceId())) && !existingStartnumber.getStartnumberText().isEmpty()));

            Optional<Startnumber> firstNumber = stream.findFirst(); // todo : not only first, but all and loop??

            if (firstNumber.isPresent()) {
                _logger.debug("           Found first match in startnumber = " + firstNumber);
                String existing = firstNumber.map(Startnumber::getStartnumberText).get();
                if (existing.length() > detectedStartnumber.getStartnumberText().length()) {
                    // detected number most probably cut - existing number has more digits -> replace the detected number with the old one
                    _logger.debug("           ***** Existing better ! replacing detected startnumber ["+detectedStartnumber.getStartnumberText()+"] with better, existing one :  " + existing);
                    detectedStartnumber.setStartnumberText(existing);
                } else if (detectedStartnumber.getStartnumberText().length() > existing.length()) {
                    // detected number > than an existing one found -> detected one is better, replace existing with new detected one
                    _logger.debug("           ***** Detected better ! replacing existing startnumber [" + existing + "] with better, newly detected one :  " + detectedStartnumber.getStartnumberText());
                    firstNumber.ifPresent(startnumber -> startnumber.setStartnumberText(detectedStartnumber.getStartnumberText()));
                } else {
                    _logger.debug("           matching number not better - continuing");
                }
                return;
            }
            _logger.debug("   --- no startnumber found // checking next matching face");
            // continue with next matching face
        }
        // no match
        _logger.debug("No match");
    }

    /**
     * Produce a list of Startnumber objects that contain the startnumber text according a ruleset defined in this method
     * @param textDetections
     * @param path
     * @return list of Startnumber objects with startnumber text set
     */
    public List<Startnumber> getStartnumbers(List<TextDetection> textDetections, String path) {
        List<Startnumber> startnumbersForFile = new ArrayList<>(); // startnumbers-file mapping for this file

        TextDetection lastLine = null;
        _logger.debug("*************************************");
        _logger.debug("Detected lines and words for " + path);
        _logger.debug("*************************************");
        // process all text detections and add found startnumbers to a local collection
        for (TextDetection text : textDetections) {

            // check for startnumber: (is a line, confidence > 80, parentID = null, previous LINE ID starts with SOLA AND/OR todo: next line ID similar ASVZ)
            // check for LINE with 1 to 3 digits (very simple - is that enough?)
            BoundingBox boundingBox = text.getGeometry().getBoundingBox();
            if (
                    text.getType().equals("LINE")
                            && text.getConfidence() > 80
                            && text.getParentId() == null
//                    && (lastLine != null && (lastLine.getDetectedText().startsWith("SOL") || lastLine.getDetectedText().startsWith("S0L") ))
                            && text.getDetectedText().matches("\\d{1,3}") // regex : matches if there's 1 2, or 3 digits
                            // improve search and exclude bounding boxes that start or end outside the photo (left < 0 || left + width > 1)
                            && (boundingBox.getLeft() > 0 && (boundingBox.getLeft() + boundingBox.getWidth()) < 1)
            )

            { // got a startnumber:
                Startnumber startnumber = new Startnumber(text, path);
                startnumbersForFile.add(startnumber);
                lastLine = text; // not needed with regex for 1 to 3 digits
            } else if (text.getType().equals("LINE")) { // not needed with regex
                lastLine = text;
            }


            _logger.debug("Detected: " + text.getDetectedText());
            _logger.debug("Confidence: " + text.getConfidence().toString());
            _logger.debug("Id : " + text.getId());
            _logger.debug("Parent Id: " + text.getParentId());
            _logger.debug("Type: " + text.getType());
            _logger.debug("Geometry: " + boundingBox);
            _logger.debug("");
        }
        return startnumbersForFile;
    }


}
