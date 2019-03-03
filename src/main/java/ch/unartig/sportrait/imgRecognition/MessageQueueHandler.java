package ch.unartig.sportrait.imgRecognition;

import ch.unartig.studioserver.model.Album;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import org.apache.log4j.Logger;

/**
 * Singleton class to handle amazon sqs queues related logic
 */
public class MessageQueueHandler {

    public static final String EVENT_CATEGORY_ID = "eventCategoryId"; // used as message attribute
    private Logger _logger = Logger.getLogger(getClass().getName());

    private AmazonSQS sqs;


    /**
     * Single instance of class to be retrieved via the static getter
     */
    private static final MessageQueueHandler INSTANCE = new MessageQueueHandler();
    private String sportraitQueueName;

    public static MessageQueueHandler getInstance() {
        return INSTANCE;
    }
    private MessageQueueHandler() {
        sqs = AmazonSQSClientBuilder.defaultClient();
        sportraitQueueName = "sportraitQueueName"; // todo : either name per album or move to Registry
    }

    /**
     * return a queue that holds messages for the album passed as parameter. queue will be created if it doens't exist yet
     * @param album needed to derive the queue name (-> generic level ID)
     * @return
     */
    private CreateQueueResult getQueue(Album album) {
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
     * add path of an s3 object to a queue
     * @param object s3 object for which the path shall be added to the queue, determinded by the albumds generic level id
     * @param album
     */
    public SendMessageResult addMessage(S3ObjectSummary object, Album album) {

        String path = object.getBucketName() + "/" + object.getKey();
        String queueUrl = getQueue(album).getQueueUrl();
        _logger.info("Posting message : " + path + " to queue ["+ queueUrl +"]");

        SendMessageRequest msg = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(path)
                .addMessageAttributesEntry(EVENT_CATEGORY_ID,new MessageAttributeValue().withStringValue(album.getEventCategory().getEventCategoryId().toString()));
        SendMessageResult result = sqs.sendMessage(msg);

        return result;
    }


    public String getSportraitQueueName() {
        return sportraitQueueName;
    }
}
