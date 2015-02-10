package org.sagebionetworks.bridge.sdk.models.surveys;

import java.util.List;
import java.util.Objects;

import org.joda.time.DateTime;

import com.google.common.collect.Lists;

public final class SurveyAnswer {

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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(answeredOn);
        result = prime * result + Objects.hashCode(answers);
        result = prime * result + Objects.hashCode(client);
        result = prime * result + Objects.hashCode(questionGuid);
        result = prime * result + (declined ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SurveyAnswer other = (SurveyAnswer) obj;
        return (Objects.equals(answeredOn, other.answeredOn) && Objects.equals(answers, other.answers)
                && Objects.equals(questionGuid, other.questionGuid) && Objects.equals(client, other.client) && declined == other.declined);
    }

    @Override
    public String toString() {
        return String.format("SurveyAnswer [questionGuid=%s, declined=%s, client=%s, answeredOn=%s, answers=%s]", 
                questionGuid, declined, client, answeredOn, answers);
    }

}
