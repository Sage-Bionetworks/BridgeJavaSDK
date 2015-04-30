package org.sagebionetworks.bridge.sdk.exceptions;

@SuppressWarnings("serial")
public class UnauthorizedException extends BridgeServerException {

    public UnauthorizedException(String message, String endpoint) {
        super(message, 403, endpoint);
    }

}
