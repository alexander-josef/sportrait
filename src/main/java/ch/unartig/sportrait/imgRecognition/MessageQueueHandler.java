package ch.unartig.sportrait.imgRecognition;

import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.model.Album;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class to handle amazon sqs queues related logic
 */
public class MessageQueueHandler {
    private static final String SPORTRAIT_QUEUE_NAME_PREFIX = "sportraitQueueName-";
    private Logger _logger = Logger.getLogger(getClass().getName());
    static final String EVENT_CATEGORY_ID = "eventCategoryId"; // used as message attribute
    static final String PHOTO_ID = "photoId"; // used as message attribute
    private static final String UNKNOWN_FACES_QUEUE_PREFIX = "UnknownFacesQueue-Album-";
    private String sportraitQueueName;
    private AmazonSQS sqs;
    /**
     * Single instance of class to be retrieved via the static getter
     */
    private static final MessageQueueHandler INSTANCE = new MessageQueueHandler();

    public static MessageQueueHandler getInstance() {
        return INSTANCE;
    }

    private MessageQueueHandler() {
        sqs = AmazonSQSClientBuilder.defaultClient();
        sportraitQueueName = SPORTRAIT_QUEUE_NAME_PREFIX + Registry.getApplicationEnvironment(); // todo : either name per album or move to Registry
    }

    /**
     * return a queue that holds messages for the album passed as parameter. queue will be created if it doesn't exist yet
     * @param album needed to derive the queue name (-> generic level ID) - not used yet
     * @return
     */
    private CreateQueueResult getImageRecognitionQueue(Album album) {
        // todo later : create a queue per album - start with queue
        // CreateQueueResult queueResult = sqs.createQueue(album.getGenericLevelId().toString());
        CreateQueueResult queueResult = sqs.createQueue(sportraitQueueName);
        // if necessary, add parameters to the queue, like the visibility timeout, see also here :
        // https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/sqs/model/CreateQueueRequest.html#addAttributesEntry-java.lang.String-java.lang.String-

        String queueUrl = queueResult.getQueueUrl();
        _logger.debug("created queue / queue URL : " + queueUrl);
        return queueResult;
    }


    /**
     * Add master image path of an s3 object to a queue (via s3ObjectSummary). submit eventCategeryId and photoId as message attributes
     * @param object s3 object for which the path shall be added to the queue, determinded by the albumds generic level id
     * @param album
     * @param photoId
     */
    public SendMessageResult addMessage(S3ObjectSummary object, Album album, Long photoId) {

        String path = object.getBucketName() + "/" + object.getKey();
        return addMessage(album, photoId, path);
    }

    /**
     * Add master image path of an s3 object to a message queue. submit eventCategeryId and photoId as message attributes
     * @param album
     * @param photoId
     * @param path
     * @return
     */
    public SendMessageResult addMessage(Album album, Long photoId, String path) {
        String queueUrl = getImageRecognitionQueue(album).getQueueUrl();
        _logger.info("Posting message : " + path + " to queue ["+ queueUrl +"]");
        _logger.debug("with param [eventCategoryId] : " + album.getEventCategory().getEventCategoryId().toString());
        _logger.debug("with param [photoId] : " + photoId.toString());


        final Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        messageAttributes.put(EVENT_CATEGORY_ID, new MessageAttributeValue()
                .withDataType("String")
                .withStringValue(album.getEventCategory().getEventCategoryId().toString()));

        messageAttributes.put(PHOTO_ID, new MessageAttributeValue()
                .withDataType("String")
                .withStringValue(photoId.toString()));


        SendMessageRequest msg = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(path)
                .withMessageAttributes(messageAttributes);

        SendMessageResult result = sqs.sendMessage(msg);

        return result;
    }


    public String getSportraitQueueName() {
        return sportraitQueueName;
    }

    public String getUnknownFacesQueueName(Long albumId) {
        String queueName = UNKNOWN_FACES_QUEUE_PREFIX + +albumId + "_" + Registry.getApplicationEnvironment();
        _logger.debug("returning unknown faces queue name : " + queueName);
        return queueName;
    }

    /**
     * Add a message to a separate queue for every unknown face in a etappe
     * @param runnerFace used to extract faceID for the message body
     * @param photoId - will be submitted as a message attribute
     * @param albumId
     * @return SendMessageResult
     */
    public SendMessageResult    addMessageForUnknownFace(RunnerFace runnerFace, String photoId, Long albumId) {
        String path = runnerFace.getPath();
        String faceId = runnerFace.getFaceRecord().getFace().getFaceId();

        CreateQueueResult queueResult = sqs.createQueue(getUnknownFacesQueueName(albumId));

        String queueUrl = queueResult.getQueueUrl();
        _logger.info("Posting message for unknown face [ID : "+faceId+"] in  : " + path + " to queue ["+ queueUrl +"]");
        _logger.debug("with param [photoId] : " + photoId);


        final Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();

        messageAttributes.put(PHOTO_ID, new MessageAttributeValue()
                .withDataType("String")
                .withStringValue(photoId));


        SendMessageRequest msg = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(faceId)
                .withMessageAttributes(messageAttributes);

        SendMessageResult result = sqs.sendMessage(msg);

        return result;


    }


}
