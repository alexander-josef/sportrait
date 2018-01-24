/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since Oct 6, 2005$
 *
 * Copyright (c) 2005 unartig AG  --  All rights reserved
 *
 * STATUS  :
 *    $Revision$, $State$, $Name$
 *
 *    $Author$, $Locker$
 *    $Date$
 *
 *************************************************
 * $Log$
 * Revision 1.5  2007/06/09 11:15:37  alex
 * photographer
 *
 * Revision 1.4  2007/05/03 16:59:39  alex
 * set photos on overview to 16 (2 rows)
 *
 * Revision 1.3  2007/04/17 11:03:27  alex
 * dynamic pager added
 *
 * Revision 1.2  2007/03/27 16:39:17  alex
 * refactored studioalbum into album
 *
 * Revision 1.1  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.51  2006/11/12 11:58:47  alex
 * dynamic album ads
 *
 * Revision 1.50  2006/10/28 21:57:09  alex
 * reformat
 *
 * Revision 1.49  2006/10/17 08:07:07  alex
 * creating the order hashes
 *
 * Revision 1.48  2006/08/25 23:27:58  alex
 * payment i18n
 *
 * Revision 1.47  2006/05/04 18:01:39  alex
 * new param max entries
 *
 * Revision 1.46  2006/04/30 16:21:27  alex
 * removing system.outs
 *
 * Revision 1.45  2006/04/29 23:32:07  alex
 * many sola features, bugs, hibernate config
 *
 * Revision 1.44  2006/04/19 21:31:53  alex
 * session will be restored with album-bean (i.e. for bookmarked urls or so...)
 *
 * Revision 1.43  2006/03/20 15:33:33  alex
 * first check in for new sports album logic and db changes
 *
 * Revision 1.42  2006/02/28 14:57:46  alex
 * added more resources (email for order confirmation), small fixes
 *
 * Revision 1.41  2006/02/22 17:08:56  alex
 * jumping forward and jumping back x-pages works
 *
 * Revision 1.40  2006/02/22 14:00:51  alex
 * new album nav concept works also in display
 *
 * Revision 1.39  2006/02/13 16:15:28  alex
 * but [968]
 *
 * Revision 1.38  2006/02/07 14:48:53  alex
 * bug 820 and minor refactorings
 *
 * Revision 1.37  2006/01/10 15:44:56  alex
 * vm1 config files, new property "simulateOrderOnly"
 *
 * Revision 1.36  2005/12/02 23:13:53  alex
 * change log for colorcorrectino to info
 *
 * Revision 1.35  2005/12/02 21:48:34  alex
 * order process bug fix, color correction on
 *
 * Revision 1.34  2005/11/25 13:22:24  alex
 * resources
 *
 * Revision 1.33  2005/11/25 10:56:58  alex
 *
 * Revision 1.32  2005/11/23 20:52:10  alex
 * bug-fixes
 *
 * Revision 1.31  2005/11/22 19:45:46  alex
 * admin actions, configurations
 *
 * Revision 1.30  2005/11/21 17:52:59  alex
 * no account action , photo order
 *
 * Revision 1.29  2005/11/19 22:04:04  alex
 * shopping cart reflects different price segments
 *
 * Revision 1.28  2005/11/19 16:31:43  alex
 * bookmarks of displays should work now
 *
 * Revision 1.27  2005/11/18 19:15:52  alex
 * stuff ...
 *
 * Revision 1.26  2005/11/18 11:10:22  alex
 * customer service message
 *
 * Revision 1.25  2005/11/16 14:26:49  alex
 * validator works for email, new library
 *
 * Revision 1.24  2005/11/14 10:43:34  alex
 * shopping cart basic functions work. photo list needs a bit more work yet
 *
 * Revision 1.23  2005/11/12 23:15:27  alex
 * using indexed properties ... first step
 *
 * Revision 1.22  2005/11/09 15:48:16  alex
 * check out wizard
 *
 * Revision 1.21  2005/11/09 09:01:29  alex
 * check out form wizard
 *
 * Revision 1.20  2005/11/08 13:22:58  alex
 * rename tree items. tree items now not in cvs ... generated at startup time
 *
 * Revision 1.19  2005/11/08 11:03:20  alex
 * nothing
 *
 * Revision 1.18  2005/11/08 10:05:02  alex
 * tree items i18n, backend
 *
 * Revision 1.17  2005/11/07 17:38:26  alex
 * admin interface refactored
 *
 * Revision 1.16  2005/11/06 21:43:22  alex
 * overview, admin menu, index-photo upload
 *
 * Revision 1.15  2005/11/05 21:41:58  alex
 * overview und links in tree menu
 *
 * Revision 1.14  2005/11/05 16:37:25  alex
 * tiles error, more sc stuff
 *
 * Revision 1.13  2005/11/05 16:00:41  alex
 * tiles error, more sc stuff
 *
 * Revision 1.12  2005/11/05 14:57:08  alex
 * images small correction
 *
 * Revision 1.11  2005/11/05 10:32:14  alex
 * shopping cart and minor problems, exception handling
 *
 * Revision 1.10  2005/11/04 23:02:54  alex
 * shopping cart session
 *
 * Revision 1.9  2005/11/04 17:12:18  alex
 * tree refactoring
 *
 * Revision 1.8  2005/11/01 11:28:39  alex
 * pagination works; put logic in overview bean
 *
 * Revision 1.7  2005/10/28 10:00:20  alex
 * layout changes
 *
 * Revision 1.6  2005/10/27 20:21:39  alex
 * album overview
 *
 * Revision 1.5  2005/10/26 15:36:44  alex
 * some fixes
 *
 * Revision 1.4  2005/10/26 14:34:32  alex
 * first version of album overview
 * new mappings in struts for the /album/** url
 *
 * Revision 1.3  2005/10/24 13:50:07  alex
 * upload of album
 * import in db 
 * processing of images
 *
 * Revision 1.2  2005/10/21 13:02:10  alex
 * introducing i18n for text and images
 *
 * Revision 1.1  2005/10/06 18:14:23  alex
 * saving new tree_items file
 *
 ****************************************************************/
