package ch.unartig.studioserver;

import ch.unartig.studioserver.storageProvider.FileStorageProviderInterface;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.Properties;

/**
 * Global Registry class for constants and fields from properties file
 */
public final class Registry
{

    public static Logger _logger = Logger.getLogger("ch.unartig.studioserver.Registry");

    public static final String _SESSION_CLIENT_NAME = "clientInSession";
    public static final String _ALBUM_ID_NAME = "albumId";
    public static final String _LANDSCAPE_MODE_SUFFIX = "landscape";
    public static  boolean _DevEnv = false; // constant variable to indicate if we're in dev environment - initializes to false, will be set to true for dev env / used in JSPa
    public static  boolean _IntEnv = false; // constant variable to indicate if we're in int environment - initializes to false, will be set to true for dev env / used in JSPa
    public static  boolean _ProdEnv = false; // constant variable to indicate if we're in prod environment - initializes to false, will be set to true for dev env / used in JSPa

    // todo: move to appSettings
    public static final Regions AWS_FRANKFURT_REGION = Regions.EU_CENTRAL_1;
    public final static Region awsRegionFrankfurt = Region.getRegion(AWS_FRANKFURT_REGION); // Frankfurt - used for bucket URLs in pre-image-service configuration - conflict with EU-WEST-1 buckets and services?
    public static final Regions SPORTRAIT_AWS_DEFAULT_REGION = AWS_FRANKFURT_REGION; // Ireland - used for bucket URLs after image recognition, used for queues
//read from prop-file
    private static String modelPackageName = "ch.unartig.studioserver.model.";
    public static String frontendDirectory = "";
    private static String projectName="not set";
    private static String projectVersion="not set";
    private static String buildNumber = "99999";

    // Where the fine images are located. No document root, not accessible by a web server.
    // example: /Users/alexanderjosef/DEV/sportrait-web/fine-images
    private static String fineImagesDirectory;
    // We need the document root and the context for web-images! document root = /opt/DATA/sportrait/web-images ; context web-images
    private static String webImagesDocumentRoot;
    /*We usually define a different context for web images. This is the first part of the path part in the URL after the host and port (until the second '/')*/
    private static String webImagesContext;
    /* Path to the script that put the SOLA logo over the images */
    private static String logosScriptPath;
    public static String jsDirectory = "js/";
    public static String jsTreeDirectory = "js/tree/";
    /*the tree items file name without the language dependant suffix*/
    private static final String treeItemsFilePrefix = "tree_items_";
    // todo confusing: distinguish between the serverFinePath and the fine directory
    private static final String finePath = "fine/"; // used in all storage providers as path denominator
    private static final String thumbnailPath = "thumbnail/"; // used in all storage providers as path denominator
    private static final String displayPath = "display/"; // used in all storage providers as path denominator
    public static final String _PORTRAIT_MODE_SUFFIX = "portrait";
    private static final Integer displayPixelsLongerSide = 380; // used to be 484 for unartig.ch and the beginning of sportrait
    private static final Integer thumbnailPixelsLongerSide = 100;
    /*the number of thumbnail photos on the preview page*/
    public static int itemsOnPage = 14;

    /*this addess will be used for sending a request from the contact form to fogbugz  */
    private static final String customerServiceAddress = "info@sportrait.com";
    /*this is the sender for the confirmation email after an order has been confirmed*/
    private static final String orderConfirmationFromAddress = "info@sportrait.com";


    public static final float _IMAGE_QUALITY_STANDARD = 0.75F; // used for saving thumbnail and display JPGs
    public static final float _IMAGE_QUALITY_FINE = 0.96F; // used for logo montage on fine images (should result in similar file size as originals)
    public static final float _SHARP_FACTOR_STANDARD = 0.5F;
    public static float _ImageSharpFactor = _SHARP_FACTOR_STANDARD;

    public static long oipsOrderPeriod;



    public static FileStorageProviderInterface fileStorageProvider; // Implementation of the configured file storage provider class (either local file storage or AWS S3

