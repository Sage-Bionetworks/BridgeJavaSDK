package org.sagebionetworks.bridge.rest.exceptions;

@SuppressWarnings("serial")
public class AuthenticationFailedException extends BridgeSDKException {
    public AuthenticationFailedException(String message, String endpoint) {
        super(message, 401, endpoint);
    }
}
