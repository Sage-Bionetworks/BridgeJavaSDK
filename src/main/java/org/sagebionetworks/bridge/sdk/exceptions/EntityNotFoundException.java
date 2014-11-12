package org.sagebionetworks.bridge.sdk.exceptions;

public class EntityNotFoundException extends BridgeServerException {

    private static final long serialVersionUID = -5306944794350472764L;

    public EntityNotFoundException(String message, String endpoint) {
        super(message, 404, endpoint);
    }

}
