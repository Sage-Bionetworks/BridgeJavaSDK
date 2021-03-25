package org.sagebionetworks.bridge.rest.exceptions;

@SuppressWarnings("serial")
public class PublishedEntityException extends BridgeSDKException {

    public PublishedEntityException(String message, String url) {
        super(message, 400, url);
    }

    @Override
    public String toString() {
        return "PublishedEntityException[message=" + getMessage() +
                ", statusCode=" + getStatusCode() +
                ", endpoint=" + getRestEndpoint() + "]";
    }
}
