package org.sagebionetworks.bridge.rest.exceptions;

@SuppressWarnings("serial")
public class UnauthorizedException extends BridgeSDKException {

    public UnauthorizedException(String message, String endpoint) {
        super(message, 403, endpoint);
    }

}
