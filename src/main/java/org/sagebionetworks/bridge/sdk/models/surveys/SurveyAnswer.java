package org.sagebionetworks.bridge.sdk.models.surveys;

import java.util.List;

import org.joda.time.DateTime;

import com.google.common.collect.Lists;

public class SurveyAnswer {

    private String questionGuid;
    private boolean declined;
    private String client;
    private DateTime answeredOn;
    private List<String> answers = Lists.newArrayList();

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

    @Override
    public String toString() {
        return "SurveyAnswer [questionGuid=" + questionGuid + ", declined=" + declined + ", client=" + client
                + ", answeredOn=" + answeredOn + ", answers=" + answers + "]";
    }

}
