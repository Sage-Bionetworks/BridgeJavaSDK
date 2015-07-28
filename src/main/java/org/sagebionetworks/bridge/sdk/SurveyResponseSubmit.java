package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyAnswer;

class SurveyResponseSubmit {

    private String surveyGuid;
    private DateTime surveyCreatedOn;
    private String identifier;
    private final List<SurveyAnswer> answers;
    
    SurveyResponseSubmit(GuidCreatedOnVersionHolder keys, String identifier, List<SurveyAnswer> answers) {
        this.surveyGuid = (keys == null) ? null : keys.getGuid();
        this.surveyCreatedOn = (keys == null) ? null : keys.getCreatedOn();
        this.identifier = identifier;
        this.answers = answers;
    }

    public String getSurveyGuid() {
        return surveyGuid;
    }

    public DateTime getSurveyCreatedOn() {
        return surveyCreatedOn;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<SurveyAnswer> getAnswers() {
        return answers;
    }
    
}
