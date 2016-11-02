package org.sagebionetworks.bridge.rest.exceptions;

import org.sagebionetworks.bridge.rest.model.UserSessionInfo;

@SuppressWarnings("serial")
public final class ConsentRequiredException extends BridgeSDKException {

    private final UserSessionInfo session;

    public ConsentRequiredException(String message, String endpoint, UserSessionInfo session) {
        super(message, 412, endpoint);
        this.session = session;
    }

    public final UserSessionInfo getSession() {
        return session;
    }

    @Override
    public String toString() {
        return String.format("ConsentRequiredException[message=%s, statusCode=%s, endpoint=%s]", 
            getMessage(), getStatusCode(), getRestEndpoint());
    }
}
