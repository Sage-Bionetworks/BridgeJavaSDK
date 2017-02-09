package org.sagebionetworks.bridge.rest.exceptions;

@SuppressWarnings("serial")
public class ConstraintViolationException extends BridgeSDKException {
    
    public ConstraintViolationException(String message, String endpoint) {
        super(message, 409, endpoint);
    }
}
