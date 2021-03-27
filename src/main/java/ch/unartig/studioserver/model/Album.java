/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since Sep 22, 2005$
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
 * Revision 1.4  2007/05/02 14:27:55  alex
 * Uploading refactored, included fine-path server import for sportrait
 *
 * Revision 1.3  2007/04/17 11:03:27  alex
 * dynamic pager added
 *
 * Revision 1.2  2007/04/03 06:49:23  alex
 * photographer added, cagtegory added to album
 *
 * Revision 1.1  2007/03/27 16:39:17  alex
 * refactored studioalbum into album
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.5  2007/03/14 03:18:36  alex
 * no more price segment
 *
 * Revision 1.4  2007/03/14 02:41:01  alex
 * initial checkin
 *
 * Revision 1.3  2007/03/13 16:55:03  alex
 * template for properties
 *
 * Revision 1.2  2007/03/09 23:44:24  alex
 * no message
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.42  2006/12/06 18:42:08  alex
 * no js necessary for basic shopping cart functionality
 *
 * Revision 1.41  2006/12/05 22:51:56  alex
 * album kann jetzt freigeschaltet werden oder geschlossen sein
 *
 * Revision 1.40  2006/11/10 16:01:37  urban
 * tree bug solves
 * main.css
 *
 * Revision 1.39  2006/11/10 14:24:13  alex
 * dynamic priceinfo
 *
 * Revision 1.38  2006/11/08 09:55:03  alex
 * dynamic priceinfo
 *
 * Revision 1.37  2006/04/30 16:21:27  alex
 * removing system.outs
 *
 * Revision 1.36  2006/04/29 23:32:07  alex
 * many sola features, bugs, hibernate config
 *
 * Revision 1.35  2006/04/19 21:31:53  alex
 * session will be restored with album-bean (i.e. for bookmarked urls or so...)
 *
 * Revision 1.34  2006/04/06 18:31:22  alex
 * display fixed for sports album
 *
 * Revision 1.33  2006/03/20 15:45:33  alex
 * first check in for new sports album logic and db changes
 *
 * Revision 1.32  2006/03/20 15:33:32  alex
 * first check in for new sports album logic and db changes
 *
 * Revision 1.31  2006/03/08 17:42:26  alex
 * small fixes
 *
 * Revision 1.30  2006/03/03 16:54:56  alex
 * minor fixes
 *
 * Revision 1.29  2006/02/28 15:06:35  alex
 * link in overview links to first page
 *
 * Revision 1.28  2006/02/28 14:57:46  alex
 * added more resources (email for order confirmation), small fixes
 *
 * Revision 1.27  2006/02/23 14:37:42  alex
 * admin tool: new category works now
 *
 * Revision 1.26  2006/02/22 16:10:25  alex
 * added back link
 *
 * Revision 1.25  2006/02/22 14:00:51  alex
 * new album nav concept works also in display
 *
 * Revision 1.24  2006/02/16 17:13:46  alex
 * admin interface: deletion of levels works now
 *
 * Revision 1.23  2006/02/15 15:57:03  alex
 * bug [968] fixed. admin interface does that now
 *
 * Revision 1.22  2006/02/13 16:15:28  alex
 * but [968]
 *
 * Revision 1.21  2006/02/10 14:21:49  alex
 * admin tool: edit level partly ...
 *
 * Revision 1.20  2006/02/08 18:04:50  alex
 * first steps for album type configuration
 *
 * Revision 1.19  2006/01/24 15:38:35  alex
 * file ending is now .html
 *
 * Revision 1.18  2005/11/30 10:47:38  alex
 * bug fixes
 *
 * Revision 1.17  2005/11/29 02:00:17  alex
 * bug fixes
 *
 * Revision 1.16  2005/11/28 17:52:16  alex
 * bug fixes
 *
 * Revision 1.15  2005/11/27 19:39:10  alex
 * fast upload
 *
 * Revision 1.14  2005/11/25 11:09:09  alex
 * removed system outs
 *
 * Revision 1.13  2005/11/25 10:56:58  alex
 *
 * Revision 1.12  2005/11/22 19:45:46  alex
 * admin actions, configurations
 *
 * Revision 1.11  2005/11/20 21:24:32  alex
 * side navigation fixes
 *
 * Revision 1.10  2005/11/19 11:08:20  alex
 * index navigation works, (extended GenericLevel functions)
 * wrong calculation of fixed checkout overview eliminated
 *
 * Revision 1.9  2005/11/18 19:15:52  alex
 * stuff ...
 *
 * Revision 1.8  2005/11/16 14:26:49  alex
 * validator works for email, new library
 *
 * Revision 1.7  2005/11/07 21:57:43  alex
 * admin interface refactored
 *
 * Revision 1.6  2005/11/07 17:38:26  alex
 * admin interface refactored
 *
 * Revision 1.5  2005/11/05 21:41:58  alex
 * overview und links in tree menu
 *
 * Revision 1.4  2005/10/26 14:34:32  alex
 * first version of album overview
 * new mappings in struts for the /album/** url
 *
 * Revision 1.3  2005/10/24 14:37:55  alex
 * small fixes, creating directories
 *
 * Revision 1.2  2005/10/24 13:50:07  alex
 * upload of album
 * import in db
 * processing of images
 *
 * Revision 1.1  2005/10/08 10:52:36  alex
 * jstl 1.1 integrated, new web.xml
 *
 * Revision 1.4  2005/10/06 14:30:09  alex
 * generating the nav tree recursivly works
 *
 * Revision 1.3  2005/10/06 11:06:33  alex
 * generating the nav tree
 *
 * Revision 1.2  2005/10/06 08:54:04  alex
 * cleaning up the model
 *
 * Revision 1.1  2005/09/26 18:37:45  alex
 * *** empty log message ***
 *
 ****************************************************************/
