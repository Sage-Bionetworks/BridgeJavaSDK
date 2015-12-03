package org.sagebionetworks.bridge.sdk.exceptions;

@SuppressWarnings("serial")
public class ConcurrentModificationException extends BridgeSDKException {

    public ConcurrentModificationException(String message, String endpoint) {
        super(message, 409, endpoint);
    }

}
