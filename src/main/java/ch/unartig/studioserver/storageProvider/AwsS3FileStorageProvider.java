package ch.unartig.studioserver.storageProvider;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.model.Album;
import ch.unartig.studioserver.model.SportsAlbum;
import ch.unartig.util.FileUtils;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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
            String key = "fine-images/"+album.getGenericLevelId()+"/"+Registry.getFinePath()+photoFile.getName();
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

    public File getFineImageFile(Album album, String filename) {
        _logger.debug("Downloading an s3 fine image");

        // example for key: fine-images/163/fine/sola14_e01_fm_0005.JPG

        String key = "fine-images/"+album.getGenericLevelId()+"/"+Registry.getFinePath()+filename;
        GetObjectRequest objectRequest = new GetObjectRequest(bucketName, key);
        S3Object object = null;
        try {
            object = s3.getObject(objectRequest);
        } catch (AmazonClientException e) {
            e.printStackTrace();
        }
        _logger.debug("S3 File Content-Type: " + object.getObjectMetadata().getContentType());
        File destFile = new File(filename);
        try {
            FileUtils.copyFile(object.getObjectContent(), destFile);
        } catch (IOException e) {
            _logger.info("Cannot not read file from S3 Storage ("+key+"/"+bucketName+")");
            return null;
        }
        return destFile;
    }

    public File[] getFineImages(Album album) {

        // todo only tests here ...


        ListObjectsRequest listObjectRequest = new ListObjectsRequest().
                withBucketName(bucketName).
                withPrefix("fine-images/").
                withDelimiter("/");
        List<String> objectListing = s3.listObjects(listObjectRequest).getCommonPrefixes();

        for (int i = 0; i < objectListing.size(); i++) {
            String s = objectListing.get(i);
            System.out.println(s);
            _logger.debug(s);
        }

        listObjectRequest = new ListObjectsRequest().
                withBucketName(bucketName).
                withPrefix("fine-images/175/fine/").
                withDelimiter("/");

        ObjectListing objectListing2 = s3.listObjects(listObjectRequest);

        for (int i = 0; i < objectListing2.getObjectSummaries().size(); i++) {
            S3ObjectSummary s3ObjectSummary = objectListing2.getObjectSummaries().get(i);

        }



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
        return new File[0];
    }

    public void delete(String key) {

        // s3.deleteObject(bucketName, key);

        // todo-files: implement
        throw new RuntimeException("not implemented yet");

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
                String key = "fine-images/"+album.getGenericLevelId()+"/"+Registry.getFinePath()+zipEntry.getName();

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
