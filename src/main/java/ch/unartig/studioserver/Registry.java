package ch.unartig.studioserver;

import ch.unartig.studioserver.storageProvider.FileStorageProviderInterface;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.Properties;

/**
 * Global Registry class for constants and fields from properties file
 */
public final class Registry
{

    public static Logger _logger = LogManager.getLogger("ch.unartig.studioserver.Registry");

    public static final int _PHOTO_IMPORT_TIMEOUT_SEC = 10; // timeout after each photo import in DEV env to better test the current imports status information
    public static final int _ALBUM_IMPORT_STATUS_TIMEOUT_SEC = 2; // timeout after each photo import in DEV env to better test the current imports status information
    public static final String _LANDSCAPE_MODE_SUFFIX = "landscape";
    public static  boolean _DevEnv = false; // constant variable to indicate if we're in dev environment - initializes to false, will be set to true for dev env / used in JSPa
    public static  boolean _IntEnv = false; // constant variable to indicate if we're in int environment - initializes to false, will be set to true for dev env / used in JSPa
    public static  boolean _ProdEnv = false; // constant variable to indicate if we're in prod environment - initializes to false, will be set to true for dev env / used in JSPa

    // todo: move to appSettings
    public static final Regions AWS_FRANKFURT_REGION = Regions.EU_CENTRAL_1;
    public final static Region awsRegionFrankfurt = Region.getRegion(AWS_FRANKFURT_REGION); // Frankfurt - used for bucket URLs in pre-image-service configuration - conflict with EU-WEST-1 buckets and services?
    public static final Regions SPORTRAIT_AWS_DEFAULT_REGION = AWS_FRANKFURT_REGION; // Ireland - used for bucket URLs after image recognition, used for queues

    // Where the fine images are located. No document root, not accessible by a web server.
    // example: /Users/alexanderjosef/DEV/sportrait-web/fine-images
    private static String fineImagesDirectory;
    // We need the document root and the context for web-images! document root = /opt/DATA/sportrait/web-images ; context web-images
    private static String logosScriptPath;
    // todo confusing: distinguish between the serverFinePath and the fine directory
    private static final String finePath = "fine/"; // used in all storage providers as path denominator
    public static final String _PORTRAIT_MODE_SUFFIX = "portrait";
    /*the number of thumbnail photos on the preview page*/
    public static int itemsOnPage = 14;

    /*this addess will be used for sending a request from the contact form to fogbugz  */
    private static final String customerServiceAddress = "info@sportrait.com";
    /*this is the sender for the confirmation email after an order has been confirmed*/
    private static final String orderConfirmationFromAddress = "info@sportrait.com";


    public static FileStorageProviderInterface fileStorageProvider; // Implementation of the configured file storage provider class (either local file storage or AWS S3



    private static final String mailFromAddress = "info@unartig.ch";
    public static final String _GENDER_MALE_CODE = "m";
    /**
     * time-interval in minutes to be shown in overview
     */
    private static boolean demoOrderMode = false;
    private static boolean simulateOrderOnly = false;
    public static final String _NAME_SPORTSALBUM_LEVEL_TYPE = "SportsAlbum";
    public static final String _NAME_ALBUM_LEVEL_TYPE = "Album";
    public static final String _NAME_SPORTSEVENT_LEVEL_TYPE = "SportsEvent" ;
    public static final String _NAME_EVENT_LEVEL_TYPE = "Event";
    public static final String _NAME_EVENTGROUP_LEVEL_TYPE = "EventGroup";
    public static final String _NAME_ALBUM_TYPE_PARAM = "type";
    private static String logosOverlayPortraitFile;
    private static String logosOverlayLandscapeFile;
    private static boolean applyLogoOrWatermarkOnFineImage;
    private static String s3BucketName;
    private static String sponsorBarFile; // full path to image that contains sponsor bar that will be copied over image
    private static String logoImageFile; // full path of logo image file that will be copied over the image on the upper left

    private static String applicationEnvironment; // dev, int or prod
    private static String imgixSignKey; // environment dependant sign key as generated on the imgix admin website
    private static String imgixSignKey2; // environment dependant sign key as generated on the imgix admin website for the 2nd imgix source
    private static String amazonSqsQueueName; // installation dependant queue name for image recognition - each imported photo will be stored to this sqs queue for later image recognition


