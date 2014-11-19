package org.sagebionetworks.bridge.sdk.exceptions;

@SuppressWarnings("serial")
public class EntityNotFoundException extends BridgeServerException {

    public EntityNotFoundException(String message, String endpoint) {
        super(message, 404, endpoint);
    }

}
