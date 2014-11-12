package org.sagebionetworks.bridge.sdk.exceptions;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

public class PublishedSurveyException extends BridgeServerException {

    private static final long serialVersionUID = 5150621818895986256L;

    private final String surveyGuid;
    private final DateTime surveyCreatedOn;

    public PublishedSurveyException(String message, String endpoint) {
        super(message, 400, endpoint);
        this.surveyGuid = guidFromUrl(endpoint);
        this.surveyCreatedOn = createdOnFromUrl(endpoint);


    }

    public String getGuid() {
        return surveyGuid;
    }

    public DateTime getCreatedOn() {
        return surveyCreatedOn;
    }

    @Override
    public String toString() {
        return "PublishedSurveyException[message=" + getMessage() +
                ", statusCode=" + getStatusCode() +
                ", endpoint=" + getRestEndpoint() +
                ", surveyGuid=" + getGuid() +
                ", surveyCreatedOn=" + getCreatedOn() + "]";
    }

    private DateTime createdOnFromUrl(String url) {
        int createdOnIndex = url.lastIndexOf("/") + 1;
        System.out.println(url.substring(createdOnIndex));
        System.out.println(ISODateTimeFormat.dateTime().parseDateTime(url.substring(createdOnIndex)));
        return ISODateTimeFormat.dateTime().parseDateTime(url.substring(createdOnIndex));
    }

    private String guidFromUrl(String url) {
        int guidEndIndex = url.lastIndexOf("/"); // substring exclusive end-index
        int guidStartIndex = guidEndIndex - 36;
        return url.substring(guidStartIndex, guidEndIndex);
    }

}