    /**
     * class won't be instantiated, hence private constructor
     */
    private Registry()
    {
    }


    /**
     * will be called upon startup of the Action servlet
     */
    public static void init() throws ClassNotFoundException, IllegalAccessException, InstantiationException, GeneralSecurityException, IOException, NoSuchMethodException, InvocationTargetException {

        String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        String appConfigPath = rootPath + "appSettings.properties";
        Properties appSettings = new Properties();
        appSettings.load(new FileInputStream(appConfigPath));
        // override appSettings values from appSettings.properties with Env vars, if they exist
        System.getenv().forEach((k, v) -> {
            Object overriddenProp;
            //System.out.println(k + ":" + v);
            overriddenProp = appSettings.setProperty(k,v);
            if (overriddenProp!=null) {
                System.out.println("replacing ["+k+"] with value ["+v+"] (old value : "+overriddenProp.toString()+")");
            }
        });

        String appEnv = appSettings.getProperty("application.environment");
        setApplicationEnvironment(appEnv);
        _logger.info("***** setting application environment = " + appEnv);

        switch (applicationEnvironment) {
            case "dev":
                _DevEnv = true;
                break;
            case "int":
                _IntEnv = true;
                break;
            case "prod":
                _ProdEnv = true;
                break;
        }

        setFineImagesDirectory(appSettings.getProperty("fineImagesDirectory"));
        _logger.info("***** fine images directory = " + appSettings.getProperty("fineImagesDirectory"));

        setLogosScriptPath(appSettings.getProperty("logosScriptPath"));
        _logger.info("***** logosScriptPath = " + appSettings.getProperty("logosScriptPath"));

        setLogosOverlayPortraitFile(appSettings.getProperty("logosOverlayPortraitFile"));
        _logger.info("***** logosOverlayPortraitFile = " + appSettings.getProperty("logosOverlayPortraitFile"));

        setLogosOverlayLandscapeFile(appSettings.getProperty("logosOverlayLandscapeFile"));
        _logger.info("***** logosOverlayLandscapeFile = " + appSettings.getProperty("logosOverlayLandscapeFile"));

        setApplyLogoOrWatermarkOnFineImage("true".equals(appSettings.getProperty("applyLogoOrWatermarkOnFineImage")));
        _logger.info("***** applyLogoOrWatermarkOnFineImage = " + appSettings.getProperty("applyLogoOrWatermarkOnFineImage"));

        setLogoImageFile(appSettings.getProperty("logoImageFile"));
        _logger.info("***** logoImageFile = " + appSettings.getProperty("logoImageFile"));

        setSponsorBarFile(appSettings.getProperty("sponsorBarFile"));
        _logger.info("***** sponsorBarFile = " + appSettings.getProperty("sponsorBarFile"));

        setDemoOrderMode("true".equals(appSettings.getProperty("demoOrder")));
        _logger.info("***** demo order flag = " + appSettings.getProperty("demoOrder"));
        setSimulateOrderOnly("true".equals(appSettings.getProperty("simulateOrderOnly")));
        _logger.info("***** simulate order only flag= " + appSettings.getProperty("simulateOrderOnly"));


        /******************* AWS SQS Settings ******************/
        _logger.info("Setting S3 SQS import queue name :" + appSettings.getProperty("sqsQueueName"));
        amazonSqsQueueName = appSettings.getProperty("sqsQueueName"); // must be set before instantiation of fileStorageProvider class

        /******************* Storage Provider / AWS S3 Settings ******************/
        _logger.info("Setting S3 bucket name :" + appSettings.getProperty("awsS3BucketNameFrankfurt"));
        s3BucketName = appSettings.getProperty("awsS3BucketNameFrankfurt"); // must be set before instantiation of fileStorageProvider class
        // s3BucketNameIreland = appSettings.getProperty("awsS3BucketNameIreland"); // must be set before instantiation of fileStorageProvider class

        _logger.info("Setting FileStorageProvider implementation :" + appSettings.getProperty("fileStorageProviderImplementation"));
        fileStorageProvider = (FileStorageProviderInterface) Class.forName(appSettings.getProperty("fileStorageProviderImplementation")).getDeclaredConstructor().newInstance();
        /************************************************************************/



        _logger.info("***** imgixSignKey = " + appSettings.getProperty("imgixSignKey"));
        imgixSignKey=appSettings.getProperty("imgixSignKey");
        _logger.info("***** imgixSignKey2 = " + appSettings.getProperty("imgixSignKey2"));
        imgixSignKey2=appSettings.getProperty("imgixSignKey2");




    }


