/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Oct 24, 2005$
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
 * Revision 1.2  2007/03/27 16:39:17  alex
 * refactored studioalbum into album
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.12  2006/10/28 21:57:09  alex
 * reformat
 *
 * Revision 1.11  2006/04/30 16:21:27  alex
 * removing system.outs
 *
 * Revision 1.10  2006/04/06 18:31:22  alex
 * display fixed for sports album
 *
 * Revision 1.9  2006/02/22 14:00:51  alex
 * new album nav concept works also in display
 *
 * Revision 1.8  2005/11/27 19:39:10  alex
 * fast upload
 *
 * Revision 1.7  2005/11/21 17:52:59  alex
 * no account action , photo order
 *
 * Revision 1.6  2005/11/19 16:31:43  alex
 * bookmarks of displays should work now
 *
 * Revision 1.5  2005/10/27 20:21:39  alex
 * album overview
 *
 * Revision 1.4  2005/10/26 20:40:12  alex
 * first view impl
 *
 * Revision 1.3  2005/10/24 13:50:07  alex
 * upload of album
 * import in db 
 * processing of images
 *
 ****************************************************************/
package ch.unartig.studioserver.model;

import ch.unartig.sportrait.imgRecognition.Startnumber;
import ch.unartig.sportrait.imgRecognition.Test;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.imaging.ImagingHelper;
import ch.unartig.studioserver.storageProvider.FileStorageProviderInterface;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "photos")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Photo implements java.io.Serializable {
    // todo: store thumbnail and display width and height pixels . here? globally?

    @Transient
    Logger _logger = Logger.getLogger(getClass().getName());

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long photoId;


    private String filename;
    private String displayTitle;
    private Integer widthPixels;
    private Integer heightPixels;
    private Date pictureTakenDate;
    private Date uploadDate;

    @OneToMany(mappedBy = "photo", cascade = CascadeType.PERSIST, orphanRemoval = true,fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<OrderItem> orderItems = new HashSet<>(0);

    @ManyToMany(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "photosubjects2photos",
            joinColumns = { @JoinColumn(name = "photoid") },
            inverseJoinColumns = { @JoinColumn(name = "photosubjectid") }
            )
    private Set<PhotoSubject> photoSubjects = new HashSet<>(0);

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "albumid",nullable = false)
    private Album album;

    /**
     * default empty constructor
     */
    public Photo()
    {
    }

    @Transient
    private SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    @Transient
    private SimpleDateFormat hoursMinutesSecondsDateFormatter = new SimpleDateFormat("HH:mm:ss");


    /**
     * complete constructor; sets picture upload date to now
     *
     * @param filename
     * @param album
     * @param width
     * @param height
     * @param pictureTakenDate
     */
    public Photo(String filename, Album album, Integer width, Integer height, Date pictureTakenDate, String displayTitle)
    {
        setFilename(filename);
        setAlbum(album);
        setWidthPixels(width);
        setHeightPixels(height);
        setPictureTakenDate(pictureTakenDate);
        setUploadDate(new Date());
        setDisplayTitle(displayTitle);
    }

    /**
     * @return the suffix that is used for the orientation mode
     */
    public String getOrientationSuffix()
    {
        String retVal;
        if (isOrientationLandscape())
        {
            retVal = Registry._LANDSCAPE_MODE_SUFFIX;
        } else if (isOrientationPortrait())
        {
            retVal = Registry._PORTRAIT_MODE_SUFFIX;
        } else
        {
            return Registry._PORTRAIT_MODE_SUFFIX;
        }

        return retVal;
    }



    /**
     * @return true if orientation is portrait
     */
    public boolean isOrientationPortrait()
    {
        return (getWidthPixels().compareTo(getHeightPixels()) < 0);
    }

    /**
     * true if orientation if landscpae
     *
     * @return inverse of isOrientationPortrait
     */
    public boolean isOrientationLandscape()
    {
        return !isOrientationPortrait();
    }

    /**
     * this getter will be called from the view to present the picture taken time
     *
     * @return String with HH:mm
     */
    public String getShortTimeString()
    {
        return hoursMinutesSecondsDateFormatter.format(getPictureTakenDate());
    }

    /**
     *
     * @return (absolute o relative ) URL string for thumbnail image
     */
    public String getThumbnailUrl()
    {
        String thumbnailUrl;

        if (this.isAfterImageServiceMigration()){
            // old solution before using parameters:
            // thumbnailUrl = getMasterImageUrlFromImageService() + "?w=100&h=100&fit=clip&auto=format,enhance,compress&q=40&usm=20";
            // get a signed thumbnail URL
            Map<String,String> params = new HashMap<String,String>();
            params.put("w","100");
            params.put("h","100");
            params.put("fit","clip");
            params.put("auto","format,enhance,compress");
            params.put("q","40");
            params.put("usm","20");

            thumbnailUrl = ImagingHelper.getSignedImgixUrl(params,getPathForImageService(), getImageServiceDomain(), getImageServiceSignKey() ) ;
        } else {
            // URL to thumbnail file - legacy solution before image service (imgix)
            return Registry.getFileStorageProvider().getThumbnailUrl(getAlbum().getGenericLevelId().toString(), getFilename());
        }
        return thumbnailUrl;
    }

    /**
     *
     * @return (absolute o relative ) URL string for thumbnail image
     */
    public String getThumbnailUrl2x()
    {
        String thumbnailUrl;

        if (this.isAfterImageServiceMigration()){
            // old solution before using parameters:
            // thumbnailUrl = getMasterImageUrlFromImageService() + "?w=100&h=100&fit=clip&auto=format,enhance,compress&q=40&usm=20";
            // get a signed thumbnail URL
            Map<String,String> params = new HashMap<String,String>();
            params.put("w","100");
            params.put("h","100");
            params.put("fit","clip");
            params.put("auto","format,enhance,compress");
            params.put("q","30");
            params.put("usm","20");
            params.put("dpr","2");

            thumbnailUrl = ImagingHelper.getSignedImgixUrl(params,getPathForImageService(), getImageServiceDomain(), getImageServiceSignKey()) ;
        } else {
            // URL to thumbnail file - legacy solution before image service (imgix)
            return Registry.getFileStorageProvider().getThumbnailUrl(getAlbum().getGenericLevelId().toString(), getFilename());
        }
        return thumbnailUrl;
    }

    /**
     *
     * @return (absolute o relative ) URL string for thumbnail image
     */
    public String getThumbnailUrl3x()
    {
        String thumbnailUrl;

        if (this.isAfterImageServiceMigration()){
            // old solution before using parameters:
            // thumbnailUrl = getMasterImageUrlFromImageService() + "?w=100&h=100&fit=clip&auto=format,enhance,compress&q=40&usm=20";
            // get a signed thumbnail URL
            Map<String,String> params = new HashMap<String,String>();
            params.put("w","100");
            params.put("h","100");
            params.put("fit","clip");
            params.put("auto","format,enhance,compress");
            params.put("q","20");
            params.put("usm","20");
            params.put("dpr","3");

            thumbnailUrl = ImagingHelper.getSignedImgixUrl(params,getPathForImageService(), getImageServiceDomain(), getImageServiceSignKey()) ;
        } else {
            // URL to thumbnail file - legacy solution before image service (imgix)
            return Registry.getFileStorageProvider().getThumbnailUrl(getAlbum().getGenericLevelId().toString(), getFilename());
        }
        return thumbnailUrl;
    }

    /**
     *
     * @return URL string for display image
     */
    public String getDisplayUrl()
    {
        String displayUrl;
        if (this.isAfterImageServiceMigration())
        {
            // old solution before using params and imgix client
            // displayUrl = getMasterImageUrlFromImageService() + "?w=380&h=380&fit=clip&auto=format,enhance&q=50&usm=20";


            Map<String,String> params = new HashMap<String,String>();
            params.put("w","380");
            params.put("h","380");
            params.put("fit","clip");
            params.put("auto","format,enhance,compress");
            params.put("q","50");
            params.put("usm","20");


            if (Registry.isDevEnv() || Registry.isIntEnv()) {
                // _logger.debug("printing out startnumber on display image");
                printStartnumbersOnPhoto(params, getStartnumbersAsString());
                // addNumberRecognitionText(params, getStartnumbersAsString());
            }

            displayUrl = ImagingHelper.getSignedImgixUrl(params,getPathForImageService(), getImageServiceDomain(),getImageServiceSignKey()) ;

        } else {
            // URL to display file - before image service migration (imgix)
            return Registry.getFileStorageProvider().getDisplayUrl(getAlbum().getGenericLevelId().toString(), getFilename());

        }
        return displayUrl;
    }

    public String getDisplayUrl2x() {

        String displayUrl;
        if (this.isAfterImageServiceMigration())
        {
            // old solution before using params and imgix client
            // displayUrl = getMasterImageUrlFromImageService() + "?w=380&h=380&fit=clip&auto=format,enhance&q=50&usm=20";


            Map<String,String> params = new HashMap<String,String>();
            params.put("w","380");
            params.put("h","380");
            params.put("fit","max");
            params.put("auto","format,enhance,compress");
            params.put("q","40");
            params.put("usm","20");
            params.put("dpr","2");

            // add number recognition text
            // *****
            // test only
            if (Registry.isDevEnv() || Registry.isIntEnv()) {
                // _logger.debug("printing out startnumber on display image");
                printStartnumbersOnPhoto(params, getStartnumbersAsString());
                // addNumberRecognitionText(params, getStartnumbersAsString());
            }
            // ****

            displayUrl = ImagingHelper.getSignedImgixUrl(params,getPathForImageService(), getImageServiceDomain(), getImageServiceSignKey()) ;

        } else {
            // URL to display file - before image service migration (imgix)
            return Registry.getFileStorageProvider().getDisplayUrl(getAlbum().getGenericLevelId().toString(), getFilename());

        }
        return displayUrl;    }

    /**
     * iterate through photoSubjects and eventrunners, create a list of startnumbers and return them
     * @return list of Startnumber Objects
     */
    private String getStartnumbersAsString() {

        List <String> startnumbers = new ArrayList<>();

        for (Object o : getPhotoSubjects()) {
            PhotoSubject photoSubject = (PhotoSubject) o;

            for (Object o1 : photoSubject.getEventRunners()) {
                EventRunner eventRunner = (EventRunner) o1;
                startnumbers.add(eventRunner.getStartnumber());
            }
        }
        return String.join("/", startnumbers);
    }

    public String getDisplayUrl3x() {

        String displayUrl;
        if (this.isAfterImageServiceMigration())
        {
            // old solution before using params and imgix client
            // displayUrl = getMasterImageUrlFromImageService() + "?w=380&h=380&fit=clip&auto=format,enhance&q=50&usm=20";


            Map<String,String> params = new HashMap<String,String>();
            params.put("w","380");
            params.put("h","380");
            params.put("fit","max");
            params.put("auto","format,enhance,compress");
            params.put("q","20");
            params.put("usm","20");
            params.put("dpr","3");

            if (Registry.isDevEnv() || Registry.isIntEnv()) {
                // _logger.debug("printing out startnumber on display image");
                printStartnumbersOnPhoto(params, getStartnumbersAsString());
                // addNumberRecognitionText(params, getStartnumbersAsString());
            }


            displayUrl = ImagingHelper.getSignedImgixUrl(params,getPathForImageService(),getImageServiceDomain(),getImageServiceSignKey() ) ;

        } else {
            // URL to display file - before image service migration (imgix)
            return Registry.getFileStorageProvider().getDisplayUrl(getAlbum().getGenericLevelId().toString(), getFilename());

        }
        return displayUrl;
    }

    /**
     * delete again - testing only
     * @param params imgix URL creation params
     * @param allStartnumbers list of Startnumber objects
     */
    private void addNumberRecognitionText(Map<String, String> params, List<Startnumber> allStartnumbers) {
        try {

            Test test =  new Test();
            List<Startnumber> photoStartnumbers = test.getRecognizedNumbersFor(this,allStartnumbers);
            allStartnumbers.addAll(photoStartnumbers);
            String numbers = photoStartnumbers.stream().map(Startnumber::getStartnumberText).collect(Collectors.joining("/"));
            printStartnumbersOnPhoto(params, numbers);

        } catch (Exception e) {
            _logger.debug("error trying to recognize number on photo",e);
            e.printStackTrace();
        }
    }

    private void printStartnumbersOnPhoto(Map<String, String> params, String numbers) {
        //_logger.debug("startnumbers  : " + numbers);

        params.put("txtsize","30");
        params.put("txtalign","bottom,right");
        params.put("txtclr","AADD44");
        params.put("txt", numbers);
    }

    public String getDisplayUrlFacebookSharingImage() {

        String displayUrl;
        if (this.isAfterImageServiceMigration())
        {
            //w=1000&h=500&fit=crop&crop=top%2Cleft

            Map<String,String> params = new HashMap<String,String>();
            params.put("w","1000");
            params.put("h","500");
            params.put("fit","crop");
            params.put("crop","top,left");
            params.put("auto","format,enhance,compress");
            params.put("q","20");
            params.put("usm","20");

            displayUrl = ImagingHelper.getSignedImgixUrl(params,getPathForImageService(), getImageServiceDomain(), getImageServiceSignKey() ) ;

        } else {
            // URL to display file - before image service migration (imgix)
            return Registry.getFileStorageProvider().getDisplayUrl(getAlbum().getGenericLevelId().toString(), getFilename());

        }
        return displayUrl;
    }

    public URL getImgixUrl(Map<String, String> imgixParams) throws MalformedURLException {
        return new URL(ImagingHelper.getSignedImgixUrl(imgixParams, getPathForImageService(), getImageServiceDomain(), getImageServiceSignKey()));
    }

    /**
     * Helper method to determine if photo belongs to an event that has been imported after the image service migration imgix
     * @return true in case photo will be handled by image service (imgix)
     */
    public boolean isAfterImageServiceMigration() {
        return getAlbum().getEvent().getEventDateYear() >= 2018;
    }


    /**
     * Path (without domain name / host from image service)
     * @return
     */
    public String getPathForImageService() {
        return "fine-images/" + getAlbum().getGenericLevelId().toString() + "/fine/" + getFilename();
    }



    /**
     * Returns the domain (host name) of the image service (i.e. from imgix.com) source to address this image.
     * Needed to be introduced after we split the s3 buckets and introducted an ireland (eu-west) bucket in order to use amazon rekognition
     * @return String containing the domain (host) name
     */
    private String getImageServiceDomain() {
        // todo: refactor : introduce constants / map / enum and use helper method also for AWS S3 Bucket location query (see AwsS3FileStorageProvide)
        String domain;
        if (getAlbum().getEvent().getEventDateYear() < 2019) {
            domain = Registry.getApplicationEnvironment() + "-sportrait.imgix.net";
            // use with CDN - currently not possible because https cannot be applied to imgix custom subdomains:
            // domain = Registry.getApplicationEnvironment() + "-sportrait.imgix.net";
        } else { // after 2019 use new imgix source that links to ireland s3 bucket
            // use with CDN - currently not possible because https cannot be applied to imgix custom subdomains:
            // domain = "cdn.imgs-2."+Registry.getApplicationEnvironment() + ".sportrait.com";
            domain = Registry.getApplicationEnvironment() + "2-sportrait.imgix.net"; // adding an index after the environment
        }

        return domain;
    }


    private String getImageServiceSignKey() {
        String signKey;
        if (getAlbum().getEvent().getEventDateYear() < 2019) {
            signKey = Registry.getImgixSignKey();
        } else { // after 2019 use new imgix source that links to ireland s3 bucket
            signKey =  Registry.getImgixSignKey2();
        }

        return signKey;
    }

    public String getHighResUrl()
    {
        String highResUrl = "todo";
//        todo : concatenate high res url (not via fileStorageProvider)
        // currently implemented via DownloadPhotoAction

        // check if free or paid image
        // used for social sharing

        // example for high res image from imgix including the logo / sponsor bar :
        // https://int-sportrait.imgix.net/fine-images/215/fine/sola15_e12_mf_2011.JPG?bm=normal&blend64=L2xvZ28vc29sYS1zcG9uc29ycy1iYXItYm90dG9tLW5ldS5wbmc&mark64=L2xvZ28vYXN2ei1sb2dvLTIwMTctcmVzaXplZC03MDBweC5wbmc&markalign=right%2Ctop&ba=bottom%2C%20center&bs=inherit
        return highResUrl;
    }



    public boolean equals(Object obj)
    {
        return this.getPhotoId().equals(((Photo) obj).getPhotoId());
    }



    /**
     * Return InputStream from photo file by given fileStorageProvider (changed since S3 Migration)
     *
     * @return The fine file content as InputStream from the storage provider
     */
    public InputStream getFileContent()
    {
        FileStorageProviderInterface fileStorageProvider = Registry.getFileStorageProvider();
        return fileStorageProvider.getFineImageFileContent(getAlbum(), getFilename());
    }

    /**
     * convenience method to handle a new PhotoSubject
     * @param subj
     */
    public void addPhotoSubject(PhotoSubject subj)
    {
        Set subjects = getPhotoSubjects();
        if (subjects==null)
        {
            subjects = new HashSet();
        }
        subjects.add(subj);
    }

    /**
     * convenience getter for photographer in class album
     * @return
     */
    public Photographer getPhotographer()
    {
        return getAlbum().getPhotographer();
    }


    public Long getPhotoId() {
        return this.photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDisplayTitle() {
        return this.displayTitle;
    }

    public void setDisplayTitle(String displayTitle) {
        this.displayTitle = displayTitle;
    }

    public Integer getWidthPixels() {
        return this.widthPixels;
    }

    public void setWidthPixels(Integer widthPixels) {
        this.widthPixels = widthPixels;
    }

    public Integer getHeightPixels() {
        return this.heightPixels;
    }

    public void setHeightPixels(Integer heightPixels) {
        this.heightPixels = heightPixels;
    }

    public Date getPictureTakenDate() {
        return this.pictureTakenDate;
    }

    public void setPictureTakenDate(Date pictureTakenDate) {
        this.pictureTakenDate = pictureTakenDate;
    }

    public Date getUploadDate() {
        return this.uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Set getOrderItems() {
        return this.orderItems;
    }

    public void setOrderItems(Set orderItems) {
        this.orderItems = orderItems;
    }

    public Set getPhotoSubjects() {
        return this.photoSubjects;
    }

    public void setPhotoSubjects(Set photoSubjects) {
        this.photoSubjects = photoSubjects;
    }

    public Album getAlbum() {
        return this.album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    /**
     * toString
     * @return String
     */
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("photoId").append("='").append(getPhotoId()).append("' ");
      buffer.append("filename").append("='").append(getFilename()).append("' ");
      buffer.append("displayTitle").append("='").append(getDisplayTitle()).append("' ");
      buffer.append("widthPixels").append("='").append(getWidthPixels()).append("' ");
      buffer.append("heightPixels").append("='").append(getHeightPixels()).append("' ");
      buffer.append("pictureTakenDate").append("='").append(getPictureTakenDate()).append("' ");
      buffer.append("uploadDate").append("='").append(getUploadDate()).append("' ");
      buffer.append("album").append("='").append(getAlbum()).append("' ");
      buffer.append("]");

      return buffer.toString();
     }
}
