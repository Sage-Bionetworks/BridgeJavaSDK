package org.sagebionetworks.bridge.rest.exceptions;

@SuppressWarnings("serial")
public class BadRequestException extends BridgeSDKException {

    public BadRequestException(String message, String endpoint) {
        super(message, 400, endpoint);
    }

    public BadRequestException(Throwable t, String endpoint) {
        super(t, 400, endpoint);
    }

}
