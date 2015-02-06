package org.sagebionetworks.bridge.sdk.models.surveys;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SurveyRule {

    public enum Operator {
        eq, // equal to
        ne, // not equal to
        lt, // less than
        gt, // greater than
        le, // less than or equal to
        ge, // greater than or equal to
        de;  // declined to answer
    }

    private Operator operator;
    private Object value;
    private String skipToTarget;

    public SurveyRule() {
    }
    public SurveyRule(Operator operator, Object value, String skipToTarget) {
        this.operator = operator;
        this.value = value;
        this.skipToTarget = skipToTarget;
    }
    public Operator getOperator() {
        return operator;
    }
    public void setOperator(Operator operator) {
        this.operator = operator;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    @JsonProperty("skipTo")
    public String getSkipToTarget() {
        return skipToTarget;
    }
    public void setSkipToTarget(String skipToTarget) {
        this.skipToTarget = skipToTarget;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(skipToTarget);
        result = prime * result + Objects.hashCode(operator);
        result = prime * result + Objects.hashCode(value);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SurveyRule other = (SurveyRule) obj;
        return (Objects.equals(skipToTarget, other.skipToTarget) && Objects.equals(operator, other.operator) && 
                Objects.equals(value, other.value));
    }

    @Override
    public String toString() {
        return String.format("SurveyRule [operator=%s, value=%s, skipToTarget=%s]", operator, value, skipToTarget);
    }

}