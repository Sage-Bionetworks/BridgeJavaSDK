package org.sagebionetworks.bridge.sdk.models.surveys;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.BridgeSDKException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SurveyResponse {

    // TODO
    private final String guid;
    private final DateTime startedOn;
    private final DateTime completedOn;
    private final Status status;
    private final Survey survey;
    private final List<SurveyAnswer<?>> answers;

    private enum Status { UNSTARTED, IN_PROGRESS, FINISHED }

    @JsonCreator
    private SurveyResponse(@JsonProperty("guid") String guid, @JsonProperty("startedOn") DateTime startedOn,
            @JsonProperty("completedOn") DateTime completedOn, @JsonProperty("status") String status,
            @JsonProperty("survey") Survey survey, @JsonProperty("answers") List<SurveyAnswer<?>> answers) {
        this.guid = guid;
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

    public String getGuid() { return guid; }
    public DateTime getStartedOn() { return startedOn; }

}
