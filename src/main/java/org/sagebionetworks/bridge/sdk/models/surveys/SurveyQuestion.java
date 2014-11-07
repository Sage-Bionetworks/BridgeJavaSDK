package org.sagebionetworks.bridge.sdk.models.surveys;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.holders.GuidHolder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class SurveyQuestion implements GuidHolder {

    private String guid;
    private String identifier;
    private String prompt;
    private UiHint hint;
    private Constraints constraints;

    @Override
    public String getGuid() {
        return guid;
    }
    public void setGuid(String guid) {
        this.guid = guid;
    }
    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    public String getPrompt() {
        return prompt;
    }
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    @JsonSerialize(using = EnumSerializer.class)
    @JsonProperty("uiHint")
    public UiHint getUIHint() {
        return hint;
    }
    @JsonDeserialize(using = UiHintDeserializer.class)
    public void setUiHint(UiHint hint) {
        this.hint = hint;
    }
    public Constraints getConstraints() {
        return constraints;
    }
    public void setConstraints(Constraints constraints) {
        this.constraints = constraints;
    }
    @JsonIgnore
    public SurveyAnswer declineAnswerForQuestion(String client) {
        checkArgument(isNotBlank(client), "Client string must indicate client (e.g. mobile or desktop)");
        SurveyAnswer surveyAnswer = new SurveyAnswer();
        surveyAnswer.setQuestionGuid(getGuid());
        surveyAnswer.setAnsweredOn(DateTime.now());
        surveyAnswer.setDeclined(true);
        surveyAnswer.setClient(client);
        return surveyAnswer;
    }
    @JsonIgnore
    public SurveyAnswer createAnswerForQuestion(String answer, String client) {
        checkArgument(isNotBlank(client), "Client string must indicate client (e.g. mobile or desktop)");
        SurveyAnswer surveyAnswer = new SurveyAnswer();
        surveyAnswer.setQuestionGuid(getGuid());
        surveyAnswer.setAnsweredOn(DateTime.now());
        surveyAnswer.setAnswer(answer);
        surveyAnswer.setClient(client);
        return surveyAnswer;
    }
    @JsonIgnore
    public SurveyAnswer createAnswerForQuestion(List<String> answers, String client) {
        checkArgument(isNotBlank(client), "Client string must indicate client (e.g. mobile or desktop)");
        SurveyAnswer surveyAnswer = new SurveyAnswer();
        surveyAnswer.setQuestionGuid(getGuid());
        surveyAnswer.setAnsweredOn(DateTime.now());
        surveyAnswer.setAnswers(answers);
        surveyAnswer.setClient(client);
        return surveyAnswer;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((constraints == null) ? 0 : constraints.hashCode());
        result = prime * result + ((guid == null) ? 0 : guid.hashCode());
        result = prime * result + ((hint == null) ? 0 : hint.hashCode());
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + ((prompt == null) ? 0 : prompt.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SurveyQuestion other = (SurveyQuestion) obj;
        if (constraints == null) {
            if (other.constraints != null)
                return false;
        } else if (!constraints.equals(other.constraints))
            return false;
        if (guid == null) {
            if (other.guid != null)
                return false;
        } else if (!guid.equals(other.guid))
            return false;
        if (hint != other.hint)
            return false;
        if (identifier == null) {
            if (other.identifier != null)
                return false;
        } else if (!identifier.equals(other.identifier))
            return false;
        if (prompt == null) {
            if (other.prompt != null)
                return false;
        } else if (!prompt.equals(other.prompt))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "SurveyQuestion [guid=" + guid + ", identifier=" + identifier + ", prompt=" + prompt + ", hint=" + hint
                + ", constraints=" + constraints + "]";
    }

}
