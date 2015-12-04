package org.sagebionetworks.bridge.sdk.exceptions;

@SuppressWarnings("serial")
public class EntityAlreadyExistsException extends BridgeSDKException {

    public EntityAlreadyExistsException(String message, String endpoint) {
        super(message, 409, endpoint);
    }


}