    public static final String _STRATEGY_IMPL_PACKAGE = "ch.unartig.studioserver.businesslogic.";
    public static final String _POPULATOR_STRATEGY_SUFFIX = "PopulatorImpl";
    public static final String _POPULATOR_TYPE_NOTIME_PARAM = "notime";
    public static final String _POPULATOR_TYPE_TIME_PARAM = "time";
    public static final String _POPULATOR_TYPE_NOTIME_CLASS_PREFIX = "NoTime";
    public static final String _POPULATOR_TYPE_TIMED_CLASS_PREFIX = "Timed";
    // todo: change to p?
    public static final String _PAGE_PARAM_NAME = "page";
    // todo path needs to be adjusted to context 
    public static final String _TREE_ICONS_PATH = "/images/tree_icons/";
    /*Item Scope Settings Parament*/
    public static final String _ICON_FOR_ITEM = "_closed";
    public static final String _ICON_FOR_SELECTED_ITEM = "_open";
    public static final String _ICON_FOR_OPENED_ITEM = "_open";
    public static final String _ICON_FOR_SELECTED_OPEN_ITEM = "_open";
    public static final String _ICON_FOR_ITEM_MOUSE_OVER = "_hover";
    public static final String _ICON_FOR_SELECTED_ITEM_MOUSE_OVER = "_hover";
    public static final String _ICON_FOR_OPENED_ITEM_MOUSE_OVER = "_hover";
    public static final String _ICON_FOR_SELECTED_OPENED_ITEM_MOUSE_OVER = "_hover";
    public static final String _PSEUDO_ROOT_CLASS = "pseudo_root";
    /*Shopping Cart and Order Settings*/
    public static final String _NAME_SHOPPING_CART_SESSION = "shoppingCart";
    public static final String _NAME_PHOTO_PARAM = "photo";
    public static final String _NAME_SHOPPING_CART_ATTR = "sc";
    public static final String _NAME_ORDERED_PHOTO_ID_PARAM = "orderedPhotoId";
    public static final String _NAME_ALBUM_BEAN_ATTR = "albumBean";
    /*album overview param*/
    public static final String _NAME_HOUR_PARAM = "hour";
    public static final String _NAME_PAGE_PARAM = "page";
    public static final String _NAME_MINUTES_PARAM = "minutes";
    public static final String _NAME_POPULATOR_TYPE_PARAM = "type";
    public static final int _MAX_ENTRIES_FOR_OVERVIEW = 20;
    /*level overview constants*/
    public static final String _LEVEL_INDEX_IMAGE_NAME = "index.jpg";
     /**/
    public static final String[] _LANG_SUFFIXES = {"de", "fr", "en"};
    public static final String _JAVA_SCRIPT_SUFFIX = ".js";
    public static final String _NAME_USER_ACCOUNT_ID_SESSION = "userAccountId";
    public static final String _NAME_PRODUCT_LIST3_ATTR = "products3";
    public static final String _NAME_PRODUCT_LIST5_ATTR = "products5";
    public static final int _NUMBER_OF_SC_ITEMS_PER_PHOTO = 4;
    /**
     * the mail host is used to send out confirmation emails etc.
     */
    public static final String _MAIL_HOST = "localhost";
    public static String _mailHost = _MAIL_HOST;
    private static final String mailFromAddress = "info@unartig.ch";
    public static final String _GENDER_MALE_CODE = "m";
    /**
     * time-interval in minutes to be shown in overview
     *
     */
    public static final int _ALBUM_TIME_INTERVAL = 5;
    public static final String _NAME_CUSTOMER_ID_SESSION_ATTR = "customerId";
    public static final String _NAME_PHOTO_ORDER_SESSION_ATTR = "photoOrder";
    private static boolean demoOrderMode = false;
    private static String oipsColorcorrection;
    private static boolean simulateOrderOnly = false;
    public static final String _NAME_SPORTSALBUM_LEVEL_TYPE = "SportsAlbum";
    public static final String _NAME_ALBUM_LEVEL_TYPE = "Album";
    public static final String _NAME_SPORTSEVENT_LEVEL_TYPE = "SportsEvent" ;
    public static final String _NAME_EVENT_LEVEL_TYPE = "Event";
    public static final String _NAME_EVENTGROUP_LEVEL_TYPE = "EventGroup";
    public static final String _NAME_ALBUM_TYPE_PARAM = "type";
    /**
    *constant to use when jumping forward or backward in the album using the >| or |< buttons
    */
    public static final int _JUMP_BACK_FORWARD_PAGE_VALUE = 10;
    // public static final int _DEFAULT_PHOTOPOINT_TOLERANCE_SECONDS = 5;
    public static final String _SWITZERLAND_COUNTRY_CODE = "CHE";
    public static final String _GERMANY_COUNTRY_CODE = "DEU";
    public static final String _AUSTRIA_COUNTRY_CODE = "AUT";
    public static final String _CURRENCY_SWISS_FRANCS = "SFr.";
    public static final String _CURRENCY_EURO = "�";
    public static final int _ORDER_DOWNLOADS_VALID_DAYS = 7;
    public static final String _AD_BANNER_RIGHT_POSITION = "right";
    public static final String _ROLE_PHOTOGRAPHER = "photographer";
    public static final String _ROLE_UNARTIGADMIN = "unartigadmin";
    private static String logosOverlayPortraitFile;
    private static String logosOverlayLandscapeFile;
    private static boolean applyLogoOrWatermarkOnFineImage;
    private static String s3BucketName;
    private static String sponsorBarFile; // full path to image that contains sponsor bar that will be copied over image
    private static String logoImageFile; // full path of logo image file that will be copied over the image on the upper left
    private static JsonFactory googleJasonFactory;
    private static NetHttpTransport googleHttpTransport;
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

