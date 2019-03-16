package ch.unartig.sportrait.imgRecognition;

import ch.unartig.sportrait.imgRecognition.processors.SportraitImageProcessorIF;
import ch.unartig.sportrait.imgRecognition.processors.StartnumberRecognitionDbProcessor;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Processor class that starts up with the intialization of the servlet
 * polls sqs for queues of albums with images to process
 */
public class StartnumberProcessor implements Runnable {
    public static final int MAX_FACES = 5;
    private Logger _logger = Logger.getLogger(getClass().getName());
    public static final int MAX_NUMBER_OF_MESSAGES = 10;
    public static final int WAIT_TIME_SECONDS = 20;
    private final ThreadPoolExecutor executor;
    private final AmazonSQS sqs;
    private AmazonRekognition rekognitionClient;
    private List<SportraitImageProcessorIF> processors = new ArrayList<>();
    private AtomicInteger numSeenProcessor = new AtomicInteger();
    private int maxImagesToProcess;


    /**
     * Called upon initialization of the servlet
     * starts polling the sqs qeue
     * needs to  run in its own thread
     */
    public static void init() {
        StartnumberProcessor processor = new StartnumberProcessor();
        processor.run();


    }


    /**
     * standard constructor
     */
    public StartnumberProcessor() {
        _logger.info("**** Starting up Startnumber Processor");
        rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
        maxImagesToProcess = -1;
        sqs = AmazonSQSClientBuilder.defaultClient();


        // Executor Service
        int maxWorkers = 1; // no max workers defined ? 1 ?
        executor = new ThreadPoolExecutor(
                1, maxWorkers, 30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(maxWorkers * 2, false),
                new ThreadPoolExecutor.CallerRunsPolicy() // prevents backing up too many jobs
        );

        // process startnumber recognition
        // todo : parameters ?
        // todo : startnumbers to be stored to db?
        processors.add(new StartnumberRecognitionDbProcessor());
        /**
         * just one faces collection ?
         */
        ImgRecognitionHelper.createFacesCollection(rekognitionClient);
    }

