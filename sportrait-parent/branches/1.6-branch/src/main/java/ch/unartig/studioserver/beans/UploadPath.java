package ch.unartig.studioserver.beans;

/**
 * SPORTRAIT / unartig AG
 * Created by alexanderjosef on 16.01.16.
 */
public class UploadPath {
    private  String label;
    private String value;

    /**
     * Create new instance and set its value to the "key" passed in the constructor
     * @param key
     */
    public UploadPath(String key) {

        value = key;
        label = key;
    }

    public String getValue() {
        return value;
    }



    public String getLabel() {
        return label;
    }


}