        // todo: replace this old MessageResource class with something else in order to read out values from keys in properties files.
        // do we need to replace all ".getMessage" calls?
        // maybe better put the configuration as environment variables? how ?
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + "appSettings.properties";
        Properties appSettings = new Properties();
        appSettings.load(new FileInputStream(appConfigPath));

//        MessageResources appSettings = MessageResources.getMessageResources("appSettings");

//        setFrontendDirectory(appSettings.getMessage("frontendDirectory"));
//        _logger.info("***** frontend directory = " + appSettings.getMessage("frontendDirectory"));

        String appEnv = appSettings.getProperty("application.environment");
        setApplicationEnvironment(appEnv);
        _logger.info("***** setting application environment = " + appEnv);

        // String appEnv = appSettings.getMessage("application.environment");
        // setApplicationEnvironment(appEnv);
        // _logger.info("***** setting application environment = " + appEnv);
        // constants needed for jsp EL if conditions
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

        setWebImagesDocumentRoot(appSettings.getProperty("webImagesDocumentRoot"));
        _logger.info("***** web-images document Root = " + appSettings.getProperty("webImagesDocumentRoot"));

        setWebImagesContext(appSettings.getProperty("webImagesContext"));
        _logger.info("***** web-images context = " + appSettings.getProperty("webImagesContext"));

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


        // setProjectName(appSettings.getMessage("application.name"));
        // setProjectVersion(appSettings.getMessage("application.version"));
        // todo: set to enabled again after svn problem on test server is solved
//        setBuildNumber(appSettings.getMessage("application.buildNumber"));

        setMailHost(appSettings.getProperty("mailHost"));
        _logger.info("***** Mail Host = " + appSettings.getProperty("mailHost"));
        setDemoOrderMode("true".equals(appSettings.getProperty("demoOrder")));
        _logger.info("***** demo order flag = " + appSettings.getProperty("demoOrder"));
        setSimulateOrderOnly("true".equals(appSettings.getProperty("simulateOrderOnly")));
        _logger.info("***** simulate order only flag= " + appSettings.getProperty("simulateOrderOnly"));


        String oipsOrderPeriod = appSettings.getProperty("oipsOrderPeriod");
        if (oipsOrderPeriod != null && Long.parseLong(oipsOrderPeriod) > 0) {
            setOipsOrderPeriod(Long.parseLong(oipsOrderPeriod));
        }

        _logger.debug("getProperty oipsColorcorrection: " + appSettings.getProperty("oipsColorcorrection"));
        String oipsColorcorrection = appSettings.getProperty("oipsColorcorrection");
        if ("false".equals(oipsColorcorrection)) {
            setOipsColorcorrection("0"); // "0" stand for no colorcorrecion
            _logger.info("**********************************************************");
            _logger.info("getOipsColorcorrection() = " + getOipsColorcorrection());
            _logger.info("**********************************************************");
        } else {
            setOipsColorcorrection("1"); // "1" stands for colorcorrection enabled (default)
            _logger.info("**********************************************************");
            _logger.info("getOipsColorcorrection() = " + getOipsColorcorrection());
            _logger.info("**********************************************************");
        }


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

