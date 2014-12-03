package org.sagebionetworks.bridge.sdk.models.surveys;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.models.holders.IdentifierHolder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=SurveyResponse.class) // Strangely, Jackson needs this.
public class SurveyResponse implements IdentifierHolder {

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
                    "Status string is not one of the three defined: unstarted, in_progress, finished");
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

}
