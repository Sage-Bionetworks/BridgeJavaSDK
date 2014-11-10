package org.sagebionetworks.bridge.sdk.exceptions;

import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

public class PublishedSurveyException extends BridgeServerException {

    private static final long serialVersionUID = 5150621818895986256L;

    private final Survey survey;

    public PublishedSurveyException(Survey survey, String endpoint) {
        super("A published survey cannot be updated or deleted (only closed).", 400, endpoint);
        this.survey = survey;
    }

    public Survey getSurvey() {
        return survey;
    }

    @Override
    public String toString() {
        return "PublishedSurveyException[message=" + getMessage() +
                ", statusCode=" + getStatusCode() +
                ", endpoint=" + getRestEndpoint() +
                ", survey=" + getSurvey() + "]";
    }

}
