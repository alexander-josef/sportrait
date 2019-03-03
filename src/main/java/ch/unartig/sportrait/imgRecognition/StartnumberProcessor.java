package ch.unartig.sportrait.imgRecognition;

import org.apache.log4j.Logger;

/**
 * Processor class that starts up with the intialization of the servlet
 * polls sqs for queues of albums with images to process
 */
public class StartnumberProcessor {
    private Logger _logger = Logger.getLogger(getClass().getName());

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        init();
    }


    public StartnumberProcessor() {
    }

    /**
     * Called upon initialization of the servlet
     */
    public static void init() {
        System.out.println("**** Starting up Startnumber Processor");
        StartnumberProcessor processor = new StartnumberProcessor();
        processor.start();

    }


    public void start() {
        _logger.info("Start Polling the SQS queues for incoming images to recognize ....");

        _logger.info(" TODO .....");

        // todo
    }




}
