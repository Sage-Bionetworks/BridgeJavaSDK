package org.sagebionetworks.bridge.sdk.models.studies;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The operating systems that Bridge commonly accepts requests from. A minimum supported application 
 * version can be set for each of the operating systems. Application requests from clients lower than 
 * this version will be blocked by the server.
 */
public enum OperatingSystem {

    IOS("iPhone OS"),
    ANDROID("Android");
    
    private OperatingSystem(String osName) {
        this.osName = osName;
    }
    private final String osName;

    @JsonCreator
    public static OperatingSystem create(String osName) {
        for (OperatingSystem os : values()) {
            if (os.getOsName().toLowerCase().equals(osName.toLowerCase())) {
                return os;
            }
        }
        return null;
    }
    
    @JsonValue
    public String getOsName() {
        return osName;
    }
    
}
