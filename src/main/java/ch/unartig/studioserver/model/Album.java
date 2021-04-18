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
