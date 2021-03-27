package ch.unartig.studioserver.storageProvider;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.sportrait.imgRecognition.MessageQueueHandler;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.businesslogic.ImportStatus;
import ch.unartig.studioserver.model.Album;
import ch.unartig.studioserver.model.Photo;
import ch.unartig.studioserver.model.SportsAlbum;
import ch.unartig.util.FileUtils;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.apache.log4j.Logger;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * SPORTRAIT / unartig AG
 * Created by alexanderjosef on 01.10.15.
 */
public class AwsS3FileStorageProvider implements FileStorageProviderInterface {
    private static final String FINE_IMAGES_PREFIX = "fine-images";
    private final Logger _logger = Logger.getLogger(getClass().getName());

    private AmazonS3 s3DefaultClient; //
    private AmazonS3 s3FrankfurtClient; // specific for older albums on frankfurt bucket
    // final private String bucketName = Registry.getS3BucketName();
    final private String preImageServiceBucketName = Registry.getS3BucketName();
    //    private final static String awsS3Url = "s3.amazonaws.com";
    private final static String awsS3RegionUrlFrankfurt = "s3-" + Registry.awsRegionFrankfurt + ".amazonaws.com";
    // todo: http or https
    // see for example: http://stackoverflow.com/questions/3048236/amazon-s3-https-ssl-is-it-possible
//    private String bucketUrlWithoutRegion = "http://" + bucketName + "." + awsS3RegionUrlFrankfurt;
    private String bucketHttpsUrl = "https://" + awsS3RegionUrlFrankfurt + "/" + preImageServiceBucketName;


    public AwsS3FileStorageProvider() {
        initStorageProvider();
    }

    public void initStorageProvider() {

         /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (~/.aws/credentials).

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

         */


        // creating different clients per region, see also https://aws.amazon.com/blogs/developer/working-with-different-aws-regions/
        s3DefaultClient = AmazonS3ClientBuilder.standard().withRegion(Registry.SPORTRAIT_AWS_DEFAULT_REGION).withForceGlobalBucketAccessEnabled(true).build();
        // not used yet:
        s3FrankfurtClient = AmazonS3ClientBuilder.standard().withRegion(Registry.AWS_FRANKFURT_REGION).withForceGlobalBucketAccessEnabled(true).build();


        _logger.debug("======================================================");
        _logger.debug("Amazon S3 storage provider implementation initialized");
        _logger.debug("======================================================\n");
    }


    public void putFineImage(Album album, File photoFile) throws UAPersistenceException {


        _logger.debug("Uploading a new object to S3 from a file\n");

        // todo: file exists already? Move from other s3 location? check for only JPG files? return success message? no space? other exceptions from S3?
        try {
            String key = getFineImageKey(album, photoFile.getName());
            s3DefaultClient.putObject(new PutObjectRequest(getS3BucketNameFor(album), key, photoFile));
        } catch (AmazonClientException e) {
            _logger.error("Problem putting photo to S3", e);
            throw new UAPersistenceException(e);
        }


    }

    public void putFineImage(Album album, OutputStream fineImageAsOutputStream, String fineImageFileName) {
        _logger.debug("Uploading a new fine image object to S3 from an output stream \n");

        String key = getFineImageKey(album, fineImageFileName);

        putImage((ByteArrayOutputStream) fineImageAsOutputStream, key, getS3BucketNameFor(album), false);
        // todo: file exists already? Move from other s3 location? check for only JPG files? return success message? no space? other exceptions from S3?


    }


