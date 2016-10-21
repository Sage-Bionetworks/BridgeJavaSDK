package org.sagebionetworks.bridge.sdk.rest.exceptions;

@SuppressWarnings("serial")
public class UnsupportedVersionException extends BridgeSDKException {
    
    public UnsupportedVersionException(String message, String endpoint) {
        super(message, 410, endpoint);
    }

}
