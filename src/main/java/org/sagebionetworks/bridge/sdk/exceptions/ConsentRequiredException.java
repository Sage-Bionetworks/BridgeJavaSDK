package org.sagebionetworks.bridge.sdk.exceptions;

import org.sagebionetworks.bridge.sdk.Session;

@SuppressWarnings("serial")
public final class ConsentRequiredException extends BridgeSDKException {

    private final Session session;

    public ConsentRequiredException(String message, String endpoint, Session session) {
        super(message, 412, endpoint);
        this.session = session;
    }

    public final Session getSession() {
        return session;
    }

    @Override
    public String toString() {
        return String.format("ConsentRequiredException[message=%s, statusCode=%s, endpoint=%s]", 
            getMessage(), getStatusCode(), getRestEndpoint());
    }
}