    public InputStream getFineImageFileContent(Album album, String filename) {
        _logger.debug("Downloading an s3 fine image");

        // example for key: fine-images/163/fine/sola14_e01_fm_0005.JPG

        String key = getFineImageKey(album, filename);
        _logger.debug("Key : " + key);
        GetObjectRequest objectRequest = new GetObjectRequest(getS3BucketNameFor(album), key);
        S3Object object = null; // todo : check if the s3 object is closed again --> prevent connection pool leaks
        try {
            object = s3DefaultClient.getObject(objectRequest); // might fail if region is not set correctly ! must know where the object lives!
        } catch (AmazonClientException e) {
            _logger.error("cannot get fine image file from s3 for filename : " + filename, e);
            throw new UAPersistenceException(e);
        } catch (Exception ex) {
            _logger.error("general exception - check stack trace ", ex);
        }
        _logger.debug("S3 File Content-Type: " + object.getObjectMetadata().getContentType());
        _logger.debug("S3 File Content-Length: " + object.getObjectMetadata().getContentLength());

        S3ObjectInputStream objectContent = object.getObjectContent();
        return objectContent;
    }


    /**
     * Static helper method for fine image key
     *
     * @param album
     * @param filename
     * @return
     */
    public static String getFineImageKey(Album album, String filename) {
        // todo: parameters
        return FINE_IMAGES_PREFIX + "/" + album.getGenericLevelId() + "/" + Registry.getFinePath() + filename;
    }

    public Set registerStoredFinePhotos(Album album, Boolean createThumbnailDisplay, boolean applyLogoOnFineImages) {


        String bucketName = getS3BucketNameFor(album);
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().
                withBucketName(bucketName).
                withPrefix(FINE_IMAGES_PREFIX + "/" + album.getGenericLevelId() + "/" + Registry.getFinePath()).
                withDelimiter("/");

        ObjectListing objects;
        // loop through all listed objects - might be truncated and needs to be called several times
        do {
            objects = s3DefaultClient.listObjects(listObjectsRequest);

            for (int i = 0; i < objects.getObjectSummaries().size(); i++) {
                S3ObjectSummary s3ObjectSummary = objects.getObjectSummaries().get(i);
                String key = s3ObjectSummary.getKey();
                String filename = key.substring(key.lastIndexOf("/") + 1);

                // todo : check if photo is already registered for album in DB?

                final S3ObjectInputStream objectContent = s3DefaultClient.getObject(new GetObjectRequest(bucketName, key)).getObjectContent();
                album.registerSinglePhoto(album.getProblemFiles(), objectContent, filename, createThumbnailDisplay, applyLogoOnFineImages);
                objectContent.abort(); // abort (close) stream after reading the metadata - there's a warning that not all bytes were read. It's probably OK.
            }
            listObjectsRequest.setMarker(objects.getNextMarker());
        } while (objects.isTruncated());

        // todo: clean up the return value - problem files can be added directly in album
        return null;
    }

