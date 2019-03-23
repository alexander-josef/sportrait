package ch.unartig.sportrait.imgRecognition;

import ch.unartig.sportrait.imgRecognition.processors.SportraitImageProcessorIF;
import ch.unartig.sportrait.imgRecognition.processors.StartnumberRecognitionDbProcessor;
import ch.unartig.studioserver.Registry;
import com.amazonaws.SdkClientException;
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
    private static final int EXECUTOR_KEEP_ALIVE_TIME = 30;
    private static final int MAX_WORKERS = 8; // used to be 1 - what's possible with the rekognition service? will we run into problem with a higher number?
    private static final int CORE_POOL_SIZE = 4;
    private Logger _logger = Logger.getLogger(getClass().getName());
    private static final int MAX_NUMBER_OF_MESSAGES = 10;
    private static final int WAIT_TIME_SECONDS = 20;
    private final ThreadPoolExecutor executor;
    private final AmazonSQS sqs;
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
        maxImagesToProcess = -1;
        sqs = AmazonSQSClientBuilder.defaultClient();


        // Executor Service
        executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAX_WORKERS, EXECUTOR_KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(MAX_WORKERS * 2, false),
                new ThreadPoolExecutor.CallerRunsPolicy() // prevents backing up too many jobs
        );

        // process startnumber recognition
        // todo : parameters ?
        // todo : startnumbers to be stored to db?
        processors.add(new StartnumberRecognitionDbProcessor());
        /**
         * just one faces collection ?
         */
        ImgRecognitionHelper.getInstance().createFacesCollection();
    }

    /**
     * overridden run method to start up the thread
     */
    public void run() {
        String queueUrl = MessageQueueHandler.getInstance().getSportraitQueueName();
        _logger.info("Start Polling the SQS queue - environment  : ["+ Registry.getApplicationEnvironment() +"] for incoming images to recognize ....");
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
            List<Message> messages;
            try {
                messages = sqs.receiveMessage(poll).getMessages();
            } catch (QueueDoesNotExistException e) {
                _logger.error("Queue does not exist");
                // todo : don't create directly - use MessageQueueHandler:
                CreateQueueResult queueResult = sqs.createQueue(MessageQueueHandler.getInstance().getSportraitQueueName());
                _logger.info("Created new queue with URL : " + queueResult.getQueueUrl());
                messages = sqs.receiveMessage(poll).getMessages();
            } catch (SdkClientException e) {
                _logger.warn("ignoring unknown exception : ",e);
                messages = new ArrayList<>();
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


        _logger.info("####### Executor has shutdown ##########");

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
        List<TextDetection> photoTextDetections = ImgRecognitionHelper.getInstance().getTextDetectionsFor(bucket, key);
        List<FaceRecord> photoFaceRecords = ImgRecognitionHelper.getInstance().addFacesToCollection(bucket, key, filename);

        // Process downstream actions:
        for (SportraitImageProcessorIF processor : processors) {
            // only one processor so far
            processor.process(photoTextDetections, photoFaceRecords, photoPath, ImgRecognitionHelper.FACE_COLLECTION_ID, photoId);
        }
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