package ch.unartig.studioserver.model;

import ch.unartig.controller.Client;
import ch.unartig.exceptions.NotAuthorizedException;
import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.businesslogic.AlbumType;
import ch.unartig.studioserver.businesslogic.GenericLevelVisitor;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;
import ch.unartig.studioserver.persistence.DAOs.OrderItemDAO;
import ch.unartig.studioserver.persistence.DAOs.PhotoDAO;
import ch.unartig.studioserver.persistence.DAOs.PriceDAO;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.*;
import java.util.*;



/**
 *
 */

@Entity
@DiscriminatorValue("ALBUM")
public class Album extends GenericLevel implements Serializable {

    @Transient
    private Set problemFiles;
    /**
     * this String defines the action url that is to be called for viewing this album
     */
    @Transient
    private String actionString;

    /**
     * this will be overwritten be inheriting classes
     */
    @Transient
    private String actionStringPart = "/album/";

    @Transient
    private String albumTypeString;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "photographerid")
    private Photographer photographer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventid")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventcategoryid")
    private EventCategory eventCategory;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("pictureTakenDate")
    private Set<Photo> photos = new HashSet<>(0);

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OrderBy("productType")
    private Set<Product> products = new HashSet<>(0);

    /**
     * default constructor
     */
    public Album() {
    }


    /**
     * actionString must be lazily initialized
     * <br>cannot be initialized during object creation ... null pointer exception
     *
     * @return action url string
     */
    private String lazyInitActionString() {
        if (actionString == null) {
            actionString = getActionStringPart() + getGenericLevelId().toString() + "/" + getNavTitle() + ".html";
        }
        _logger.debug("returning :" + actionString);
        return actionString;
    }

    /**
     * @param visitor Visitor to use
     */
    public void accept(GenericLevelVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * @param navTitle
     * @param longTitle
     * @param description
     */
    public Album(String navTitle, String longTitle, String description) {
        setNavTitle(navTitle);
        setLongTitle(longTitle);
        setDescription(description);
    }


    public List listChildren() {
        return null;
    }

    public Class getParentClazz() {
        return Event.class;
    }

    public String getEventDateDisplay() {
        return getEvent().getEventDateDisplay();
    }


    /**
     * convenience method to add an event
     *
     * @param event
     */
    public void setParentLevel(GenericLevel event) {
        _logger.debug("$$$$$$$$$$$$$$$$$$$$$$$$$  addParentLevel NARROW  $$$$$$$$$$$$$$$$$$$$$$$$4");
        Event e = (Event) event;
        setEvent(e);
        e.getAlbums().add(this);
    }

    public GenericLevel getParentLevel() {
        return getEvent();
    }

    /**
     * set the hibernate - property albumTypeString using the AlbumType
     *
     * @param albumType
     */
    public void setAlbumType(AlbumType albumType) {
        this.setAlbumTypeString(albumType.getDesignator());
    }

    public AlbumType getAlbumType() {
        return AlbumType.getAlbumType(this.getAlbumTypeString());
    }

    /**
     * Register photos that are already at the file system providers location (no temp folder available)
     *
     * @param createThumbnailDisplay
     * @param applyLogoOnFineImages  true if logo shall be copied on fine photos
     */
    public void registerPhotos(Boolean createThumbnailDisplay, boolean applyLogoOnFineImages) {

        _logger.debug("start registerPhotos, " + System.currentTimeMillis());

        // Try passing the method to register the photos in Album class (we don't want to mix file system and sportrait registration logic) - java lambdas?
        // Registry.getFileStorageProvider().registerStoredFinePhotos(this,album.createThumbnailDisplay::registersinglephoto(), createThumbnailDisplay);

        // in File storage provider: go through Folder of Album and register all valid photo stored in album-folder; store problem files
        problemFiles = Registry.getFileStorageProvider().registerStoredFinePhotos(this, createThumbnailDisplay, applyLogoOnFineImages);
    }

    /**
     * Register all photos from the temporary 'fine' Path in the db; reads the EXIF picture-take date and enters it for every photo record<br/>
     * <p/> Needed: temporary fine photos
     * <p/> copies the temp file to it's final location according to the given fileStorageProvider
     * <p/> This method shall fail gracefully with a exception message in case a photo can not be registered (corrupt file, not a foto-file etc.)
     *
     * @param tempSourceDir         Temporary location (pointed to in the UI) where the files to be imported have been temporarily uploaded; can be 'null' if the photos have already been put to the correct file storage location
     * @param createThumbDisp       set to true to create the display and thumbnail images
     * @param applyLogoOnFineImages set to true if logo needs to be copied on all fine images
     */
    public void registerPhotosFromTempLocation(String tempSourceDir, boolean createThumbDisp, boolean applyLogoOnFineImages) {
        _logger.debug("start registerPhotosFromTempLocation, " + System.currentTimeMillis());

        // loop through temp directory on local file system with uploaded files (independent of file storage provider for the temporary photo location)
        Registry.getFileStorageProvider().registerFromTempPath(this, tempSourceDir, createThumbDisp, applyLogoOnFineImages);
        // think whether do startnumber recognition here or in registerFromTempPath
        // better solution would be to have a pattern on "registerFromTempPath" where the startnumber recognition is one task (and not to do everything in the filestorageprovider specific methods)

        // after registering fine images, delete the temp folder on the file storage provider
        Registry.getFileStorageProvider().deleteFile(tempSourceDir, this);
    }


    /**
     * Registers a single photo in the db and creates the thumb and disp images if the applyLogoOnFineImages argument is true
     * AJ 20180204 : applyLogoOnFineImages not used anymore with image service (imgix)
     * <p>
     * <p>
     * EXIF orientation deatils according to: http://sylvana.net/jpegcrop/exif_orientation.html
     * <p>
     * <p>
     * For convenience, here is what the letter F would look like if it were tagged correctly and displayed by a program that ignores the orientation tag (thus showing the stored image):
     * <p>
     * 1        2       3      4         5            6           7          8
     * <p>
     * 888888  888888      88  88      8888888888  88                  88  8888888888
     * 88          88      88  88      88  88      88  88          88  88      88  88
     * 8888      8888    8888  8888    88          8888888888  8888888888          88
     * 88          88      88  88
     * 88          88  888888  888888
     *
     * @param problemFiles           A set of accumulated files that caused problems during import
     * @param photoFileContentStream The image file input stream to be registered
     * @param filename               The filename used for registering the photo in the db photos table
     * @param createThumbDisp        set to true to create the display and thumbnail images
     * @param applyLogoOnFineImages
     * @return newly created and registered photo
     */
    public Photo registerSinglePhoto(Set problemFiles, InputStream photoFileContentStream, String filename, boolean createThumbDisp, boolean applyLogoOnFineImages) {


        int pictureWidth;
        int pictureHeight;
        Date pictureTakenDate;
        int pictureOrientation;

        try {
            // use metadata-extractor (com.drewnoakes.metadata-extractor) , get rid of JAI and own ExifData implementation
            Metadata metadata = ImageMetadataReader.readMetadata(photoFileContentStream);
            Directory jpegDirectory = metadata.getFirstDirectoryOfType(JpegDirectory.class);
            ExifDirectoryBase exifDirectoryBase = metadata.getFirstDirectoryOfType(ExifDirectoryBase.class);
            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

            // todo : this can cause an error in case the Tag does not exist. check using containsTag() first
            if (exifDirectoryBase.containsTag(ExifDirectoryBase.TAG_ORIENTATION)) {

                pictureOrientation = exifDirectoryBase.getInt(ExifDirectoryBase.TAG_ORIENTATION);

                _logger.debug("read width and height - consider orientation");
                if ((pictureOrientation != 6) && (pictureOrientation != 8)) {
                    // "regular" landscape orientation
                    pictureHeight = jpegDirectory.getInt(JpegDirectory.TAG_IMAGE_HEIGHT);
                    pictureWidth = jpegDirectory.getInt(JpegDirectory.TAG_IMAGE_WIDTH);

                } else {
                    // "portrait" orientation
                    pictureWidth = jpegDirectory.getInt(JpegDirectory.TAG_IMAGE_HEIGHT);
                    pictureHeight = jpegDirectory.getInt(JpegDirectory.TAG_IMAGE_WIDTH);

                }

            } else {
                // no exif orientation tag available, assume landscape orientation
                // "regular" landscape orientation
                pictureHeight = jpegDirectory.getInt(JpegDirectory.TAG_IMAGE_HEIGHT);
                pictureWidth = jpegDirectory.getInt(JpegDirectory.TAG_IMAGE_WIDTH);
            }
            // todo later: introduce orientation as property of photo

            if (directory.containsTag(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)) { // check first for existince of tag information
                pictureTakenDate = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                _logger.debug("registerPhoto 3, " + System.currentTimeMillis());
                if (pictureTakenDate == null) {
                    pictureTakenDate = new Date(0);
                    _logger.error("Unable to determine date of file");
                    //problemFiles.add(photoFile);
                }
            } else {
                // no exif date information
                pictureTakenDate = new Date(0);
            }

            _logger.debug("filename : " + filename);
            _logger.debug("***********  " + pictureTakenDate + "  **************");

            Photo photo = new Photo(filename, this, pictureWidth, pictureHeight, pictureTakenDate, filename);
            // add to db:
            add(photo);


            // removed logic to add thumbnails or logo montage on master images
            // see 1.6-branch
            return photo;

        } catch (Exception e3) {
            // todo: catch specific (Metadata extractor)
            _logger.info("unknown error; continue with next image", e3);
            //noinspection unchecked
            problemFiles.add(filename);
            return null;
        }
    }


    /**
     * overriden for album: go to the album-action
     *
     * @return album action link
     */
    public String getIndexNavLink() {
        return getActionString() + "?page=1";
    }

    /**
     * @return the action url that is to called for viewing this album
     */
    public String getActionLink() {
        return getActionString();
    }


    public String getLevelType() {
        return Registry._NAME_ALBUM_LEVEL_TYPE;
    }

    /**
     * Convenience method to add and save a new photo that belongs to this album;
     * Commits a transaction and opens a new one for each photo that is stored.
     *
     * @param photo
     * @throws UAPersistenceException
     */
    private void add(Photo photo) throws UAPersistenceException {
        PhotoDAO phDao = new PhotoDAO();
        try {
            HibernateUtil.beginTransaction();
            getPhotos().add(photo);
            phDao.saveOrUpdate(photo);
            HibernateUtil.commitTransaction();
            _logger.info("wrote photo (filename/id) : " + photo.getFilename() + "/" + photo.getPhotoId().toString());
        } catch (UAPersistenceException e) {
            HibernateUtil.rollbackTransaction();
            _logger.error("error while saving photo", e);
        }
    }



    public void setProblemFiles(Set problemFiles) {
        this.problemFiles = problemFiles;
    }

    public Set getProblemFiles() {
        if (problemFiles == null) {
            problemFiles = new HashSet();
        }
        return problemFiles;
    }

    public List<Photo> getPhotosAsList() {
        return new ArrayList<>(getPhotos());
    }

    /**
     * overridden from GenericLevel for checking the type of level
     *
     * @return true for album
     */
    public boolean isAlbumLevel() {
        return true;
    }

    /**
     * Delete Album:
     * delete all photos in db<br>
     * remove references in orderitems
     * delete files from storage provider (local file system / S3)
     * all order items that have a photo of this album need to set their photofilename and set the photoid to null
     */
    public void deleteLevel() throws UAPersistenceException {
        OrderItemDAO oiDao = new OrderItemDAO();

        Set orderItemsForAlbum = new HashSet();
        // todo  this does not perform  ... create a HQL expression to update all orderitems
        for (Object o : getPhotos()) {
            Photo photo = (Photo) o;
            orderItemsForAlbum.addAll(photo.getOrderItems());
            photo.setOrderItems(Collections.EMPTY_SET);
        }

        // removing reference to photo from orderItems
        // todo check performance?
        for (Object anOrderItemsForAlbum : orderItemsForAlbum) {
            OrderItem orderItem = (OrderItem) anOrderItemsForAlbum;
            orderItem.setPhotoFileName(orderItem.getPhoto().getFilename());
            orderItem.setPhoto(null);
            orderItem.setProduct(null);
            _logger.debug("trying to save order item : " + orderItem);
            _logger.debug("trying to save Product : " + orderItem.getProduct());
            try {
                // save includes cascaded save of product
                oiDao.saveOrUpdate(orderItem);
            } catch (UAPersistenceException e) {
                throw new UAPersistenceException("can not save orderitem", e);
            }

        }


        // now delete the album image directories on disk
        try {
            Registry.getFileStorageProvider().deletePhotos(this);
        } catch (UAPersistenceException e) {
            _logger.error(e);
        }

        // eventCategory contains reference to album (this caused a temporary problem if caching is turned on for albums in eventcategories)
        getEventCategory().getAlbums().remove(this);

    }

    /**
     * Convenience method to access last photo in album
     * todo: the term 'album' is not specific enough anymore with sportsalbums ... there, the last photo of one 'view' is not necessarily the last photo in the whole album
     *
     * @return last photo in album
     */
    public Photo getLastPhotoInAlbumAndSelection() {
        _logger.debug("Album.getLastPhotoInCategoryAndSelection xxxx");
        // reload this album
        GenericLevelDAO glDao = new GenericLevelDAO();
        List<Photo> photoList = new ArrayList<>(((Album) glDao.load(this.getGenericLevelId(), Album.class)).getPhotos());
        Photo retVal = (photoList.get(getNumberOfPhotos() - 1));
        _logger.debug("returning photo : " + retVal);
        return retVal;
    }

    /**
     * method to get first photo in album
     *
     * @return first photo in album
     */
    public Photo getFirstPhotoInAlbum() {
        PhotoDAO photoDao = new PhotoDAO();
        return photoDao.getFirstPhotoFor(this);
    }

    public String getActionString() {
        _logger.debug("calling action string ...");
        lazyInitActionString();
        return actionString;
    }

    public String getActionStringPart() {
        return actionStringPart;
    }

    public void setActionStringPart(String actionStringPart) {
        this.actionStringPart = actionStringPart;
    }


    /**
     * Given the map of productTypeids (key) and the priceids (value) set the collections of products for this album
     *
     * @param productPrices
     * @throws ch.unartig.exceptions.UAPersistenceException
     */
    public void setProductPricesMap(Map productPrices) throws UAPersistenceException {

        // todo deprecate once we have to zk solution that handles single product changes?
        // product entries per album: only one per productType
        Set productTypeIds = productPrices.keySet();
        PriceDAO priceDao = new PriceDAO();
        for (Object typeId : productTypeIds) {
            String productTypeIdString = (String) typeId;
            String priceIdString = (String) productPrices.get(productTypeIdString);
            //producttypes with priceid <=0 are not set for this album
            final Long productTypeId = Long.valueOf(productTypeIdString);
            final Long priceId = Long.valueOf(priceIdString);
            if (priceId > 0 && !getAvailableProductTypes(false).keySet().contains(productTypeId)) {
                // producttype does not yet exist for this album; create new product
                getProducts().add(new Product(productTypeId, priceId, this));
            }
            // product exists, and producttype is already available for album. update?
            else if (priceId > 0) {
                final Product availableProduct = getProductFor(productTypeId);
                final Price newPrice = priceDao.load(priceId);
                _logger.debug("available product : [" + availableProduct + "] old price: [" + availableProduct.getPrice() + "] new Price [" + newPrice + "]");
                availableProduct.setPrice(newPrice);
                // update newPrice
            } else {
                removeProductFor(productTypeId);
            }


        }
    }

    /**
     * if a product entry exists that has the passed productTypeId as identifier for its productType, delete it?
     *
     * @param productTypeId
     * @return
     */
    private boolean removeProductFor(Long productTypeId) {
        return getProducts().remove(getProductFor(productTypeId));
    }

    /**
     * There is zero or one product for a given product-type per album.
     *
     * @param productTypeId The ID of the ProductType
     * @return <p>The product that has the productType identified by the productTypeId.</p>
     * <p>NULL, if no product exists with the given productType.</p>
     */
    public Product getProductFor(Long productTypeId) {
        // make a query or use the collection???
        for (Object productO : getProducts()) {
            Product product = (Product) productO;
            if (product.getProductType().getProductTypeId().equals(productTypeId)) {
                return product;
            }
        }
        _logger.debug("no product found with given productTypeId"); // this is fine. It will then show as not available in the overview.
        return null;
    }

    /**
     * Return a map containing the productTypeId as key and the productType as Value
     *
     * @param onlyActiveProducts set to true to filter for active products, in the shopping cart, for example.
     *                           For product configuration, more likely all products are needed
     * @return
     */
    public Map<Long, ProductType> getAvailableProductTypes(boolean onlyActiveProducts) {

        Map<Long, ProductType> productTypeMap = new HashMap<>();
        Set<Product> products = onlyActiveProducts ? getActiveProducts() : getProducts();
        for (Product product : products) {
            ProductType productType = product.getProductType();
            productTypeMap.put(productType.getProductTypeId(), productType);
        }
        return productTypeMap;
    }


    /**
     * Write access check for an album; client needs to be either admin or owner of the album.
     * Currently used for all access checks, also read (check calling methods)
     *
     * @param client
     * @return
     * @throws NotAuthorizedException
     */
    protected boolean checkWriteAccessFor(Client client) throws NotAuthorizedException {
        _logger.debug("checking access for user [" + client.getUserProfile().getUserName() + "] with roles [" + client.getUserProfile().getRoles() + "]");
        _logger.debug("client is admin? " + client.isAdmin());
        // _logger.debug("Photographer : " + getPhotographer()); // don't use this - can cause lazy initialization exception
        // special case no photographer:
        if (getPhotographer() == null && client.isAdmin()) {
            // album without a photographer ...
            return true;
        } else if (getPhotographer() == null && !client.isAdmin()) {
            throw new NotAuthorizedException("Unexpected state : album w/o linked photographer accessed from non-admin!!");
        }
        // regular check:
        if (!(client.isAdmin() || getPhotographer().equals(client.getPhotographer()))) {
            _logger.debug("client is not authorized - photographerId  :  " + client.getPhotographer().getPhotographerId());
            throw new NotAuthorizedException("Missing rights - need to be owning photographer or admin");
        }
        return true;
    }

    /**
     * Read access check for an album; client needs to be either admin or owner of the album.
     *
     * @param client
     * @return
     * @throws NotAuthorizedException
     */
    public boolean checkReadAccessFor(Client client) throws NotAuthorizedException {
        //todo : implement RBAC specific read access permissions - currently no permission schema for it.
        return checkWriteAccessFor(client);
    }

    /**
     * Only products that are not flagged for inactive shall be shown in the product information or shopping cart.
     *
     * @return Active products (products that don't have the inactvie flag)
     */
    public Set<Product> getActiveProducts() {
        Set<Product> activeProducts = new HashSet<Product>();
        Set<Product> allProductsForAlbum = getProducts();
        for (Object anAllProductsForAlbum : allProductsForAlbum) {
            Product product = (Product) anAllProductsForAlbum;
            if (product.getInactive() == null || !product.getInactive()) { // either null or NOT inactive
                activeProducts.add(product);
            }
        }
        return activeProducts;
    }

    /**
     * @return true In case a free download is offered on the display page offers this method
     */
    public boolean isHasFreeHighResDownload() {
        for (Object o : getActiveProducts()) {
            Product product = (Product) o;
            if (product.isDigitalProduct() && (product.getPrice().getPriceCHF().floatValue() == 0.0f)) {
                return true;
            }
        }
        return false;
    }

    public String getAlbumTypeString() {
        return this.albumTypeString;
    }

    public void setAlbumTypeString(String albumTypeString) {
        this.albumTypeString = albumTypeString;
    }

    public Photographer getPhotographer() {
        return this.photographer;
    }

    public void setPhotographer(Photographer photographer) {
        this.photographer = photographer;
    }

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public EventCategory getEventCategory() {
        return this.eventCategory;
    }

    public void setEventCategory(EventCategory eventCategory) {
        this.eventCategory = eventCategory;
    }

    public Set<Photo> getPhotos() {
        return this.photos;
    }

    public void setPhotos(Set<Photo> photos) {
        this.photos = photos;
    }

    public Set<Product> getProducts() {
        return this.products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    /**
     * toString
     *
     * @return String
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
        buffer.append("photographer").append("='").append(getPhotographer()).append("' ");
        buffer.append("event").append("='").append(getEvent()).append("' ");
        buffer.append("eventCategory").append("='").append(getEventCategory()).append("' ");
        buffer.append("]");

        return buffer.toString();
    }


    /**
     * sorts the albums for a nice list when showing all albums sorted by event and category (sales report)
     * Album comparator using Generics;
     */
    public static class EventCategoryComparator implements Comparator<Album> {// needs to be static

        public int compare(Album album, Album albumToCompare) {

            // first order is event name
            final int eventTitleComparison = album.getEvent().getLongTitle().compareTo(albumToCompare.getEvent().getLongTitle());
            if (eventTitleComparison != 0) {
                return eventTitleComparison;
            }
            // events are equal: we sort by the eventcategory id (should be in a sensible order thanks to the list-index of eventCategory)
            return album.getEventCategory().getEventCategoryId().compareTo(albumToCompare.getEventCategory().getEventCategoryId());
        }
    }


}
