package org.sagebionetworks.bridge.rest.exceptions;

@SuppressWarnings("serial")
public class EntityPublishedException extends BridgeSDKException {

    public EntityPublishedException(String message, String endpoint) {
        super(message, 400, endpoint);
    }

}
