package ch.unartig.sportrait.imgRecognition;


import ch.unartig.sportrait.imgRecognition.processors.SportraitImageProcessorIF;
import ch.unartig.sportrait.imgRecognition.processors.StartnumberRecognitionProcessor;
import ch.unartig.studioserver.model.Photo;
import ch.unartig.studioserver.storageProvider.AwsS3FileStorageProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class Test {

    private static String sqsQueue; // some queue name to create the SQS queue
    private final AmazonS3 s3;
    private final String queueUrl;
    private final ThreadPoolExecutor executor;
    private List<SportraitImageProcessorIF> processors = new ArrayList<>();
    private AmazonRekognition rekognitionClient;
    private AmazonSQSClient sqs;
    private Pattern filter;
    private static final String defaultFilter = "\\.(jpg|jpeg|png)$";
    private AtomicInteger numSeenScanner = new AtomicInteger(0);
    private AtomicInteger numSeenProcessor = new AtomicInteger();
    private int maxImagesToProcess = -2; // set to -1 for infinite scanning queue when processing the batch
    private final List<Startnumber> startnumbers = new ArrayList<>();
    private final List<RunnerFace> facesWithoutNumbers = new ArrayList<>();
    private final String faceCollectionId; // maybe limit collection to etappe when used for sportrait
    private long maxQueueEntries;


    public Test() {
        rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
        sqs = new AmazonSQSClient(new ProfileCredentialsProvider().getCredentials());
        s3 = AmazonS3ClientBuilder.defaultClient();
        // ID for initializing the face collection
        faceCollectionId = "MyCollection";
        if (sqsQueue==null || sqsQueue.isEmpty()) {
            System.out.println("SQS Queue name not set - setting defautl  : " + "myTestSqsQueue");
            sqsQueue = "myTestSqsQueue";
        }

        filter = Pattern.compile(defaultFilter, Pattern.CASE_INSENSITIVE);

        // todo : not here
        CreateQueueResult queueResult = sqs.createQueue(sqsQueue); // empty queue name? does that work?
//        CreateQueueResult queueResult = sqs.createQueue(new CreateQueueRequest());
        queueUrl = queueResult.getQueueUrl();
        System.out.println("queueResult = " + queueResult);

        // define a processor for the tasks from the queue

        // process startnumber recognition
        processors.add(new StartnumberRecognitionProcessor(startnumbers,facesWithoutNumbers,rekognitionClient, faceCollectionId));


        // no limit for queue entries
        maxQueueEntries = -1L;

        // Executor Service
        int maxWorkers = 1; // no max workers defined ? 1 ?
        executor = new ThreadPoolExecutor(
                1, maxWorkers, 30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(maxWorkers * 2, false),
                new ThreadPoolExecutor.CallerRunsPolicy() // prevents backing up too many jobs
        );

    }

    /**
     * used for testing recognition on display images on the fly
     * @param photo
     * @param allStartnumbers
     * @return String with all numbers recognized
     * @deprecated don't use except for testing recognitions on single images
     */
    public List<Startnumber> getRecognizedNumbersFor(Photo photo, List<Startnumber> allStartnumbers) {


        // for testing purposes, make sure faces cellection is freshly initialized
        // deleteFacesCollection();
        // create a faces collection
        createFacesCollection();

        // need bucket and path
        String bucket = AwsS3FileStorageProvider.getS3BucketNameFor(photo.getAlbum());
        String key = AwsS3FileStorageProvider.getFineImageKey(photo.getAlbum(),photo.getFilename());
        List<TextDetection> photoTextDetections = getTextDetectionsFor(bucket, key);


        StartnumberRecognitionProcessor processor = new StartnumberRecognitionProcessor(allStartnumbers,facesWithoutNumbers, rekognitionClient, faceCollectionId);
        List<Startnumber> photoStartnumbers = processor.getStartnumbers(photoTextDetections,key);

        List<FaceRecord> photoFaceRecords = addFacesToCollection(bucket, key);

        processor.mapFacesToStartnumbers(photoFaceRecords, photo.getPathForImageService(), photoStartnumbers);

        System.out.println("*************************************");
        System.out.println("*********** Done for File ***********");
        System.out.println("*************************************");


        return photoStartnumbers;
    }

    public static void main(String[] args) throws Exception {

        String bucket = "test.rekognition.sportrait.com";
        String prefix = "2018-etappe-1-rekognition-test";
        String photo = "dev-upload-12/sola12_e11_mm_2003.jpg";

        sqsQueue = "myTestSqsQueue";



        // initialize Test and set environment
        Test test = new Test();

        // for testing purposes, make sure faces cellection is freshly initialized
        test.deleteFacesCollection();
        // create a faces collection
        test.createFacesCollection();

        // independent from queue, single photo detection:
        // test.detectSportraitStartnumber(photo, bucket);


        // 1st scan the given image bucket for images and create a queue containing the URLs in SQS
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
     * Loop through given bucket and add images to an SQS queue
     * @param bucket
     * @param prefix
     */
    private void scanBucket(String bucket, String prefix) {
        ListObjectsRequest listReq = new ListObjectsRequest()
                .withPrefix(prefix)
                .withBucketName(bucket);

        System.out.println("Scanning S3 bucket "+ bucket + "/" +prefix);
        ObjectListing listing = s3.listObjects(listReq);
        boolean ok = processObjects(listing.getObjectSummaries());

        while (ok && listing.isTruncated()) {
            listing = s3.listNextBatchOfObjects(listing);
            ok = processObjects(listing.getObjectSummaries());
        }

        System.out.println("Completed scan, added "+numSeenScanner+" images to the processing queue.");
    }

    /**
     * process objects = send path of all files in bucket to msg queue
     * @param objects
     * @return
     */
    private boolean processObjects(List<S3ObjectSummary> objects) {
        System.out.println("Scanning next batch of "+ objects.size());
        objects
                .parallelStream()
                .filter(this::shouldEnqueue)
                .forEach(object -> {
                    numSeenScanner.incrementAndGet();
                    String path = object.getBucketName() + "/" + object.getKey();
                    System.out.println("Posting: " + path);

                    SendMessageRequest msg = new SendMessageRequest()
                            .withQueueUrl(queueUrl)
                            .withMessageBody(path);
                    sqs.sendMessage(msg);
                });
        if (maxQueueEntries > -1L && numSeenScanner.incrementAndGet() > maxQueueEntries) {
            System.out.println("Added max jobs, quitting");
            return false;
        }

        maxImagesToProcess = maxImagesToProcess + objects.size();
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
            // todo : how many messages will be fetched? max =10 - but actual?
            ReceiveMessageRequest poll = new ReceiveMessageRequest(queueUrl)
                    .withMaxNumberOfMessages(10)
                    .withWaitTimeSeconds(20);
            List<Message> messages = sqs.receiveMessage(poll).getMessages();
            System.out.println("Got "+messages.size() + " messages from queue. Processed "+ numSeenProcessor +" so far. maxImagesToProcess = " + maxImagesToProcess);

            // process the messages in parallel.
            for (Message message : messages) {
                numSeenProcessor.incrementAndGet();
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
            if (maxImagesToProcess > -1 && numSeenProcessor.get() > maxImagesToProcess) {
                System.out.println("Seen enough ("+numSeenProcessor.get()+"), quitting. maxImagesToProcess = " + maxImagesToProcess);
                executor.shutdown(); // don't use shutdown now - we want to finish execution of pending threads


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

            // error handling is simple here - an exception will terminate just the impacted job, and the job is left
            // on the queue, so you can fix and re-drive. Alternatively you could catch and write to a dead letter queue
        }


        System.out.println("####### Executor has shutdown ##########");

        // need to wait for some time ?


        // process faces w/o number - try to find face matches and extract startnumber from the matches, add the startnumbers to the list of mapped startnumbers

        // todo : separate queue handling ?
        processFacesWithoutNumber();

        System.out.println("");
        System.out.println("");
        System.out.println("################################################");
        System.out.println("############   RESULTING MATCHES ###############");
        System.out.println("################################################");
        for (int i = 0; i < startnumbers.size(); i++) {
            Startnumber startnumber = startnumbers.get(i);
            System.out.println("startnumber = " + startnumber);
        }
        System.out.println("################################################");
        System.out.println("############   Done Processing   ###############");
        System.out.println("################################################");

    }

    private void processFacesWithoutNumber() {
        // for every face that has not been matched to a number: // todo : check for too many faces, bystanders etc.
        System.out.println("######################");
        System.out.println("Starting processing faces w/o numbers");
        System.out.println("######################");
        for (RunnerFace runnerFace : facesWithoutNumbers) {
            runnerFace.addStartnumberFromMatchingFacesInCollection(faceCollectionId, rekognitionClient, startnumbers);
        }

    }



    /**
     * process task for each image - called by executor
     * @param message payload delivered by the queue
     */
    private void processTask(Message message) {
        String photoPath = message.getBody();
        PathSplit pathComp = new PathSplit(photoPath);
        String bucket = pathComp.bucket;
        String key = pathComp.key;
        System.out.println("Processing " +  bucket +" "+ key);


        // text detection:
        List<TextDetection> photoTextDetections = getTextDetectionsFor(bucket, key);


        // add the faces of the file/photo to the collection and get a list of face records as return value
        // faces needed in collection for later comparison
        // todo : only add one face per startnumber to collection ? --> price wise not necessary
        List<FaceRecord> photoFaceRecords = addFacesToCollection(bucket, key);
        // getFacesDetails(bucket, key);


        // Process downstream actions:
        for (SportraitImageProcessorIF processor : processors) {
            // only one processor so far
            // todo : add facedetections to processor and do all in once processor?
            processor.process(photoTextDetections, photoFaceRecords, photoPath);
        }
    }

    /**
     * Only returns faces details w/o storing to collection
     * @param bucket
     * @param key
     */
    private void getFacesDetails(String bucket, String key) {
        // face detection
        DetectFacesRequest facesRequest = new DetectFacesRequest()
                .withImage(new Image()
                        .withS3Object(new S3Object()
                                .withName(key)
                                .withBucket(bucket)))
                .withAttributes(Attribute.ALL);
        // Replace Attribute.ALL with Attribute.DEFAULT to get default values.
        DetectFacesResult facesResult = rekognitionClient.detectFaces(facesRequest);
        // faces details needed?
        List<FaceDetail> faceDetails = facesResult.getFaceDetails();
    }

    private void deleteFacesCollection() {
        System.out.println("Deleting collection ...");

        DeleteCollectionRequest request = new DeleteCollectionRequest()
                .withCollectionId(faceCollectionId);
        DeleteCollectionResult deleteCollectionResult = null;

        try {
            deleteCollectionResult = rekognitionClient.deleteCollection(request);
            System.out.println(faceCollectionId + ": status code " + deleteCollectionResult.getStatusCode().toString());
        } catch (ResourceNotFoundException e) {
            System.out.println("Collection didn't exist ... ignoring");
            e.printStackTrace();
        }

    }


    /**
     * use indexFaces operation to add detected faces - with defined quality - to collection - creates an indexFacesResult
     * @param bucket
     * @param key
     */
    private List<FaceRecord> addFacesToCollection(String bucket, String key) {

        String filename; // = external ID for faces collection

        PathSplit pathComp = new PathSplit(key);
        filename= pathComp.filename; // part after last occurance of '/'
        System.out.println("Adding faces to collection for :  " +  bucket +" "+ key);
        System.out.println("filename used as external image id :  " +  filename);


        Image image = new Image()
                .withS3Object(new S3Object()
                        .withBucket(bucket)
                        .withName(key));

        IndexFacesRequest indexFacesRequest = new IndexFacesRequest()
                .withImage(image)
                .withQualityFilter(QualityFilter.AUTO) // use AUTO to apply amazon defined quality filtering - seems to work good
                .withMaxFaces(5) // detecting up to 5 faces - the biggest boxes will be returned
                .withCollectionId(faceCollectionId)
                .withExternalImageId(filename) // external image ID must be without '/' - only filename
                .withDetectionAttributes("DEFAULT"); // todo define

        IndexFacesResult indexFacesResult = null;
        try {
            indexFacesResult = rekognitionClient.indexFaces(indexFacesRequest);
        } catch (AmazonRekognitionException e) {
            System.out.println("Cannot index faces, see stacktrace - continuing");
            e.printStackTrace();
        }

        System.out.println("Done adding faces to collection for : " + key);
        System.out.println("Faces indexed:");
        List<FaceRecord> faceRecords = indexFacesResult.getFaceRecords();
        for (FaceRecord faceRecord : faceRecords) {
            System.out.println("  Face ID: " + faceRecord.getFace().getFaceId());
            System.out.println("  Location:" + faceRecord.getFaceDetail().getBoundingBox().toString());
        }

        // for debug purposes:
        List<UnindexedFace> unindexedFaces = indexFacesResult.getUnindexedFaces();
        System.out.println("Faces not indexed:");
        for (UnindexedFace unindexedFace : unindexedFaces) {
            System.out.println("  Location:" + unindexedFace.getFaceDetail().getBoundingBox().toString());
            System.out.println("  Reasons:");
            for (String reason : unindexedFace.getReasons()) {
                System.out.println("   " + reason);
            }
        }
        return faceRecords;
    }


    private void createFacesCollection() {
        System.out.println("Creating collection: " +
                faceCollectionId);

        CreateCollectionRequest request = new CreateCollectionRequest()
                .withCollectionId(faceCollectionId);

        CreateCollectionResult createCollectionResult = null;
        try {
            createCollectionResult = rekognitionClient.createCollection(request);
            System.out.println("CollectionArn : " +
                    createCollectionResult.getCollectionArn());
            System.out.println("Status code : " +
                    createCollectionResult.getStatusCode().toString());
        } catch (ResourceAlreadyExistsException e) {
            System.out.println("Ignoring - Collection already existed");
        }
    }



    /**
     * Todo : put in helper class
     * @param bucket
     * @param key
     * @return
     */
    private List<TextDetection> getTextDetectionsFor(String bucket, String key) {
        // text detection:
        DetectTextRequest textRequest = new DetectTextRequest()
                .withImage(new Image()
                        .withS3Object(new S3Object()
                                .withName(key)
                                .withBucket(bucket)));


        // create list of number/filename and print it out a the end

        DetectTextResult textResult = rekognitionClient.detectText(textRequest);
        return textResult.getTextDetections();
    }


    static public class PathSplit {
        public final String bucket;
        public final String key;
        public final String filename;

        public PathSplit(String path) {
            bucket = path.substring(0, path.indexOf('/'));
            key = path.substring(bucket.length() + 1);
            filename = path.substring(path.lastIndexOf('/')+1); // part after last occurence of '/'
        }
    }
}
