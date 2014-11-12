package org.sagebionetworks.bridge.sdk.exceptions;

public class NotAuthenticatedException extends BridgeServerException {

    private static final long serialVersionUID = 9068536501239946185L;

    public NotAuthenticatedException(String message, String endpoint) {
        super(message, 401, endpoint);
    }


}
