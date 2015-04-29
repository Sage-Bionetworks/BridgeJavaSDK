package org.sagebionetworks.bridge.sdk.exceptions;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.sdk.models.schedules.SurveyReference;

public class PublishedSurveyException extends BridgeServerException {

    private static final long serialVersionUID = 5150621818895986256L;

    private final SurveyReference reference;

    public PublishedSurveyException(String message, String endpoint) {
        super(message, 400, endpoint);
        this.reference = new SurveyReference(endpoint);
    }

    public String getGuid() {
        return reference.getGuid();
    }

    public DateTime getCreatedOn() {
        return reference.getCreatedOnDateTime();
    }

    @Override
    public String toString() {
        return "PublishedSurveyException[message=" + getMessage() +
                ", statusCode=" + getStatusCode() +
                ", endpoint=" + getRestEndpoint() +
                ", surveyGuid=" + getGuid() +
                ", surveyCreatedOn=" + getCreatedOn() + "]";
    }

}
