package ch.unartig.studioserver.beans;

import ch.unartig.studioserver.Registry;

/**
 *
 * Simple bean used by the uploading function
 *
 * SPORTRAIT / unartig AG
 * Created by alexanderjosef on 16.01.16.
 */
public class UploadPath {
    private  String label;
    private String value;

    /**
     * Create new instance and set its value to the "key" passed in the constructor
     * label contains number of images in the upload folder
     * @param key
     */
    public UploadPath(String key) {

        value = key;
        label = key + " ("+Registry.getFileStorageProvider().getNumberOfFineImageFiles(key)+")";
    }

    public String getValue() {
        return value;
    }



    public String getLabel() {
        return label;
    }


}
