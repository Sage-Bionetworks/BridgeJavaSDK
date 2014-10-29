package org.sagebionetworks.bridge.sdk.models.surveys;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SurveyAnswer {

    private final String questionGuid;
    private final boolean declined;
    private final String answer;
    private final String client;
    private final DateTime answeredOn;

    @JsonCreator
    private SurveyAnswer(@JsonProperty("questionGuid") String questionGuid, @JsonProperty("declined") boolean declined,
            @JsonProperty("answer") String answer, @JsonProperty("answeredOn") DateTime answeredOn,
            @JsonProperty("client") String client) {
        this.questionGuid = questionGuid;
        this.declined = declined;
        this.answer = answer;
        this.client = client;
        this.answeredOn = answeredOn;
    }

    public static SurveyAnswer valueOf(String questionId, boolean declined, String answer, DateTime answeredOn, String client) {
        return new SurveyAnswer(questionId, declined, answer, answeredOn, client);
    }

    public String getQuestionGuid() { return questionGuid; }
    public boolean isDeclined() { return declined; }
    public String getAnswer() { return answer; }
    public String getClient() { return client; }
    public DateTime getAnsweredOn() { return answeredOn; }

}