package ch.unartig.studioserver;

import ch.unartig.studioserver.storageProvider.AwsS3FileStorageProvider;
import ch.unartig.studioserver.storageProvider.FileStorageProviderInterface;
import ch.unartig.studioserver.storageProvider.LocalFileSystemStorageProvider;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import org.apache.log4j.Logger;
import org.apache.struts.util.MessageResources;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Global Registry class for constants and fields from properties file
 */
public final class Registry
{

    public static Logger _logger = Logger.getLogger("ch.unartig.studioserver.Registry");

    public static final String _SESSION_CLIENT_NAME = "clientInSession";
    public static final String _ALBUM_ID_NAME = "albumId";
    public static final String _LANDSCAPE_MODE_SUFFIX = "landscape";

// todo: move to appSettings
//read from prop-file
    private static String modelPackageName = "ch.unartig.studioserver.model.";
    public static String frontendDirectory = "";
    private static String projectName;
    private static String projectVersion;
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
    private static String treeItemsFilePrefix = "tree_items_";
    // todo confusing: distinguish between the serverFinePath and the fine directory
    private static String finePath = "fine/"; // used in all storage providers as path denominator
    private static String thumbnailPath = "thumbnail/"; // used in all storage providers as path denominator
    private static String displayPath = "display/"; // used in all storage providers as path denominator
    public static final String _PORTRAIT_MODE_SUFFIX = "portrait";
    private static Integer displayPixelsLongerSide = 380; // used to be 484 for unartig.ch and the beginning of sportrait
    private static Integer thumbnailPixelsLongerSide = 100;
    /*the number of thumbnail photos on the preview page*/
    public static int itemsOnPage = 14;

    /*this addess will be used for sending a request from the contact form to fogbugz  */
    private static String customerServiceAddress = "info@sportrait.com";
    /*this is the sender for the confirmation email after an order has been confirmed*/
    private static String orderConfirmationFromAddress = "info@sportrait.com";


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
    private static String mailFromAddress = "info@unartig.ch";
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
    public static final int _DEFAULT_PHOTOPOINT_TOLERANCE_SECONDS = 5;
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


    /**
     * class won't be instantiated, hence private constructor
     */
    private Registry()
    {
    }


