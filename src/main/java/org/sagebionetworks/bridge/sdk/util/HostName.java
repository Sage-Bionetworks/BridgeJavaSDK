package org.sagebionetworks.bridge.sdk.util;

public class HostName {

    public static final String LOCAL = "http://localhost:9000/";
    public static final String DEV = "http://pd-dev.sagebridge.org/";
    public static final String STAGING = "http://pd-staging.sagebridge.org/";
    public static final String PROD = "http://pd.sagebridge.org/";
    
    public static boolean isValidHostName(String hostname) {
        if (hostname.equals(LOCAL) || hostname.equals(DEV) || hostname.equals(STAGING) || hostname.equals(PROD)) {
            return true;
        } else {
            return false;
        }
    }
}
