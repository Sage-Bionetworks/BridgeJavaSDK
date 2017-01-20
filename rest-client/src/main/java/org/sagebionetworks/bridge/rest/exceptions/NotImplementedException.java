package org.sagebionetworks.bridge.rest.exceptions;

/**
 * This endpoint has not been implemented for this study. It has been disabled, or not completely configured. The caller
 * cannot fix the call to succeed, but it is not an unexpected server error, it is intentional.
 */
@SuppressWarnings("serial")
public class NotImplementedException extends BridgeSDKException {

    public NotImplementedException(String message, String endpoint) {
        super(message, 501, endpoint);
    }

}
