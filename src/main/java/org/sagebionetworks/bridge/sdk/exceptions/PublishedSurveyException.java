package org.sagebionetworks.bridge.sdk.exceptions;

@SuppressWarnings("serial")
public class PublishedSurveyException extends BridgeSDKException {

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
