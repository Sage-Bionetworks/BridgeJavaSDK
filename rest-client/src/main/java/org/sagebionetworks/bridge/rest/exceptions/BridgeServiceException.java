package org.sagebionetworks.bridge.rest.exceptions;

@SuppressWarnings("serial")
public class BridgeServiceException extends BridgeSDKException {
    public BridgeServiceException(String message, String endpoint) {
        super(message, 500, endpoint);
    }
}
