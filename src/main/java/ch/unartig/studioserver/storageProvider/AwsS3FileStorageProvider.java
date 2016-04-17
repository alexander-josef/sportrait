package ch.unartig.studioserver.storageProvider;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.model.Album;
import ch.unartig.studioserver.model.SportsAlbum;
import ch.unartig.util.FileUtils;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * SPORTRAIT / unartig AG
 * Created by alexanderjosef on 01.10.15.
 */
public class AwsS3FileStorageProvider implements FileStorageProviderInterface {
    Logger _logger = Logger.getLogger(getClass().getName());

    AmazonS3 s3;
    final private String bucketName = Registry.getS3BucketName();
    final private Region awsRegion = Region.getRegion(Regions.EU_CENTRAL_1); // Frankfurt
    private final static String awsS3Url = "s3.amazonaws.com";
    private String bucketUrlWithoutRegion = "http://" + bucketName + "." + awsS3Url;


    public AwsS3FileStorageProvider() {
        initStorageProvider();
    }

    public void initStorageProvider() {

         /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (~/.aws/credentials).
         */
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }

        s3 = new AmazonS3Client(credentials);
        s3.setRegion(awsRegion);


        _logger.debug("======================================================");
        _logger.debug("Amazon S3 storage provider implementation initialized");
        _logger.debug("======================================================\n");
    }


    public void putFineImage(Album album, File photoFile) throws UAPersistenceException {


        _logger.debug("Uploading a new object to S3 from a file\n");

        // todo: file exists already? Move from other s3 location? check for only JPG files? return success message? no space? other exceptions from S3?
        try {
            String key = getFineImageKey(album, photoFile.getName());
            s3.putObject(new PutObjectRequest(bucketName, key, photoFile));
        } catch (AmazonClientException e) {
            _logger.error("Problem putting photo to S3", e);
            throw new UAPersistenceException(e);
        }


    }

    public void putFineImage(Album album, OutputStream fineImageAsOutputStream, String fineImageFileName) {
        _logger.debug("Uploading a new fine image object to S3 from an output stream \n");

        String key = getFineImageKey(album, fineImageFileName);

        putImage((ByteArrayOutputStream) fineImageAsOutputStream, key, false);
        // todo: file exists already? Move from other s3 location? check for only JPG files? return success message? no space? other exceptions from S3?


    }

    /**
     * Store a file based on an output stream
     *
     * @param album
     * @param scaledImage
     * @param name
     * @throws ch.unartig.exceptions.UAPersistenceException
     */
    public void putDisplayImage(Album album, OutputStream scaledImage, String name) throws UAPersistenceException {

        String key = Registry.getWebImagesContext()+"/"+album.getGenericLevelId()+"/"+Registry.getDisplayPath()+name;

        putImage((ByteArrayOutputStream) scaledImage, key, true);

    }

    public void putThumbnailImage(Album album, OutputStream scaledImage, String name) {

        String key = Registry.getWebImagesContext()+"/"+album.getGenericLevelId()+"/"+Registry.getThumbnailPath() + name;

        putImage((ByteArrayOutputStream) scaledImage, key, true);
    }

    public InputStream getFineImageFileContent(Album album, String filename) {
        _logger.debug("Downloading an s3 fine image");

        // example for key: fine-images/163/fine/sola14_e01_fm_0005.JPG

        String key = getFineImageKey(album, filename);
        GetObjectRequest objectRequest = new GetObjectRequest(bucketName, key);
        S3Object object = null;
        try {
            object = s3.getObject(objectRequest);
        } catch (AmazonClientException e) {
            _logger.error("cannot get fine image file from s3 for filename : " + filename, e);
            throw new UAPersistenceException(e);
        }
        _logger.debug("S3 File Content-Type: " + object.getObjectMetadata().getContentType());
        _logger.debug("S3 File Content-Length: " + object.getObjectMetadata().getContentLength());

        return object.getObjectContent();
    }

    /**
     * Helper method for fine image key
     * @param album
     * @param filename
     * @return
     */
    private String getFineImageKey(Album album, String filename) {
        // todo: parameters
        return "fine-images/"+album.getGenericLevelId()+"/"+ Registry.getFinePath()+filename;
    }

    public Set registerStoredFinePhotos(Album album, Boolean createThumbnailDisplay, boolean applyLogoOnFineImages) {


        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().
                withBucketName(bucketName).
                withPrefix("fine-images/" + album.getGenericLevelId() + "/"+ Registry.getFinePath()).
                withDelimiter("/");

        ObjectListing objects;
        // loop through all listed objects - might be truncated and needs to be called several times
        do {
            objects = s3.listObjects(listObjectsRequest);

            for (int i = 0; i < objects.getObjectSummaries().size(); i++) {
                S3ObjectSummary s3ObjectSummary = objects.getObjectSummaries().get(i);
                String key = s3ObjectSummary.getKey();
                String filename = key.substring(key.lastIndexOf("/")+1);

                // todo : check if photo is already registered for album in DB?

                final S3ObjectInputStream objectContent = s3.getObject(new GetObjectRequest(bucketName, key)).getObjectContent();
                album.registerSinglePhoto(album.getProblemFiles(), objectContent, filename, createThumbnailDisplay, applyLogoOnFineImages);
            }
            listObjectsRequest.setMarker(objects.getNextMarker());
        } while (objects.isTruncated());

        // todo: clean up the return value - problem files can be added directly in album
        return null;
    }

    public void registerFromTempPath(Album album, String tempSourceDir, boolean createThumbDisp, boolean applyLogoOnFineImages) {


        long base = System.currentTimeMillis();

        // loop through jpeg files on S3
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().
                withBucketName(bucketName).
                withPrefix(tempSourceDir).
                withDelimiter("/");

        ObjectListing objects;

        // loop through all listed objects - might be truncated and needs to be called several times
        do {
            objects = s3.listObjects(listObjectsRequest);

            for (int i = 0; i < objects.getObjectSummaries().size(); i++) {
                _logger.debug("register Photo " + i + ", " + System.currentTimeMillis());
                String filename = "null";

                try {
                    S3ObjectSummary s3ObjectSummary = objects.getObjectSummaries().get(i);
                    String key = s3ObjectSummary.getKey();
                    if (key != null && !key.substring(key.lastIndexOf("/")+1).isEmpty()) { // filename not null or empty
                        filename = key.substring(key.lastIndexOf("/") + 1);
                        _logger.debug("Reading file :" + filename);

                        // todo : check if photo is already registered for album in DB?

                        final S3ObjectInputStream objectContent;
                        objectContent = s3.getObject(new GetObjectRequest(bucketName, key)).getObjectContent();
                        album.registerSinglePhoto(album.getProblemFiles(), objectContent, filename, createThumbDisp, applyLogoOnFineImages);
                        if (!applyLogoOnFineImages && !key.equals(getFineImageKey(album, filename))) { // if no logo has been copied on the fine image and stored in the right location, move the file now:
                            moveObject(key, getFineImageKey(album, filename));
                        } else if (!key.equals(getFineImageKey(album, filename))) { // or delete after a copy has already been placed in the right location (and make sure the temp key does not equal the final key)
                            delete(key);
                        }
                    } else {
                        _logger.info("s3 object is not a file, skipping entry for key : " + key);
                    }
                } catch (AmazonClientException e) {
                    _logger.error("Cannot read photo from temp location, skipping : " + filename, e);
                }
            }
            listObjectsRequest.setMarker(objects.getNextMarker());
        } while (objects.isTruncated());


        // handle problem files

        _logger.info("**********************");
        _logger.info("Import time (Java or Script): " + ((System.currentTimeMillis() - base) / 1000 + " seconds"));
        _logger.info("**********************");

    }

    /**
     * Helper method to move an object from a temporary location to its final location.
     * First copy to destination, then delete source
     * @param sourceKey
     * @param destinationKey
     * @return True if copy was successful, false otherwise
     */
    private boolean moveObject(String sourceKey, String destinationKey) {
        try {
            CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName,sourceKey,bucketName,destinationKey);
            s3.copyObject(copyObjectRequest);
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName,sourceKey);
            s3.deleteObject(deleteObjectRequest);
        } catch (AmazonServiceException ase) {
            _logger.error("Amazon Service Exception while moving File",ase);
            return false;
        } catch (AmazonClientException e) {
            _logger.error("Amazon Client Exception while moving file",e);
            return false;
        }

        return true;
    }

    /**
     * Use for showing number of photos when importing from temp location.
     * (implementation checks for truncated object lists and works also for object size > 1'000)
     * @param key the aws s3 prefix-key (or "folder")
     * @return
     */
    public int getNumberOfFineImageFiles(String key) {

        int fileCount=0;
        ObjectListing objectListing;
        ListObjectsRequest listObjectRequest;
        listObjectRequest = new ListObjectsRequest().
                withBucketName(bucketName).
                withPrefix(key).
                withDelimiter("/");
        do {
            objectListing = s3.listObjects(listObjectRequest);
            listObjectRequest.setMarker(objectListing.getNextMarker());
            fileCount += objectListing.getObjectSummaries().size();
        } while (objectListing.isTruncated());

        return fileCount;
    }

    public void delete(String key) {

        // todo : exception handling? if key is folder and folder is not empty?
        s3.deleteObject(bucketName, key);

    }

    /**
     * Returns the virtual hosted style URL of a thumbnail image
     * example: http://photos.sportrait.com.s3.amazonaws.com/web-images/176/thumbnail/sola14_e01_fm_0005.JPG
     *
     * @param genericLevelId AlbumId
     * @param filename filename of master image
     * @return Url to S3 object (public access)
     */
    public String getThumbnailUrl(String genericLevelId, String filename) {
        // todo: introduce a sportrait.com cname for "photos.sportrait.com.s3.amazonaws.com" and use a sportrait.com URL
        // todo: https not possible; certificate for domain name that includes the bucket name must exist. Change if https is needed
        return bucketUrlWithoutRegion + "/" + Registry.getWebImagesContext() + "/" + genericLevelId + "/" + Registry.getThumbnailPath() + filename;
    }

    /**
     * Returns the virtual hosted style URL of a display image
     * example: http://photos.sportrait.com.s3.amazonaws.com/web-images/176/display/sola14_e01_fm_0005.JPG
     * @param genericLevelId AlbumId
     * @param filename filename of master image
     * @return Url to S3 object (public access)
     */
    public String getDisplayUrl(String genericLevelId, String filename) {
        // todo: use following style (exclude Region): http://photos.sportrait.com.s3.amazonaws.com/web-images/176/display/sola14_e01_fm_0005.JPG
        // todo: introduce a sportrait.com cname for "photos.sportrait.com.s3.amazonaws.com" and use a sportrait.com URL
        return bucketUrlWithoutRegion +"/"+ Registry.getWebImagesContext() +"/"+ genericLevelId +"/"+ Registry.getDisplayPath() + filename;

    }


    public List<String> getUploadPaths() {

        ArrayList<String> retVal = new ArrayList();
        ListObjectsRequest listObjectRequest = new ListObjectsRequest().
                withBucketName(bucketName).
                withPrefix("upload/").
                withDelimiter("/");
        List<String> objectListing = s3.listObjects(listObjectRequest).getCommonPrefixes();

        // list all common prefixes:
        for (String prefix : objectListing) {
            _logger.debug(prefix);
            retVal.add(prefix);
        }

        return retVal;
    }

    public void deletePhotos(Album album) throws UAPersistenceException {


        ListObjectsRequest listObjectsRequest;

        // todo: configuration parameter for "fine-images"
        listObjectsRequest = new ListObjectsRequest().
                withBucketName(bucketName).
                withPrefix("fine-images/" + album.getGenericLevelId() + "/"+ Registry.getFinePath()).
                withDelimiter("/");

        deleteFromListObject(listObjectsRequest);

        listObjectsRequest = new ListObjectsRequest().
                withBucketName(bucketName).
                withPrefix(Registry.getWebImagesContext() +"/" + album.getGenericLevelId() + "/"+ Registry.getDisplayPath()).
                withDelimiter("/");

        deleteFromListObject(listObjectsRequest);

        listObjectsRequest = new ListObjectsRequest().
                withBucketName(bucketName).
                withPrefix(Registry.getWebImagesContext() +"/" + album.getGenericLevelId() + "/"+ Registry.getThumbnailPath()).
                withDelimiter("/");

        deleteFromListObject(listObjectsRequest);


    }

    private void deleteFromListObject(ListObjectsRequest listObjectsRequest) {
        ObjectListing objects;
        // loop through all listed objects - might be truncated and needs to be called several times
        do {
            objects = s3.listObjects(listObjectsRequest);

            for (int i = 0; i < objects.getObjectSummaries().size(); i++) {
                S3ObjectSummary s3ObjectSummary = objects.getObjectSummaries().get(i);
                s3.deleteObject(bucketName,s3ObjectSummary.getKey());

            }
            listObjectsRequest.setMarker(objects.getNextMarker());
        } while (objects.isTruncated());
    }

    public void putFilesFromArchive(SportsAlbum sportsAlbum, InputStream fileInputStream) throws UnartigException {


        _logger.debug("Extracting files to S3 : " + sportsAlbum.getLongTitle());
        extractFlatZipArchive(fileInputStream, sportsAlbum);


        // getFineImages(sportsAlbum);

    }


    /**
     *  @param scaledImage
     * @param key
     * @param setPublicReadAccess
     */
    private void putImage(ByteArrayOutputStream scaledImage, String key, boolean setPublicReadAccess) {
        // todo: check piped output stream. performance? memory?
        ByteArrayInputStream bais = new ByteArrayInputStream(scaledImage.toByteArray());

        // set the correct content type: (otherwise an image won't be displayed by the browser but the file will be downloaded)
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/jpeg");
        metadata.setContentLength(scaledImage.size());
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, bais, metadata);
        // set access control to public read:
        if (setPublicReadAccess) {
            putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
        }
        s3.putObject(putObjectRequest);

        // close bois / bais?

        try {
            bais.close();
            scaledImage.close();
        } catch (IOException e) {
            throw new UAPersistenceException(e);
        }
    }

    /**
     * Takes a zip archive as input stream and uploads Zip entry by Zip entry to S3
     * Two possible implementations:
     * either a) create a temporary file for each zip entry
     * or b) copy the input stream to a byte array for each zip entry
     * Currently implemented b)
     *
     * @param uploadInputStream
     * @param album
     * @throws UnartigException
     */
    private void extractFlatZipArchive(InputStream uploadInputStream, SportsAlbum album) throws UnartigException {
        ZipInputStream zis = null;
        ZipEntry zipEntry;
        try
        {
            zis = new ZipInputStream(uploadInputStream);
            // loop through input stream while there are more zip entries
            while ((zipEntry = zis.getNextEntry()) != null)
            {
                String key = getFineImageKey(album, zipEntry.getName());

                if (zipEntry.isDirectory() || zipEntry.getName().contains("/")) {
                    // is zip entry a directory or file in a directory?
                    _logger.info("Only Files allowed in uploaded archives; files must not be in directories - ignoring entry in ZIP File -- " + zipEntry.getName());
                }

                // check for .jpg file:
                else if ( !zipEntry.getName().contains(".")  || !".jpg".equals(zipEntry.getName().substring(zipEntry.getName().lastIndexOf(".")).toLowerCase()))
                {
                    // ignore entry
                    _logger.info("Skipping file in archive;File is not of type jpeg : " + zipEntry.getName());
                }
                else {
                    // seems to be a valid file - copy to target location:


                    try {

                        // Solution 1) - put the zip input stream to a byte array

                        // create separate output stream
                        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                        // write Zip Input Stream to Byte Array Output Stream, don't close the Zip Input Stream!
                        FileUtils.copyFile(zis,byteOut,false,true);
                        // write to output stream to byte array
                        byte[] bites = byteOut.toByteArray();

                        /////////////////////////

                        // Solution 2) - write a temp file for each zip entry and then upload tmp file to S3

                        // File tmpFile = new File(zipEntry.getName());
                        // FileUtils.copyFile(zis, tmpFile,false,true);
                        // PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, tmpFile);

                        ObjectMetadata metadata = new ObjectMetadata();
                        metadata.setContentType("image/jpeg");
                        metadata.setContentLength(bites.length);
                        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, new ByteArrayInputStream(bites),metadata);
                        // set access control: bucket owner (i.e. photographer ?) and Object owner gets full control
                        putObjectRequest.setCannedAcl(CannedAccessControlList.BucketOwnerFullControl);
                        s3.putObject(putObjectRequest);
                        _logger.info("extracted : " + zipEntry.getName());
                    } catch (AmazonClientException e) {
                        _logger.error("Problem putting photo to S3", e);
                        throw new UAPersistenceException(e);
                    }
                    zis.closeEntry();
                }

            }

        } catch (IOException e)
        {
            _logger.error("error while reading entries in zip file", e);
            System.exit(1);
        } finally
        {
            try
            {
                zis.close();
                uploadInputStream.close();
            } catch (IOException e)
            {
                _logger.error("input stream does not close");
                e.printStackTrace();
            }
        }

    }


}
