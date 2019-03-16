package ch.unartig.sportrait.imgRecognition;

import ch.unartig.sportrait.imgRecognition.processors.SportraitImageProcessorIF;
import ch.unartig.studioserver.model.EventRunner;
import ch.unartig.studioserver.model.Photo;
import ch.unartig.studioserver.model.PhotoSubject;
import ch.unartig.studioserver.persistence.DAOs.PhotoDAO;
import ch.unartig.studioserver.persistence.DAOs.PhotoSubjectDAO;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.InvalidParameterException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class ImageRecognitionPostProcessor implements Runnable{
    public static final int MAX_IDLE = 5;
    private final int maxImagesToProcess;
    private final Long albumId;
    private Logger _logger = Logger.getLogger(getClass().getName());

    private static final int MAX_NUMBER_OF_MESSAGES = 10;
    private static final int WAIT_TIME_SECONDS = 20;
    private final ThreadPoolExecutor executor;
    private final AmazonSQS sqs;
    private AtomicInteger numSeenProcessor = new AtomicInteger();

    private List<SportraitImageProcessorIF> processors = new ArrayList<>();


    /**
     * Constructor for post processor
     * @param genericLevelId ID of queue to be post processed (-> album-Id of imported album)
     */
    public ImageRecognitionPostProcessor(Long genericLevelId) {
        this.albumId = genericLevelId;
        _logger.info("**** Starting up Startnumber Post Processor");
        maxImagesToProcess = -1;
        sqs = AmazonSQSClientBuilder.defaultClient();

        int maxWorkers = 1; // no max workers defined ? 1 ?
        executor = new ThreadPoolExecutor(
                1, maxWorkers, 30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(maxWorkers * 2, false),
                new ThreadPoolExecutor.CallerRunsPolicy() // prevents backing up too many jobs
        );

    }


    @Override
    public void run() {
        int idleCounter=0; // idle counter - if idle counter reaches MAX_IDLE, shutdown the polling server
        String queueUrl = MessageQueueHandler.getInstance().getUnknownFacesQueueName(albumId);

        while (!executor.isShutdown()) {
            // poll for messages on the queue.
            // how many messages will be fetched? max =10 - but actual?
            ReceiveMessageRequest poll = new ReceiveMessageRequest(queueUrl)
                    .withMaxNumberOfMessages(MAX_NUMBER_OF_MESSAGES)
                    .withWaitTimeSeconds(WAIT_TIME_SECONDS)
                    .withMessageAttributeNames("All");
            List<Message> messages = null;
            try {
                messages = sqs.receiveMessage(poll).getMessages();
            } catch (QueueDoesNotExistException e) {
                _logger.error("Queue does not exist");
/*
                CreateQueueResult queueResult = sqs.createQueue(MessageQueueHandler.getInstance().getSportraitQueueName());
                _logger.info("Created new queue with URL : " + queueResult.getQueueUrl());
                messages = sqs.receiveMessage(poll).getMessages();
*/
            }
            _logger.debug("Got " + messages.size() + " messages from queue. Processed " + numSeenProcessor + " so far. maxImagesToProcess = " + maxImagesToProcess);

            // process the messages in parallel.
            for (Message message : messages) {
                numSeenProcessor.incrementAndGet();
                executor.execute(() -> {
                    try {
                        processTask(message);
                    } catch (InvalidParameterException e) {
                        if (e.getMessage().contains("Minimum image height")) {
                            _logger.debug("Input image " + message.getBody() + " too small to analyze, skipping.");
                        } else {
                            _logger.warn(e);
                        }
                    }
                });

                // remove the job from the queue when completed successfully (or skipped)
                DeleteMessageResult result = sqs.deleteMessage(queueUrl, message.getReceiptHandle());
                _logger.debug("message processed - deleted message from queue with result : "+result.toString());
                idleCounter=0; // reset idle counter after successfully processing a message
            }

            if (messages.size() ==0) {
                _logger.debug("Increasing idle counter for post processing server [AlbumId = "+albumId+"]");
                idleCounter++; // increase idle counter
            }

            // Exit clauses
            if (maxImagesToProcess > -1 && numSeenProcessor.get() > maxImagesToProcess) {
                _logger.debug("Seen enough (" + numSeenProcessor.get() + "), quitting. maxImagesToProcess = " + maxImagesToProcess);
                shutdown();
            } else if (idleCounter > MAX_IDLE) {
                _logger.info("idle counter reached MAX_IDLE - shutting down");
                shutdown();
                DeleteQueueResult result = sqs.deleteQueue(new DeleteQueueRequest(queueUrl));
                _logger.info("shut down polling and deleted queue : " +queueUrl + " -- result : " + result);

            }


            // error handling is simple here - an exception will terminate just the impacted job, and the job is left
            // on the queue, so you can fix and re-drive. Alternatively you could catch and write to a dead letter queue
        }

    }

    private boolean processTask(Message message) {
        // todo : think about mapBetterNumber logic ... call here? separate executor?

        String faceId = message.getBody();
        String photoId = message.getMessageAttributes().get(MessageQueueHandler.PHOTO_ID).getStringValue();

        _logger.debug("processing post processing task for : " + message.getBody());
        PhotoSubjectDAO photoSubjectDAO = new PhotoSubjectDAO();
        PhotoDAO photoDAO = new PhotoDAO();

        // search face record in collection
        List<FaceMatch> faceImageMatches = ImgRecognitionHelper.getInstance().searchMatchingFaces(ImgRecognitionHelper.FACE_COLLECTION_ID, faceId);

        if (faceImageMatches.size()==0) {
            _logger.debug("No face matches for faceId : " + faceId);
            return false;
        }

        PhotoSubject bestPhotoSubject = null; // store the photoSubject here that contains a startnumber with the most digits
        int startnumberLength=0;
        String startnumber="n/a";
        HibernateUtil.beginTransaction();
        List<PhotoSubject> matchingPhotoSubjects = photoSubjectDAO.getMatchingPhotoSubjects(faceImageMatches,albumId);
        for (PhotoSubject photoSubject : matchingPhotoSubjects) {
            EventRunner runnerWithBestNumber = photoSubject.getEventRunners().stream()
                    .max(Comparator.comparing(o -> o.getStartnumber().length()))
                    .orElse(null);
            if (runnerWithBestNumber!=null && runnerWithBestNumber.getStartnumber().length() >startnumberLength) {
                startnumber = runnerWithBestNumber.getStartnumber();
                startnumberLength = runnerWithBestNumber.getStartnumber().length();
                bestPhotoSubject=photoSubject;
                _logger.debug("Startnumber length now : [" + runnerWithBestNumber.getStartnumber().length() +"] and photo subject = " + photoSubject);
                _logger.debug("Startnumber  = " + runnerWithBestNumber.getStartnumber());
            }
        }
        _logger.debug("");


        if (bestPhotoSubject!=null) {
            Photo photo = photoDAO.load(Long.valueOf(photoId));
            photo.addPhotoSubject(bestPhotoSubject);
            photoDAO.saveOrUpdate(photo);

            _logger.debug("**************************************************************************************************");
            _logger.debug("*********** Found startnumber for unmapped FaceID " + faceId + " --> Photosubject : " + bestPhotoSubject + "Startnumber : " + startnumber);
            _logger.debug("*********** Added for photo : " + photo.getFilename() + "**********");
            _logger.debug("**************************************************************************************************");
        }
        HibernateUtil.commitTransaction();
        _logger.debug("Post-Processing Transaction committed");

        return true;
    }





    public void shutdown() {
        executor.shutdown(); // don't use shutdownNow() - we want to finish execution of pending threads
        try {
            // Wait a while for existing tasks to terminate
            if (!executor.awaitTermination(20, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Cancel currently executing tasks after wait time
                // Wait a while for tasks to respond to being cancelled
                if (!executor.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Executor Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

}