    /**
     * overridden run method to start up the thread
     */
    public void run() {
        String queueUrl = MessageQueueHandler.getInstance().getSportraitQueueName();
        _logger.info("Start Polling the SQS queues for incoming images to recognize ....");
        if (processors.isEmpty()) {
            _logger.warn("No processors defined, will not start up.");
            return;
        }

        // todo  : start with single queue  - add later a list of queue (one for each album)
        // for loop for all queues currently active (-> receive from queue handler)

        // todo : make sure queue exists or create queue if it doesn't yet?

        _logger.info("Processor started up, looking for messages on " + queueUrl);

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
                CreateQueueResult queueResult = sqs.createQueue(MessageQueueHandler.getInstance().getSportraitQueueName());
                _logger.info("Created new queue with URL : " + queueResult.getQueueUrl());
                messages = sqs.receiveMessage(poll).getMessages();
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
                        }
                    }
                });

                // remove the job from the queue when completed successfully (or skipped)
                sqs.deleteMessage(queueUrl, message.getReceiptHandle());
            }
            // todo  : check exit clause - how many images? forever?
            if (maxImagesToProcess > -1 && numSeenProcessor.get() > maxImagesToProcess) {
                _logger.debug("Seen enough (" + numSeenProcessor.get() + "), quitting. maxImagesToProcess = " + maxImagesToProcess);
                shutdown();


            }

            // error handling is simple here - an exception will terminate just the impacted job, and the job is left
            // on the queue, so you can fix and re-drive. Alternatively you could catch and write to a dead letter queue
        }


        System.out.println("####### Executor has shutdown ##########");

        // todo: think about faces collection ??


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


    /**
     * process task for each image - called by executor
     *
     * @param message payload delivered by the queue
     */
    private void processTask(Message message) {
        String photoPath = message.getBody();
        Map<String, MessageAttributeValue> messageAttributes = message.getMessageAttributes();

        String eventCategoryId = messageAttributes.get(MessageQueueHandler.EVENT_CATEGORY_ID).getDataType();
        String photoId = messageAttributes.get(MessageQueueHandler.PHOTO_ID).getStringValue();
        PathSplit pathComp = new PathSplit(photoPath);
        String bucket = pathComp.bucket;
        String key = pathComp.key;
        String filename = pathComp.filename;
        _logger.debug("Processing " + bucket + " " + key);


        // text detection:
        List<TextDetection> photoTextDetections = ImgRecognitionHelper.getTextDetectionsFor(rekognitionClient, bucket, key);
        List<FaceRecord> photoFaceRecords = addFacesToCollection(bucket, key, filename, eventCategoryId);

        // Process downstream actions:
        for (SportraitImageProcessorIF processor : processors) {
            // only one processor so far
            processor.process(photoTextDetections, photoFaceRecords, photoPath, ImgRecognitionHelper.FACE_COLLECTION_ID, photoId);
        }
    }


    /**
     * add the faces of the file/photo to the collection and get a list of face records as return value
     * use indexFaces operation to add detected faces - with defined quality - to collection with ID = eventCategoryId of the image that is processed - creates an indexFacesResult
     * faces needed in collection for later comparison
     * Check todo's
     *
     * @param bucket
     * @param key
     * @param collectionId
     * @return list of faces records - can be null
     */
    private List<FaceRecord> addFacesToCollection(String bucket, String key, String filename, String collectionId) {
        // todo : put faces to collections per album
        // todo : only add one face per startnumber to collection ? --> price wise not necessary
        // todo : check if we have too many false positives when collection grows

        _logger.debug("Adding faces to collection for :  " + bucket + " " + key);
        _logger.debug("filename used as external image id :  " + filename);

        Image image = new Image()
                .withS3Object(new S3Object()
                        .withBucket(bucket)
                        .withName(key));

        IndexFacesRequest indexFacesRequest = new IndexFacesRequest()
                .withImage(image)
                .withQualityFilter(QualityFilter.AUTO) // use AUTO to apply amazon defined quality filtering - seems to work good
                .withMaxFaces(MAX_FACES) // detecting up to 5 faces - the biggest boxes will be returned
                .withCollectionId(ImgRecognitionHelper.FACE_COLLECTION_ID) // todo : just one global collection used for now - shall we use eventCategoryId of photo instead as the collection ID ?? How would we make sure the collection is initialized ? have a map that indicates true or false ?
                .withExternalImageId(filename) // external image ID must be without '/' - only filename
                .withDetectionAttributes("DEFAULT");

        IndexFacesResult indexFacesResult = null;
        try {
            indexFacesResult = rekognitionClient.indexFaces(indexFacesRequest);
        } catch (AmazonRekognitionException e) {
            _logger.warn("Cannot index faces, see stacktrace - continuing");
            e.printStackTrace();
        }

        _logger.debug("Done adding faces to collection for : " + key);
        List<FaceRecord> faceRecords = null;
        if (indexFacesResult != null) {
            _logger.debug("Faces indexed:");
            faceRecords = indexFacesResult.getFaceRecords();
            for (FaceRecord faceRecord : faceRecords) {
                _logger.debug("  Face ID: " + faceRecord.getFace().getFaceId());
                _logger.debug("  Location:" + faceRecord.getFaceDetail().getBoundingBox().toString());
            }

            // for debug purposes:
            List<UnindexedFace> unindexedFaces = indexFacesResult.getUnindexedFaces();
            _logger.debug("Faces not indexed:");
            for (UnindexedFace unindexedFace : unindexedFaces) {
                _logger.debug("  Location:" + unindexedFace.getFaceDetail().getBoundingBox().toString());
                _logger.debug("  Reasons:");
                for (String reason : unindexedFace.getReasons()) {
                    _logger.debug("   " + reason);
                }
            }
        } else {
            _logger.warn("No faces indexed - indexFacesResult = null !");
        }
        return faceRecords;
    }


    static public class PathSplit {
        public final String bucket;
        public final String key;
        public final String filename;

        public PathSplit(String path) {
            bucket = path.substring(0, path.indexOf('/'));
            key = path.substring(bucket.length() + 1);
            filename = path.substring(path.lastIndexOf('/') + 1); // part after last occurence of '/'
        }
    }


}
