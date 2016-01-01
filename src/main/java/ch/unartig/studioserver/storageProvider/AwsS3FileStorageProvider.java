package ch.unartig.studioserver.storageProvider;

import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.model.Album;
import ch.unartig.util.FileUtils;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

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

    public void getFinePath() {

    }



    public void putFile(File file) {

    }

    public File getFile(Album album, String filename) {
        _logger.debug("Downloading an object");

        // example for key: fine-images/163/fine/sola14_e01_fm_0005.JPG

        String key = "fine-images/"+album.getGenericLevelId()+"/"+Registry.getFinePath()+filename;
        GetObjectRequest objectRequest = new GetObjectRequest(bucketName, key);
        S3Object object = null;
        try {
            object = s3.getObject(objectRequest);
        } catch (AmazonClientException e) {
            e.printStackTrace();
        }
        _logger.debug("S3 File Content-Type: "  + object.getObjectMetadata().getContentType());
        File destFile = new File(filename);
        try {
            FileUtils.copyFile(object.getObjectContent(), destFile);
        } catch (IOException e) {
            _logger.info("Cannot not read file from S3 Storage ("+key+"/"+bucketName+")");
            return null;
        }
        return destFile;
    }

    public void delete(String key) {

    }

    public String getThumbnailUrl(String genericLevelId, String filename) {

        // example:
        // https://s3-eu-west-1.amazonaws.com/photos.sportrait.com/web-images/163/thumbnail/sola14_e01_fm_0005.JPG
        // AWS S3 resource needs to be publicly readable

        String url = "https://s3-eu-west-1.amazonaws.com/" + bucketName +"/"+ Registry.getWebImagesContext() +"/"+ genericLevelId +"/"+ Registry.getThumbnailPath() + filename;
        return url;
    }

    public String getDisplayUrl(String genericLevelId, String filename) {

        // example:
        // https://s3-eu-west-1.amazonaws.com/photos.sportrait.com/web-images/163/display/sola14_e01_fm_0005.JPG
        // AWS S3 resource needs to be publicly readable

        String url = "https://s3-eu-west-1.amazonaws.com/" + bucketName +"/"+ Registry.getWebImagesContext() +"/"+ genericLevelId +"/"+ Registry.getDisplayPath() + filename;
        return url;

    }


}
