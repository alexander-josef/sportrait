package ch.unartig.sportrait.imgRecognition;


import ch.unartig.sportrait.imgRecognition.processors.SportraitImageProcessorIF;
import ch.unartig.sportrait.imgRecognition.processors.StartnumberProcessor;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class Test {

    private static String sqsQueue; // some queue name to create the SQS queue
    private final AmazonS3Client s3;
    private final String queueUrl;
    private final ThreadPoolExecutor executor;
    private List<SportraitImageProcessorIF> processors = new ArrayList<>();
    private AmazonRekognition rekognitionClient;
    private AmazonSQSClient sqs;
    private Pattern filter;
    private static final String defaultFilter = "\\.(jpg|jpeg|png)$";
    private AtomicInteger numSeen = new AtomicInteger();
    private int maxImagesToProcess = -1;
    private final List<String[]> startnummern = new ArrayList<>();



    private Test() {
        rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
        sqs = new AmazonSQSClient(new ProfileCredentialsProvider().getCredentials());
        s3 = new AmazonS3Client(new ProfileCredentialsProvider().getCredentials());

        filter = Pattern.compile(defaultFilter, Pattern.CASE_INSENSITIVE);

        CreateQueueResult queueResult = sqs.createQueue(sqsQueue); // empty queue name? does that work?
//        CreateQueueResult queueResult = sqs.createQueue(new CreateQueueRequest());
        queueUrl = queueResult.getQueueUrl();
        System.out.println("queueResult = " + queueResult);

        // define a processor for the tasks from the queue

        // process startnumber recognition
        processors.add(new StartnumberProcessor(startnummern));

        // Executor Service
        int maxWorkers = 1; // no max workers defined ? 1 ?
        executor = new ThreadPoolExecutor(
                1, maxWorkers, 30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(maxWorkers * 2, false),
                new ThreadPoolExecutor.CallerRunsPolicy() // prevents backing up too many jobs
        );


    }

    public static void main(String[] args) throws Exception {

        String bucket = "test.rekognition.sportrait.com";
        String prefix = "batchtest";
        String photo = "dev-upload-12/sola12_e11_mm_2003.jpg";

        sqsQueue = "myTestSqsQueue";



        // initialize Test and set environment
        Test test = new Test();

        // independent from queue, single photo detection:
        // test.detectSportraitStartnumber(photo, bucket);


        // 1st scan the given image bucket (and create a queue containing the URLs)
        test.scanBucket(bucket, prefix);

        // 2nd process according to the given processors
        test.startProcessing();


    }


    private void bla() {





//        detectLabels(photo, bucket, rekognitionClient);

        List sportraitRekognitionRequests = null;


        for (int i = 0; i < sportraitRekognitionRequests.size(); i++) {
            Object photo = sportraitRekognitionRequests.get(i);


        }


    }

    private static void detectLabels(String photo, String bucket, AmazonRekognition rekognitionClient) {
        // label detection:
        DetectLabelsRequest request = new DetectLabelsRequest()
                .withImage(new Image()
                        .withS3Object(new S3Object()
                                .withName(photo).withBucket(bucket)))
                .withMaxLabels(10)
                .withMinConfidence(75F);

        try {
            DetectLabelsResult result = rekognitionClient.detectLabels(request);
            List<Label> labels = result.getLabels();

            System.out.println("Detected labels for " + photo);
            for (Label label: labels) {
                System.out.println(label.getName() + ": " + label.getConfidence().toString());
            }


        } catch(AmazonRekognitionException e) {
            e.printStackTrace();
        }
    }

    /**
     *  @param photo
     * @param bucket
     */
    private void detectSportraitStartnumber(String photo, String bucket) {

        // todo : return startnumbers here ...

        // text detection:
        DetectTextRequest request = new DetectTextRequest()
                .withImage(new Image()
                        .withS3Object(new S3Object()
                                .withName(photo)
                                .withBucket(bucket)));


        // create list of number/filename and print it out a the end
        List<String[]> startnummern = new ArrayList<>();

        try {
            DetectTextResult result = rekognitionClient.detectText(request);
            List<TextDetection> textDetections = result.getTextDetections();


            TextDetection lastLine = null;
            System.out.println("Detected lines and words for " + photo);
            for (TextDetection text: textDetections) {

                // check for startnumber: (is a line, confidence > 95, parentID = null, previous LINE ID starts with SOLA)
                if (text.getType().equals("LINE")
                        && text.getConfidence()>95
                        && text.getParentId()==null
                        && (lastLine!=null && lastLine.getDetectedText().startsWith("SOL")))
                {
                    startnummern.add(new String[]{text.getDetectedText(), photo});
                    lastLine=text;

                } else if (text.getType().equals("LINE")) {
                    lastLine=text;
                }



                System.out.println("Detected: " + text.getDetectedText());
                System.out.println("Confidence: " + text.getConfidence().toString());
                System.out.println("Id : " + text.getId());
                System.out.println("Parent Id: " + text.getParentId());
                System.out.println("Type: " + text.getType());
                System.out.println();
            }
        } catch(AmazonRekognitionException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < startnummern.size(); i++) {
            String[] strings = startnummern.get(i);
            System.out.println("startnummer = " + Arrays.toString(strings));
        }
    }



    private void scanBucket(String bucket, String prefix) {
        ListObjectsRequest listReq = new ListObjectsRequest()
                .withPrefix(prefix)
                .withBucketName(bucket);

        System.out.println("Scanning S3 bucket "+ bucket +prefix);
        ObjectListing listing = s3.listObjects(listReq);
        boolean ok = processObjects(listing.getObjectSummaries());

        while (ok && listing.isTruncated()) {
            listing = s3.listNextBatchOfObjects(listing);
            ok = processObjects(listing.getObjectSummaries());
        }

        System.out.println("Completed scan, added ... images to the processing queue.");
    }

    private boolean processObjects(List<S3ObjectSummary> objects) {
        System.out.println("Scanning next batch of "+ objects.size());
        objects
                .parallelStream()
                .filter(this::shouldEnqueue)
                .forEach(object -> {
                    numSeen.incrementAndGet();
                    String path = object.getBucketName() + "/" + object.getKey();
                    System.out.println("Posting: " + path);

                    SendMessageRequest msg = new SendMessageRequest()
                            .withQueueUrl(queueUrl)
                            .withMessageBody(path);
                    sqs.sendMessage(msg);
                });
//        if (max > -1L && numSeen.incrementAndGet() > max) {
//            Logger.Info("Added max jobs, quitting");
//            return false;
//        }

        return true;
    }

    // todo - interface method passed in scanBucket if you need more than a regex
    private boolean shouldEnqueue(S3ObjectSummary object) {
        return filter.matcher(object.getKey()).find();
    }



    private void startProcessing() {

        if (processors.isEmpty()) {
            System.out.println("No processors defined, will not start up.");
            return;
        }

        System.out.println("Processor started up, looking for messages on " + queueUrl);

        while (!executor.isShutdown()) {
            // poll for messages on the queue.
            ReceiveMessageRequest poll = new ReceiveMessageRequest(queueUrl)
                    .withMaxNumberOfMessages(10)
                    .withWaitTimeSeconds(20);
            List<Message> messages = sqs.receiveMessage(poll).getMessages();
            System.out.println("Got "+messages.size() + " messages from queue. Processed "+numSeen +" so far.");

            // process the messages in parallel.
            for (Message message : messages) {
                numSeen.incrementAndGet();
                executor.execute(() -> {
                    try {
                        processTask(message);
                    } catch (InvalidParameterException e) {
                        if (e.getMessage().contains("Minimum image height")) {
                            System.out.println("Input image "+ message.getBody() +" too small to analyze, skipping.");
                        }
                    }
                });

                // remove the job from the queue when completed successfully (or skipped)
                sqs.deleteMessage(queueUrl, message.getReceiptHandle());
            }
            if (maxImagesToProcess > -1 && numSeen.get() > maxImagesToProcess) {
                System.out.println("Seen enough ("+numSeen.get()+"), quitting.");
                executor.shutdownNow();
            }

            // error handling is simple here - an exception will terminate just the impacted job, and the job is left
            // on the queue, so you can fix and re-drive. Alternatively you could catch and write to a dead letter queue


            for (int i = 0; i < startnummern.size(); i++) {
                String[] strings = startnummern.get(i);
                System.out.println("startnummer = " + Arrays.toString(strings));
            }



        }
    }

    private void processTask(Message message) {
        String path = message.getBody();
        PathSplit pathComp = new PathSplit(path);
        String bucket = pathComp.bucket;
        String key = pathComp.key;
        System.out.println("Processing" +  bucket +" "+ key);

        // Rekognition: Detect Labels from S3 object
/*

        DetectLabelsRequest req = new DetectLabelsRequest()
                .withImage(new Image().withS3Object(new S3Object().withBucket(bucket).withName(key)))
                .withMinConfidence(minConfidence);
        DetectLabelsResult result;
        result = rek.detectLabels(req);
        List<Label> labels = result.getLabels();
        System.out.println("In %s, found: %s", key, labels);

*/

        // text detection:
        DetectTextRequest request = new DetectTextRequest()
                .withImage(new Image()
                        .withS3Object(new S3Object()
                                .withName(key)
                                .withBucket(bucket)));


        // create list of number/filename and print it out a the end

        DetectTextResult result = rekognitionClient.detectText(request);
        List<TextDetection> textDetections = result.getTextDetections();


        // Process downstream actions:
        for (SportraitImageProcessorIF processor : processors) {
            // only one processor so far
            processor.process(textDetections, path);
        }
    }

    static public class PathSplit {
        public final String bucket;
        public final String key;

        public PathSplit(String path) {
            bucket = path.substring(0, path.indexOf('/'));
            key = path.substring(bucket.length() + 1);
        }
    }
}
