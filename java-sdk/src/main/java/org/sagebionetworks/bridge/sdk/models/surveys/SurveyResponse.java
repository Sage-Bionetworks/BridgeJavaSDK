package org.sagebionetworks.bridge.sdk.models.surveys;

import java.util.List;
import java.util.Objects;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.models.holders.IdentifierHolder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=SurveyResponse.class)
public final class SurveyResponse implements IdentifierHolder {

    private final String identifier;
    private final DateTime startedOn;
    private final DateTime completedOn;
    private final Status status;
    private final Survey survey;
    private final List<SurveyAnswer> answers;

    private enum Status {
        UNSTARTED, IN_PROGRESS, FINISHED
    }

    @JsonCreator
    private SurveyResponse(@JsonProperty("identifier") String identifier,
            @JsonProperty("startedOn") DateTime startedOn, @JsonProperty("completedOn") DateTime completedOn,
            @JsonProperty("status") String status, @JsonProperty("survey") Survey survey,
            @JsonProperty("answers") List<SurveyAnswer> answers) {
        this.identifier = identifier;
        this.startedOn = startedOn;
        this.completedOn = completedOn;
        this.survey = survey;
        this.answers = answers;

        switch (status) {
        case "unstarted":
            this.status = Status.UNSTARTED;
            break;
        case "in_progress":
            this.status = Status.IN_PROGRESS;
            break;
        case "finished":
            this.status = Status.FINISHED;
            break;
        default:
            throw new BridgeSDKException(
                    "Status string is not one of the three defined: unstarted, in_progress, finished", 400);
        }
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public DateTime getStartedOn() {
        return startedOn;
    }

    public DateTime getCompletedOn() {
        return completedOn;
    }

    public Status getStatus() {
        return status;
    }

    public Survey getSurvey() {
        return survey;
    }

    public List<SurveyAnswer> getSurveyAnswers() {
        return answers;
    }

    public SurveyAnswer getAnswerByQuestionGuid(String questionGuid) {
        for (SurveyAnswer answer : answers) {
            if (answer.getQuestionGuid().equals(questionGuid)) {
                return answer;
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(answers);
        result = prime * result + Objects.hashCode(completedOn);
        result = prime * result + Objects.hashCode(identifier);
        result = prime * result + Objects.hashCode(startedOn);
        result = prime * result + Objects.hashCode(status);
        result = prime * result + Objects.hashCode(survey);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SurveyResponse other = (SurveyResponse) obj;
        return (Objects.equals(answers, other.answers) && Objects.equals(completedOn, other.completedOn)
                && Objects.equals(identifier, other.identifier) && Objects.equals(startedOn, other.startedOn)
                && Objects.equals(status, other.status) && Objects.equals(survey, other.survey));
    }
    
    @Override
    public String toString() {
        return String.format("SurveyResponse [identifier=%s, startedOn=%s, completedOn=%s, status=%s, survey=%s, answers=%s]", 
                identifier, startedOn, completedOn, status, survey, answers);
    }
}
