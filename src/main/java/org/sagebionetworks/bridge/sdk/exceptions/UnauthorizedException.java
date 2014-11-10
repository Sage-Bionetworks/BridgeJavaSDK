package org.sagebionetworks.bridge.sdk.exceptions;

public class UnauthorizedException extends BridgeServerException {

    private static final long serialVersionUID = 825341441889370177L;

    public UnauthorizedException(String endpoint) {
        super("Caller does not have permission to access this service.", 403, endpoint);
    }

}
