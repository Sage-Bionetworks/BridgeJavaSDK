package org.sagebionetworks.bridge.sdk.models.surveys;

import org.sagebionetworks.bridge.sdk.BridgeSDKException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SurveyRule {

    public enum Operator {
        EQ, // equal to
        NE, // not equal to
        LT, // less than
        GT, // greater than
        LE, // less than or equal to
        GE, // greater than or equal to
        DE;  // declined to answer

        @JsonCreator
        public static Operator fromEnum(String s) {
            if (s.equals("eq")) {
                return EQ;
            } else if (s.equals("ne")) {
                return NE;
            } else if (s.equals("lt")) {
                return LT;
            } else if (s.equals("gt")) {
                return GT;
            } else if (s.equals("le")) {
                return LE;
            } else if (s.equals("ge")) {
                return GE;
            } else if (s.equals("de")) {
                return DE;
            } else {
                throw new BridgeSDKException("Something went wrong while converting string to Operator. s=" + s);
            }
        }
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
        result = prime * result + ((skipToTarget == null) ? 0 : skipToTarget.hashCode());
        result = prime * result + ((operator == null) ? 0 : operator.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        SurveyRule other = (SurveyRule) obj;
        if (skipToTarget == null) {
            if (other.skipToTarget != null)
                return false;
        } else if (!skipToTarget.equals(other.skipToTarget))
            return false;
        if (operator != other.operator)
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SurveyRule [operator=" + operator + ", value=" + value + ", skipToTarget=" + skipToTarget + "]";
    }

}