        // Set up the HTTP transport and JSON factory for the google sign-in actions
        setGoogleJasonFactory(Utils.getDefaultJsonFactory());
        setGoogleHttpTransport(GoogleNetHttpTransport.newTrustedTransport());

        _logger.info("***** imgixSignKey = " + appSettings.getProperty("imgixSignKey"));
        imgixSignKey=appSettings.getProperty("imgixSignKey");
        _logger.info("***** imgixSignKey2 = " + appSettings.getProperty("imgixSignKey2"));
        imgixSignKey2=appSettings.getProperty("imgixSignKey2");




    }


    public static String getFinePath()
    {
        return finePath;
    }

    public static Integer getDisplayPixelsLongerSide()
    {
        return displayPixelsLongerSide;
    }

    public static Integer getThumbnailPixelsLongerSide()
    {
        return thumbnailPixelsLongerSide;
    }

    public static String getThumbnailPath()
    {
        return thumbnailPath;
    }

    public static String getDisplayPath()
    {
        return displayPath;
    }

    /**
     * The (local) absolute path on the server underneath the webimages are served. Usually in a different virtual host or context.
     * example: "/Users/alexanderjosef/DEV/sportrait-web/fine-images"
     * @return
     */
    public static String getWebImagesDocumentRoot()
    {
        return webImagesDocumentRoot;
    }

    public static void setWebImagesDocumentRoot(String webImagesDocumentRoot)
    {
        Registry.webImagesDocumentRoot = webImagesDocumentRoot;
    }

    public static String getModelPackageName()
    {
        return modelPackageName;
    }

    public static void setModelPackageName(String modelPackageName)
    {
        Registry.modelPackageName = modelPackageName;
    }

    public static void setLogosScriptPath(String logosScriptPath) {
        Registry.logosScriptPath = logosScriptPath;
    }

    public static String getLogosScriptPath() {
        return logosScriptPath;
    }

    public static String getMailHost()
    {
        return _mailHost;
    }

    public static void setMailHost(String _mailHost)
    {
        Registry._mailHost = _mailHost;
    }

    public static String getMailFromAddress()
    {
        return mailFromAddress;
    }

    public static String getCustomerServiceAddress()
    {
        return customerServiceAddress;
    }

    public static int getItemsOnPage()
    {
        return itemsOnPage;
    }


    public static String getFrontendDirectory()
    {
        return frontendDirectory;
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

    public static long getOipsOrderPeriod()
    {
        return oipsOrderPeriod;
    }

    public static void setOipsOrderPeriod(long oipsOrderPeriod)
    {
        Registry.oipsOrderPeriod = oipsOrderPeriod;
    }

    public static String getOipsColorcorrection()
    {
        return oipsColorcorrection;
    }

    public static void setOipsColorcorrection(String oipsColorrection)
    {
        oipsColorcorrection = oipsColorrection;
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

    public static String getOrderConfirmationFromAddress()
    {
        return orderConfirmationFromAddress;
    }

    public static String getWebImagesContext()
    {
        return webImagesContext;
    }

    public static void setWebImagesContext(String webImagesContext)
    {
        Registry.webImagesContext = webImagesContext;
    }

    public static String getFineImagesDirectory()
    {
        return fineImagesDirectory;
    }

    public static void setFineImagesDirectory(String fineImagesDirectory)
    {
        Registry.fineImagesDirectory = fineImagesDirectory;
    }

    public static void setProjectName(String projectName) {
        Registry.projectName = projectName;
    }

    public static void setProjectVersion(String projectVersion) {
        Registry.projectVersion = projectVersion;
    }

    public static String getProjectName() {
        return projectName;
    }

    public static String getProjectVersion() {
        return projectVersion;
    }

    public static void setBuildNumber(String buildNumber) {
        Registry.buildNumber = buildNumber;
    }

    public static String getBuildNumber() {
        return buildNumber;
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

    public static void setGoogleJasonFactory(JsonFactory googleJasonFactory) {
        Registry.googleJasonFactory = googleJasonFactory;
    }

    public static JsonFactory getGoogleJasonFactory() {
        return googleJasonFactory;
    }

    public static void setGoogleHttpTransport(NetHttpTransport googleHttpTransport) {
        Registry.googleHttpTransport = googleHttpTransport;
    }

    public static NetHttpTransport getGoogleHttpTransport() {
        return googleHttpTransport;
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
