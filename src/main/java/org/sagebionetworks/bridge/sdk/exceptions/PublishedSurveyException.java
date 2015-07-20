package org.sagebionetworks.bridge.sdk.exceptions;

@SuppressWarnings("serial")
public class PublishedSurveyException extends BridgeServerException {

    public PublishedSurveyException(String message, String url) {
        super(message, 400, url);
    }

    @Override
    public String toString() {
        return "PublishedSurveyException[message=" + getMessage() +
                ", statusCode=" + getStatusCode() +
                ", endpoint=" + getRestEndpoint() + "]";
    }

}
