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
    String bucketName = "photos.sportrait.com";


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
        // make sure the region is the same for creation as well as for reading!
        Region euWest1 = Region.getRegion(Regions.EU_WEST_1);
        s3.setRegion(euWest1);


        _logger.debug("===========================================");
        _logger.debug("Getting Started with Amazon S3");
        _logger.debug("===========================================\n");
    }


    public void putFineImage(Album album, File photoFile) throws UAPersistenceException {


        _logger.debug("Uploading a new object to S3 from a file\n");

        // todo: file exists already? Move from other s3 location? check for only JPG files? return success message? no space? other exceptions from S3?
        try {
            // todo: not only for fine images!
            String key = getFineImageKey(album, photoFile.getName());
            s3.putObject(new PutObjectRequest(bucketName, key, photoFile));
        } catch (AmazonClientException e) {
            _logger.error("Problem putting photo to S3", e);
            throw new UAPersistenceException(e);
        }


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

        String key = "web-images/"+album.getGenericLevelId()+"/"+Registry.getDisplayPath()+name;

        putImage((ByteArrayOutputStream) scaledImage, key);

    }

    public void putThumbnailImage(Album album, OutputStream scaledImage, String name) {

        String key = "web-images/"+album.getGenericLevelId()+"/"+Registry.getThumbnailPath() + name;

        putImage((ByteArrayOutputStream) scaledImage, key);
    }

    public InputStream getFineImageFileContent(Album album, String filename) {
        _logger.debug("Downloading an s3 fine image");

        // example for key: fine-images/163/fine/sola14_e01_fm_0005.JPG

        // todo: reusable helper method do get key:
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
        return "fine-images/"+album.getGenericLevelId()+"/"+ Registry.getFinePath()+filename;
    }

    public Set registerStoredFinePhotos(Album album, Boolean createThumbnailDisplay) {


        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().
                withBucketName(bucketName).
                withPrefix("fine-images/" + album.getGenericLevelId() + "/fine/").
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
                album.registerSinglePhoto(createThumbnailDisplay,album.getProblemFiles(),objectContent, filename);
            }
            listObjectsRequest.setMarker(objects.getNextMarker());
        } while (objects.isTruncated());

        // todo: clean up the return value - problem files can be added directly in album
        return null;
    }

    public void registerFromTempPath(Album album, String tempSourceDir, boolean createThumbDisp) {


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

                S3ObjectSummary s3ObjectSummary = objects.getObjectSummaries().get(i);
                String key = s3ObjectSummary.getKey();
                String filename = key.substring(key.lastIndexOf("/")+1);

                // todo : check if photo is already registered for album in DB?

                final S3ObjectInputStream objectContent = s3.getObject(new GetObjectRequest(bucketName, key)).getObjectContent();
                // call registerSinglePhoto for each file
                album.registerSinglePhoto(createThumbDisp,album.getProblemFiles(),objectContent, filename);
                // move file to correct location (set the right bucket key)


                // todo: move only when registration is successful
                String fineImageDestinationKey = getFineImageKey(album,filename);
                moveObject(key, fineImageDestinationKey);
                // todo: put logo on images

                // todo: check this method for applying a watermark and use it for logos
                // ImagingHelper.createNewImage(fineImage, displayScale, Registry._imageQuality, Registry._ImageSharpFactor, true);


            }
            listObjectsRequest.setMarker(objects.getNextMarker());
        } while (objects.isTruncated());

        // if empty, delete temp path on S3
        delete(tempSourceDir);

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
     * Use for showing number of photos when importing from temp location
     * @param key
     * @return
     */
    public int getNumberOfFineImageFiles(String key) {


        ListObjectsRequest listObjectRequest = new ListObjectsRequest().
                withBucketName(bucketName).
                withPrefix(key).
                withDelimiter("/");
        ObjectListing objectListing = s3.listObjects(listObjectRequest);

        return objectListing.getObjectSummaries().size();






        // -> works, returns "paths" under "fine-images"


        // some snippets from internet searches:
/*

        final ListObjectsRequest listObjectRequest = new ListObjectsRequest().
                withBucketName(bucketName).
                withPrefix("something");
        final ObjectListing objectListing = s3.listObjects(listObjectRequest);



        for (final S3ObjectSummary objectSummary: objectListing.getObjectSummaries()) {
            final String key = objectSummary.getKey();
            if (S3Asset.isImmediateDescendant(prefix, key)) {
                final String relativePath = getRelativePath(prefix, key);
                System.out.println(relativePath);
            }
        }


        // Try this:

        ListObjectsRequest listObjectRequest = new ListObjectsRequest().
                withBucketName(bucketName).
                withPrefix(prefix).
                withDelimiter("/");
        ObjectListing objectListing = s3.listObjects(listObjectRequest).getCommonPrefixes();

*/


    }

    public void delete(String key) {

        s3.deleteObject(bucketName, key);

    }

    public String getThumbnailUrl(String genericLevelId, String filename) {

        // example:
        // https://s3-eu-west-1.amazonaws.com/photos.sportrait.com/web-images/163/thumbnail/sola14_e01_fm_0005.JPG
        // AWS S3 resource needs to be publicly readable

        // todo: parameter
        String url = "https://s3-eu-west-1.amazonaws.com/" + bucketName +"/"+ Registry.getWebImagesContext() +"/"+ genericLevelId +"/"+ Registry.getThumbnailPath() + filename;
        return url;
    }

    public String getDisplayUrl(String genericLevelId, String filename) {

        // example:
        // https://s3-eu-west-1.amazonaws.com/photos.sportrait.com/web-images/163/display/sola14_e01_fm_0005.JPG
        // AWS S3 resource needs to be publicly readable

        // todo: parameter for AWS URL
        String url = "https://s3-eu-west-1.amazonaws.com/" + bucketName +"/"+ Registry.getWebImagesContext() +"/"+ genericLevelId +"/"+ Registry.getDisplayPath() + filename;
        return url;

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

        // todo: helper methods for fine / display / thumbnail paths

        ListObjectsRequest listObjectsRequest;

        listObjectsRequest = new ListObjectsRequest().
                withBucketName(bucketName).
                withPrefix("fine-images/" + album.getGenericLevelId() + "/fine/").
                withDelimiter("/");

        deleteFromListObject(listObjectsRequest);

        listObjectsRequest = new ListObjectsRequest().
                withBucketName(bucketName).
                withPrefix("web-images/" + album.getGenericLevelId() + "/display/").
                withDelimiter("/");

        deleteFromListObject(listObjectsRequest);

        listObjectsRequest = new ListObjectsRequest().
                withBucketName(bucketName).
                withPrefix("web-images/" + album.getGenericLevelId() + "/thumbnail/").
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
     *
     * @param scaledImage
     * @param key
     */
    private void putImage(ByteArrayOutputStream scaledImage, String key) {
        // todo: check piped output stream. performance? memory?
        ByteArrayInputStream bais = new ByteArrayInputStream(scaledImage.toByteArray());

        // set the correct content type: (otherwise an image won't be displayed by the browser but the file will be downloaded)
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/jpeg");
        metadata.setContentLength(scaledImage.size());
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, bais, metadata);
        // set access control to public read:
        putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
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
