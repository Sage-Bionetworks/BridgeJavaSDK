package org.sagebionetworks.bridge.sdk.exceptions;

public class ConcurrentModificationException extends BridgeServerException {

    private static final long serialVersionUID = -8765879557845159620L;

    public ConcurrentModificationException(String message, String endpoint) {
        super(message, 409, endpoint);
    }

}
