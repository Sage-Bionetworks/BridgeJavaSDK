package org.sagebionetworks.bridge.sdk.rest.exceptions;

@SuppressWarnings("serial")
public class NotAuthenticatedException extends BridgeSDKException {

    public NotAuthenticatedException(String message, String endpoint) {
        super(message, 401, endpoint);
    }


}
