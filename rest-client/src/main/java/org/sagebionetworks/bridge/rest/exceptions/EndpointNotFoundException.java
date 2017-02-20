package org.sagebionetworks.bridge.rest.exceptions;

@SuppressWarnings("serial")
public class EndpointNotFoundException extends BridgeSDKException {

    public EndpointNotFoundException(String message, String endpoint) {
        super(message, 404, endpoint);
    }
    
}
