package org.sagebionetworks.bridge.sdk.models.surveys;

import static org.sagebionetworks.bridge.sdk.utils.Utilities.TO_STRING_STYLE;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class SurveyRule {

    public enum Operator {
        /** Equal to. */
        EQ,
        /** Not equal to. */
        NE,
        /** Less than. */
        LT,
        /** Greater than. */
        GT,
        /** Less than or equal to. */
        LE,
        /** Greater than or equal to. */
        GE,
        /** Declined to answer. */
        DE;
    }

    private final Operator operator;
    private final Object value;
    private final String skipToTarget;
    private final Boolean endSurvey;

    @JsonCreator
    private SurveyRule(@JsonProperty("operator") Operator operator, @JsonProperty("value") Object value,
            @JsonProperty("skipTo") String skipToTarget, @JsonProperty("endSurvey") Boolean endSurvey) {
        this.operator = operator;
        this.value = value;
        this.skipToTarget = skipToTarget;
        this.endSurvey = Boolean.TRUE.equals(endSurvey) ? Boolean.TRUE : null;
    }
    public SurveyRule(Operator operator, Object value, String skipToTarget) {
        this(operator, value, skipToTarget, null);
    }
    public SurveyRule(Operator operator, Object value) {
        this(operator, value, null, Boolean.TRUE);
    }
    
    public Operator getOperator() {
        return operator;
    }
    public Object getValue() {
        return value;
    }
    @JsonProperty("skipTo")
    public String getSkipToTarget() {
        return skipToTarget;
    }
    public Boolean getEndSurvey() {
        return endSurvey;
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, value, skipToTarget, endSurvey);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SurveyRule other = (SurveyRule) obj;
        return Objects.equals(skipToTarget, other.skipToTarget) &&
               Objects.equals(operator, other.operator) &&
               Objects.equals(value, other.value) &&
               Objects.equals(endSurvey, other.endSurvey);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE).append("operator", operator)
                .append("value", value).append("skipTo", skipToTarget)
                .append("endSurvey", endSurvey).toString();
    }
}