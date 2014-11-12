package org.sagebionetworks.bridge.sdk.exceptions;

public class UnauthorizedException extends BridgeServerException {

    private static final long serialVersionUID = 825341441889370177L;

    public UnauthorizedException(String message, String endpoint) {
        super(message, 403, endpoint);
    }

}
