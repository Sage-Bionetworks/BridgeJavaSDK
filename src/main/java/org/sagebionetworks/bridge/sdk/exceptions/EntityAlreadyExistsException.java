package org.sagebionetworks.bridge.sdk.exceptions;

public class EntityAlreadyExistsException extends BridgeServerException {

    private static final long serialVersionUID = 2445891731789628482L;

    public EntityAlreadyExistsException(String message, String endpoint) {
        super(message, 409, endpoint);
    }


}
