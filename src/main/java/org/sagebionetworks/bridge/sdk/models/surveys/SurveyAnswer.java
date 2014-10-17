package org.sagebionetworks.bridge.sdk.models.surveys;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SurveyAnswer<T> {

    private final String questionGuid;
    private final boolean declined;
    private final T answer;
    private final String client;
    private final DateTime answeredOn;

    @JsonCreator
    private SurveyAnswer(@JsonProperty("questionGuid") String questionGuid, @JsonProperty("declined") boolean declined,
            @JsonProperty("answer") T answer, @JsonProperty("client") String client,
            @JsonProperty("answeredOn") DateTime answeredOn) {
        this.questionGuid = questionGuid;
        this.declined = declined;
        this.answer = answer;
        this.client = client;
        this.answeredOn = answeredOn;
    }

    public String getQuestionGuid() { return questionGuid; }
    public boolean isDeclined() { return declined; }
    public T getAnswer() { return answer; }
    public String getClient() { return client; }
    public DateTime getAnsweredOn() { return answeredOn; }

}
