package org.sagebionetworks.bridge.rest.exceptions;

@SuppressWarnings("serial")
public class LimitExceededException extends BridgeSDKException {
    public LimitExceededException(String message, String endpoint) {
        super(message, 429, endpoint);
    }
}
