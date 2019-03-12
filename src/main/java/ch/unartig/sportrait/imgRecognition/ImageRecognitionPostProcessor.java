package ch.unartig.sportrait.imgRecognition;

import ch.unartig.sportrait.imgRecognition.processors.SportraitImageProcessorIF;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.InvalidParameterException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import org.apache.log4j.Logger;

import java.util.ArrayList;
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
    private AmazonRekognition rekognitionClient;
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
        rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
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
        // todo : check, does this work ? or is the queue only created further down in the catch clause?
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
                sqs.deleteMessage(queueUrl, message.getReceiptHandle());
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
            }


            // error handling is simple here - an exception will terminate just the impacted job, and the job is left
            // on the queue, so you can fix and re-drive. Alternatively you could catch and write to a dead letter queue
        }

    }

    private boolean processTask(Message message) {
        _logger.debug("processing post processing task for : " + message.getBody());
        _logger.debug(" ******* do nothing *********");
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
