package org.sagebionetworks.bridge.rest.exceptions;

@SuppressWarnings("serial")
public class NotImplementedException extends BridgeSDKException {

    public NotImplementedException(String message, String endpoint) {
        super(message, 501, endpoint);
    }

}