    public static String getFinePath()
    {
        return finePath;
    }


    public static void setLogosScriptPath(String logosScriptPath) {
        Registry.logosScriptPath = logosScriptPath;
    }

    public static String getLogosScriptPath() {
        return logosScriptPath;
    }


    public static int getItemsOnPage()
    {
        return itemsOnPage;
    }



    /**
     * return true if the demo account at colorplaza shall be used
     * @return true or false
     */
    public static boolean isDemoOrderMode()
    {
        return demoOrderMode;
    }

    public static void setDemoOrderMode(boolean demoOrderMode)
    {
        Registry.demoOrderMode = demoOrderMode;
    }

    /**
     * @return true if the order shall only be simulated and no order-interface shall be contacted at all
     */
    public static boolean isSimulateOrderOnly()
    {
        return simulateOrderOnly;
    }

    public static void setSimulateOrderOnly(boolean simulateOrderOnly)
    {
        Registry.simulateOrderOnly = simulateOrderOnly;
    }

    public static String getFineImagesDirectory()
    {
        return fineImagesDirectory;
    }

    public static void setFineImagesDirectory(String fineImagesDirectory)
    {
        Registry.fineImagesDirectory = fineImagesDirectory;
    }

    public static FileStorageProviderInterface getFileStorageProvider() {
        return fileStorageProvider;
    }


    public static void setLogosOverlayPortraitFile(String logosOverlayPortraitFile) {
        Registry.logosOverlayPortraitFile = logosOverlayPortraitFile;
    }

    public static String getLogosOverlayPortraitFile() {
        return logosOverlayPortraitFile;
    }


    public static void setLogosOverlayLandscapeFile(String logosOverlayLandscapeFile) {
        Registry.logosOverlayLandscapeFile = logosOverlayLandscapeFile;
    }

    public static String getLogosOverlayLandscapeFile() {
        return logosOverlayLandscapeFile;
    }

    public static boolean getApplyLogoOrWatermarkOnFineImage() {
        return applyLogoOrWatermarkOnFineImage;
    }

    public static boolean isApplyLogoOrWatermarkOnFineImage() {
        return applyLogoOrWatermarkOnFineImage;
    }

    public static void setApplyLogoOrWatermarkOnFineImage(boolean applyLogoOrWatermarkOnFineImage) {
        Registry.applyLogoOrWatermarkOnFineImage = applyLogoOrWatermarkOnFineImage;
    }

    public static String getS3BucketName() {
        return s3BucketName;
    }

    public static String getSponsorBarFile() {
        return sponsorBarFile;
    }

    public static void setSponsorBarFile(String sponsorBarFile) {
        Registry.sponsorBarFile = sponsorBarFile;
    }

    public static String getLogoImageFile() {
        return logoImageFile;
    }

    public static void setLogoImageFile(String logoImageFile) {
        Registry.logoImageFile = logoImageFile;
    }


    public static void setApplicationEnvironment(String appEnv) {
        Registry.applicationEnvironment = appEnv;
    }

    /**
     * private access only - use the is*Env methods
     * @return
     */
    public static String getApplicationEnvironment() {
        return applicationEnvironment;
    }

    public static String getImgixSignKey() {
        return imgixSignKey;
    }

    public static String getImgixSignKey2() {
        return imgixSignKey2;
    }

    /**
     * used for templates to set tiles according to environment
     * @return
     */
    public static boolean isDevEnv() {
        return getApplicationEnvironment().equals("dev");
    }

    /**
     * used for templates to set tiles according to environment
     * @return
     */
    public static boolean isIntEnv() {
        return getApplicationEnvironment().equals("int");
    }

    /**
     * used for templates to set tiles according to environment
     * @return
     */
    public static boolean isProdEnv() {
        return getApplicationEnvironment().equals("prod");
    }

    /**
     * the Amazon SQS queue name - should be different for each installation
     * @return
     */
    public static String getAmazonSqsQueueName() {
        return amazonSqsQueueName;
    }
}