    /**
     * will be called upon startup of the Action servlet
     */
    public static void init() throws ClassNotFoundException, IllegalAccessException, InstantiationException, GeneralSecurityException, IOException {

        MessageResources appSettings = MessageResources.getMessageResources("appSettings");

//        setFrontendDirectory(appSettings.getMessage("frontendDirectory"));
//        _logger.info("***** frontend directory = " + appSettings.getMessage("frontendDirectory"));

        setApplicationEnvironment(appSettings.getMessage("application.environment"));
        _logger.info("***** application environment = " + appSettings.getMessage("application.environment"));

        setFineImagesDirectory(appSettings.getMessage("fineImagesDirectory"));
        _logger.info("***** fine images directory = " + appSettings.getMessage("fineImagesDirectory"));

        setWebImagesDocumentRoot(appSettings.getMessage("webImagesDocumentRoot"));
        _logger.info("***** web-images document Root = " + appSettings.getMessage("webImagesDocumentRoot"));

        setWebImagesContext(appSettings.getMessage("webImagesContext"));
        _logger.info("***** web-images context = " + appSettings.getMessage("webImagesContext"));

        setLogosScriptPath(appSettings.getMessage("logosScriptPath"));
        _logger.info("***** logosScriptPath = " + appSettings.getMessage("logosScriptPath"));

        setLogosOverlayPortraitFile(appSettings.getMessage("logosOverlayPortraitFile"));
        _logger.info("***** logosOverlayPortraitFile = " + appSettings.getMessage("logosOverlayPortraitFile"));

        setLogosOverlayLandscapeFile(appSettings.getMessage("logosOverlayLandscapeFile"));
        _logger.info("***** logosOverlayLandscapeFile = " + appSettings.getMessage("logosOverlayLandscapeFile"));

        setApplyLogoOrWatermarkOnFineImage("true".equals(appSettings.getMessage("applyLogoOrWatermarkOnFineImage")));
        _logger.info("***** applyLogoOrWatermarkOnFineImage = " + appSettings.getMessage("applyLogoOrWatermarkOnFineImage"));

        setLogoImageFile(appSettings.getMessage("logoImageFile"));
        _logger.info("***** logoImageFile = " + appSettings.getMessage("logoImageFile"));

        setSponsorBarFile(appSettings.getMessage("sponsorBarFile"));
        _logger.info("***** sponsorBarFile = " + appSettings.getMessage("sponsorBarFile"));


        setProjectName(appSettings.getMessage("application.name"));
        setProjectVersion(appSettings.getMessage("application.version"));
        // todo set to enabled again after svn problem on test server is solved
//        setBuildNumber(appSettings.getMessage("application.buildNumber"));

        setMailHost(appSettings.getMessage("mailHost"));
        _logger.info("***** Mail Host = " + appSettings.getMessage("mailHost"));
        setDemoOrderMode("true".equals(appSettings.getMessage("demoOrder")));
        _logger.info("***** demo order flag = " + appSettings.getMessage("demoOrder"));
        setSimulateOrderOnly("true".equals(appSettings.getMessage("simulateOrderOnly")));
        _logger.info("***** simulate order only flag= " + appSettings.getMessage("simulateOrderOnly"));


        String oipsOrderPeriod = appSettings.getMessage("oipsOrderPeriod");
        if (oipsOrderPeriod != null && Long.parseLong(oipsOrderPeriod) > 0) {
            setOipsOrderPeriod(Long.parseLong(oipsOrderPeriod));
        }

        _logger.debug("getMessage oipsColorcorrection: " + appSettings.getMessage("oipsColorcorrection"));
        String oipsColorcorrection = appSettings.getMessage("oipsColorcorrection");
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


        /******************* Storage Provider / AWS S3 Settings ******************/
        _logger.info("Setting S3 bucket name :" + appSettings.getMessage("awsS3BucketName"));
        s3BucketName = appSettings.getMessage("awsS3BucketName"); // must be set before instantiation of fileStorageProvider class

        _logger.info("Setting FileStorageProvider implementation :" + appSettings.getMessage("fileStorageProviderImplementation"));
        fileStorageProvider = (FileStorageProviderInterface) Class.forName(appSettings.getMessage("fileStorageProviderImplementation")).newInstance();
        /************************************************************************/

        // Set up the HTTP transport and JSON factory for the google sign-in actions
        setGoogleJasonFactory(Utils.getDefaultJsonFactory());
        setGoogleHttpTransport(GoogleNetHttpTransport.newTrustedTransport());


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

    public static String getTreeItemsFilePrefix()
    {
        return treeItemsFilePrefix;
    }

    public static void setTreeItemsFilePrefix(String treeItemsFilePrefix)
    {
        Registry.treeItemsFilePrefix = treeItemsFilePrefix;
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

    public static void setMailFromAddress(String mailFromAddress)
    {
        Registry.mailFromAddress = mailFromAddress;
    }

    public static String getCustomerServiceAddress()
    {
        return customerServiceAddress;
    }

    public static int getItemsOnPage()
    {
        return itemsOnPage;
    }

    public static void setItemsOnPage(int itemsOnPage)
    {
        Registry.itemsOnPage = itemsOnPage;
    }

    public static String getFrontendDirectory()
    {
        return frontendDirectory;
    }

    public static void setFrontendDirectory(String frontendDirectory)
    {
        Registry.frontendDirectory = frontendDirectory;
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

    public static void setApplicationEnvironment(String applicationEnvironment) {
        Registry.applicationEnvironment = applicationEnvironment;
    }

    public static String getApplicationEnvironment() {
        return applicationEnvironment;
    }
}
