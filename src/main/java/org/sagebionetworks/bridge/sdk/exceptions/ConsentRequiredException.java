package org.sagebionetworks.bridge.sdk.exceptions;

import org.sagebionetworks.bridge.sdk.Session;

public final class ConsentRequiredException extends BridgeServerException {
    private static final long serialVersionUID = -5005089414518354638L;

    private final Session session;

    public ConsentRequiredException(String message, String endpoint, Session session) {
        super(message, 412, endpoint);
        this.session = session;
    }

    public final Session getSession() {
        return session;
    }
}
