package org.sagebionetworks.bridge.sdk.models.surveys;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class SurveyQuestion {
    
    private String guid;
    private String identifier;
    private String prompt;
    private UIHint hint;
    private Constraints constraints;

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
    public UIHint getUIHint() {
        return hint;
    }
    @JsonDeserialize(using = UIHintDeserializer.class)
    public void setUIHint(UIHint hint) {
        this.hint = hint;
    }
    public Constraints getConstraints() {
        return constraints;
    }
    public void setConstraints(Constraints constraints) {
        this.constraints = constraints;
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