    public void registerFromTempPath(Album album, String tempSourceDir, boolean createThumbDisp, boolean applyLogoOnFineImages) {
        MessageQueueHandler queueHandler = MessageQueueHandler.getInstance();
        boolean applyNumberRecognition = true; // todo : add parameter if needed - default true so far. should go as query parameter to API
        long base = System.currentTimeMillis();

        // This is an expensive call!
        ImportStatus.getInstance().setPhotosRemaining(album, getNumberOfFineImageFiles(tempSourceDir));

        // loop through jpeg files on S3
        String targetBucketName = getS3BucketNameFor(album); // only target bucket name - source bucket name is current bucket location

        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().
                withBucketName(getCurrenctS3Bucket()).
                withPrefix(tempSourceDir).
                withDelimiter("/");

        ObjectListing objects;

        // loop through all listed objects - might be truncated and needs to be called several times
        do {
            objects = s3DefaultClient.listObjects(listObjectsRequest);

            for (int i = 0; i < objects.getObjectSummaries().size(); i++) {
                // todo : think if this can be done multi-threaded via a ThreadPoolExecutor: (see StartnumberProcessor.class, for example)
                _logger.debug("register Photo " + i + ", " + System.currentTimeMillis());
                String filename = "null";

                S3ObjectInputStream objectContent = null;
                try {
                    S3ObjectSummary s3ObjectSummary = objects.getObjectSummaries().get(i);
                    String importImageKey = s3ObjectSummary.getKey();
                    if (importImageKey != null && !importImageKey.substring(importImageKey.lastIndexOf("/") + 1).isEmpty()) { // filename not null or empty
                        filename = importImageKey.substring(importImageKey.lastIndexOf("/") + 1);
                        _logger.debug("Reading file :" + filename);

                        // todo : check if photo is already registered for album in DB?

                        objectContent = s3DefaultClient.getObject(new GetObjectRequest(getCurrenctS3Bucket(), importImageKey)).getObjectContent();
                        Photo newPhoto = album.registerSinglePhoto(album.getProblemFiles(), objectContent, filename, createThumbDisp, applyLogoOnFineImages);
                        objectContent.abort(); // since the rest of the image data is not read (only the EXIF data) we need to abort the http connection (see also the warnings from the AWS SDK currently I don't know how to avoid them)
                        String fineImageKey = getFineImageKey(album, filename);

                        _logger.debug("applyLogoOnFineImages " + applyLogoOnFineImages);
                        _logger.debug("applyNumberRecognition " + applyNumberRecognition);
                        _logger.debug("key temp file : " + importImageKey);
                        _logger.debug("key fine file : " + fineImageKey);
                        if (!applyLogoOnFineImages && !importImageKey.equals(fineImageKey)) { // if no logo has been copied on the fine image and the file is not yet stored in the right location, move the file now:
                            moveObject(targetBucketName, importImageKey, fineImageKey);
                            _logger.debug("moved master image to correct S3 directory");
                        } else if (!importImageKey.equals(fineImageKey)) { // or delete after a copy has already been placed in the right location (and make sure the temp key does not equal the final key)
                            deleteFile(importImageKey, album); // delete key / album needed for bucket
                            _logger.debug("master image deleted from temp location");
                        }
                        if (applyNumberRecognition && newPhoto != null) { // add logic in case there should be a switch in the UI
                            // add fine Image to queue for number recognition
                            String path = targetBucketName + "/" + fineImageKey;
                            queueHandler.addMessage(album, newPhoto.getPhotoId(), path); // newPhoto could be null for unknown file
                            ImportStatus.getInstance().incNumberRecognitionCounter(album);
                        }
                        // update import state object
                        ImportStatus.getInstance().photoImported(album);

                    } else {
                        _logger.info("s3 object is not a file, skipping entry for key : " + importImageKey);
                        ImportStatus.getInstance().importError(album);
                    }
                } catch (AmazonClientException e) {
                    _logger.error("Cannot read photo from temp location, skipping : " + filename, e);
                    ImportStatus.getInstance().importError(album);
                } finally { // make sure s3 object closes and release http connection
                    if (objectContent != null) {
                        try {
                            objectContent.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            listObjectsRequest.setMarker(objects.getNextMarker());
        } while (objects.isTruncated());
        ImportStatus.getInstance().resetPhotosImported(album); // all album photos are imported, clean up state counter
        _logger.info("**********************");
        _logger.info("Import time (Java or Script): " + ((System.currentTimeMillis() - base) / 1000 + " seconds"));
        _logger.info("**********************");

    }

    /**
     * Helper method to move an object from a temporary location to its final location.
     * First copy to destination, then delete source - EXCEPT for Dev env as a convenience for testing
     * Assumption: s3objects are closed after operations
     *
     * @param destinationBucketName depending on album location (Frankfurt / Ireland)
     * @param sourceKey
     * @param destinationKey
     * @return True if copy was successful, false otherwise
     */
    private boolean moveObject(String destinationBucketName, String sourceKey, String destinationKey) {
        try {
            CopyObjectRequest copyObjectRequest = new CopyObjectRequest(getCurrenctS3Bucket(), sourceKey, destinationBucketName, destinationKey);
            s3DefaultClient.copyObject(copyObjectRequest);
            if (!Registry.isDevEnv()) {
                DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(destinationBucketName, sourceKey);
                s3DefaultClient.deleteObject(deleteObjectRequest);
            }
        } catch (AmazonServiceException ase) {
            _logger.error("Amazon Service Exception while moving File", ase);
            return false;
        } catch (AmazonClientException e) {
            _logger.error("Amazon Client Exception while moving file", e);
            return false;
        }

        return true;
    }

    /**
     * Use for showing number of photos when importing from temp location.
     * (implementation checks for truncated object lists and works also for object size > 1'000)
     * CAUTION: slow operation
     * todo : works only with one level of folder hierarchy ? make it more robust
     * todo : works only with the default s3 client - only used for the upload folder so far. refactor if generic solution needed
     *
     * @param key the aws s3 prefix-key (or "folder")
     * @return
     */
    public int getNumberOfFineImageFiles(String key) {
        String bucketName = getCurrenctS3Bucket();
        int fileCount = 0;
        ObjectListing objectListing;
        ListObjectsRequest listObjectRequest;
        listObjectRequest = new ListObjectsRequest().
                withBucketName(bucketName).
                withPrefix(key).
                withDelimiter("/");
        _logger.debug("going to count from upload folder : " + key + " - starting at " + Instant.now());
        do {
            // todo : this is slow! there seem to be a better solution using the AWS S3 CLI
            _logger.debug("iterating over objectListings ....");
            objectListing = s3DefaultClient.listObjects(listObjectRequest);
            listObjectRequest.setMarker(objectListing.getNextMarker());
            fileCount += objectListing.getObjectSummaries().size();
        } while (objectListing.isTruncated());

        _logger.debug("returning file count : " + fileCount + " - completing at " + Instant.now());
        return fileCount;
    }

    public void deleteFile(String key, Album album) {

        // todo : exception handling? if key is folder and folder is not empty?
        s3DefaultClient.deleteObject(getS3BucketNameFor(album), key);

    }


    /**
     * Return a list with all paths available under the "upload/" path (the albums that have been uploaded)
     *
     * @return
     */
    public List<String> getUploadPaths() {

        _logger.debug("preparing ArrayList with upload paths");
        ArrayList<String> retVal = new ArrayList<>();

        List<String> objectListing = null;
        try {
            ListObjectsRequest listObjectRequest = new ListObjectsRequest().
                    withBucketName(getCurrenctS3Bucket()).
                    withPrefix("upload/").
                    withDelimiter("/");
            objectListing = s3DefaultClient.listObjects(listObjectRequest).getCommonPrefixes();
        } catch (SdkClientException e) {
            _logger.error("AWS SDK exception when retrieving upload paths : ", e);
        }

        _logger.debug("listing all prefixes ... ");
        // list all common prefixes:
        if (objectListing != null) {
            for (String prefix : objectListing) {
                _logger.debug("adding to uploadPaths : " + prefix);
                retVal.add(prefix);
            }
        }
        _logger.debug("returning list of upload folders");
        return retVal;
    }


    public void deletePhotos(Album album) throws UAPersistenceException {

        _logger.info("deleting on S3 ...");
        String bucketName = getS3BucketNameFor(album);
        ListObjectsRequest listObjectsRequest;

        // delete fine images
        listObjectsRequest = new ListObjectsRequest().
                withBucketName(bucketName).
                withPrefix(FINE_IMAGES_PREFIX + "/" + album.getGenericLevelId() + "/" + Registry.getFinePath()).
                withDelimiter("/");

        deleteFromListObject(listObjectsRequest, bucketName);

        deleteFromListObject(listObjectsRequest, bucketName);
        _logger.info("done deleting on S3");


    }

    private void deleteFromListObject(ListObjectsRequest listObjectsRequest, String bucketName) {
        ObjectListing objects;
        // loop through all listed objects - might be truncated and needs to be called several times
        do {
            objects = s3DefaultClient.listObjects(listObjectsRequest);

            for (int i = 0; i < objects.getObjectSummaries().size(); i++) {
                S3ObjectSummary s3ObjectSummary = objects.getObjectSummaries().get(i);
                s3DefaultClient.deleteObject(bucketName, s3ObjectSummary.getKey());

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
     * @param scaledImage
     * @param key
     * @param bucket
     * @param setPublicReadAccess
     */
    private void putImage(ByteArrayOutputStream scaledImage, String key, String bucket, boolean setPublicReadAccess) {
        // todo: check piped output stream. performance? memory?
        ByteArrayInputStream bais = new ByteArrayInputStream(scaledImage.toByteArray());

        // set the correct content type: (otherwise an image won't be displayed by the browser but the file will be downloaded)
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/jpeg");
        metadata.setContentLength(scaledImage.size());
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, bais, metadata);
        // set access control to public read:
        if (setPublicReadAccess) {
            putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
        }
        s3DefaultClient.putObject(putObjectRequest);

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
        try {
            zis = new ZipInputStream(uploadInputStream);
            // loop through input stream while there are more zip entries
            while ((zipEntry = zis.getNextEntry()) != null) {
                String key = getFineImageKey(album, zipEntry.getName());

                if (zipEntry.isDirectory() || zipEntry.getName().contains("/")) {
                    // is zip entry a directory or file in a directory?
                    _logger.info("Only Files allowed in uploaded archives; files must not be in directories - ignoring entry in ZIP File -- " + zipEntry.getName());
                }

                // check for .jpg file:
                else if (!zipEntry.getName().contains(".") || !".jpg".equals(zipEntry.getName().substring(zipEntry.getName().lastIndexOf(".")).toLowerCase())) {
                    // ignore entry
                    _logger.info("Skipping file in archive;File is not of type jpeg : " + zipEntry.getName());
                } else {
                    // seems to be a valid file - copy to target location:


                    try {

                        // Solution 1) - put the zip input stream to a byte array

                        // create separate output stream
                        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                        // write Zip Input Stream to Byte Array Output Stream, don't close the Zip Input Stream!
                        FileUtils.copyFile(zis, byteOut, false, true);
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
                        PutObjectRequest putObjectRequest = new PutObjectRequest(getS3BucketNameFor(album), key, new ByteArrayInputStream(bites), metadata);
                        // set access control: bucket owner (i.e. photographer ?) and Object owner gets full control
                        putObjectRequest.setCannedAcl(CannedAccessControlList.BucketOwnerFullControl);
                        s3DefaultClient.putObject(putObjectRequest);
                        _logger.info("extracted : " + zipEntry.getName());
                    } catch (AmazonClientException e) {
                        _logger.error("Problem putting photo to S3", e);
                        throw new UAPersistenceException(e);
                    }
                    zis.closeEntry();
                }

            }

        } catch (IOException e) {
            _logger.error("error while reading entries in zip file", e);
            System.exit(1);
        } finally {
            try {
                zis.close();
                uploadInputStream.close();
            } catch (IOException e) {
                _logger.error("input stream does not close");
                e.printStackTrace();
            }
        }

    }

    /**
     * Static helper to retrieve bucket name and make it variable according to the album that needs a bucket name
     * change of buckets could be necessary of different AWS services are not available everywhere
     * (this used to be the case with AWS Rekognition API - hence for 2019 the photos were in eu-west-1 - this has been reverted 2021-03)
     *
     * @param album
     * @return
     */
    private String getS3BucketNameFor(Album album) {
        // add logic if mapping per event is needed. used to be distinction for  Frankfurt and Ireland (when rekognition API was not available in eu-central-1)
        _logger.debug("before 2019 - returning Frankfurt bucket name");
        return Registry.getS3BucketName();
    }

    /**
     * returns the s3 bucket that is currently in use
     * This was needed after a migration to Ireland - eu-west-2 - when AWS Rekognition services were only available there
     *
     * @return
     */
    public String getCurrenctS3Bucket() {
        return Registry.getS3BucketName();
    }

}
