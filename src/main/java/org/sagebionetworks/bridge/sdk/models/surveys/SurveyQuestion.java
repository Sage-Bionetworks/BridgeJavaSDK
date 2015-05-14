package org.sagebionetworks.bridge.sdk.models.surveys;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;
import java.util.Objects;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.holders.GuidHolder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=SurveyQuestion.class)
public final class SurveyQuestion implements SurveyElement, GuidHolder {

    private String guid;
    private String identifier;
    private String type;
    private String prompt;
    private String promptDetail;
    private boolean fireEvent;
    private UiHint hint;
    private Constraints constraints;

    public SurveyQuestion() {
        setType("SurveyQuestion");
    }
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
    String getType() {
        return type;
    }
    void setType(String type) {
        this.type = type;
    }
    public String getPrompt() {
        return prompt;
    }
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    public String getPromptDetail() {
        return promptDetail;
    }
    public void setPromptDetail(String promptDetail) {
        this.promptDetail = promptDetail;
    }
    public boolean getFireEvent() {
        return fireEvent;
    }
    public void setFireEvent(boolean fireEvent) {
        this.fireEvent = fireEvent;
    }
    @JsonProperty("uiHint")
    public UiHint getUIHint() {
        return hint;
    }
    public void setUiHint(UiHint hint) {
        this.hint = hint;
    }
    public Constraints getConstraints() {
        return constraints;
    }
    @JsonDeserialize(using = ConstraintsDeserializer.class)
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
        surveyAnswer.getAnswers().add(answer);
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
        result = prime * result + Objects.hashCode(constraints);
        result = prime * result + Objects.hashCode(guid);
        result = prime * result + Objects.hashCode(hint);
        result = prime * result + Objects.hashCode(identifier);
        result = prime * result + Objects.hashCode(prompt);
        result = prime * result + Objects.hashCode(promptDetail);
        result = prime * result + Objects.hashCode(fireEvent);
        result = prime * result + Objects.hashCode(type);
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SurveyQuestion other = (SurveyQuestion) obj;
        return (Objects.equals(constraints, other.constraints) && Objects.equals(guid, other.guid)
                && Objects.equals(hint, other.hint) && Objects.equals(identifier, other.identifier)
                && Objects.equals(prompt, other.prompt) && Objects.equals(promptDetail, other.promptDetail) 
                && Objects.equals(fireEvent, other.fireEvent) && Objects.equals(type, other.type));
    }
    @Override
    public String toString() {
        return String.format("SurveyQuestion [guid=%s, identifier=%s, prompt=%s, promptDetail=%s, fireEvent=%s, hint=%s, constraints=%s]", 
                guid, identifier, prompt, promptDetail, fireEvent, hint, constraints);
    }

}
