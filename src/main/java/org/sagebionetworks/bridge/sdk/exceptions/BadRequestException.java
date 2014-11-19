package org.sagebionetworks.bridge.sdk.exceptions;

public class BadRequestException extends BridgeServerException {

    private static final long serialVersionUID = -2319926413669559039L;

    public BadRequestException(String message, String endpoint) {
        super(message, 400, endpoint);
    }

    public BadRequestException(Throwable t, String endpoint) {
        super(t, 400, endpoint);
    }

}
