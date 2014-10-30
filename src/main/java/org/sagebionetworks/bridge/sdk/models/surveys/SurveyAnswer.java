package org.sagebionetworks.bridge.sdk.models.surveys;

import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

public class SurveyAnswer {

    private String questionGuid;
    private boolean declined;
    private String client;
    private DateTime answeredOn;
    private String answer;
    private List<String> answers = Lists.newArrayList();

    @JsonCreator
    private SurveyAnswer(@JsonProperty("questionGuid") String questionGuid, @JsonProperty("declined") boolean declined,
            @JsonProperty("answer") String answer, @JsonProperty("answers") List<String> answers,
            @JsonProperty("answeredOn") DateTime answeredOn, @JsonProperty("client") String client) {
        this.questionGuid = questionGuid;
        this.declined = declined;
        this.answer = answer;
        this.client = client;
        this.answeredOn = answeredOn;
        this.answers = answers;
    }

    public SurveyAnswer() {
    }

    public String getQuestionGuid() {
        return questionGuid;
    }

    public void setQuestionGuid(String questionGuid) {
        this.questionGuid = questionGuid;
    }

    public boolean isDeclined() {
        return declined;
    }

    public void setDeclined(boolean declined) {
        this.declined = declined;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }
    
    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public DateTime getAnsweredOn() {
        return answeredOn;
    }
    
    public void setAnsweredOn(DateTime answeredOn) {
        this.answeredOn = answeredOn;
    }

}
