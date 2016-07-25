package org.sagebionetworks.bridge.sdk.models.surveys;

import static org.sagebionetworks.bridge.sdk.utils.Utilities.TO_STRING_STYLE;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder=SurveyRule.Builder.class)
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
    
    public static class Builder {
        private Operator operator;
        private Object value;
        private String skipToTarget;
        private Boolean endSurvey;
        
        public Builder withOperator(SurveyRule.Operator operator) {
            this.operator = operator;
            return this;
        }
        public Builder withValue(Object value) {
            this.value = value;
            return this;
        }
        @JsonProperty("skipTo")
        public Builder withSkipToTarget(String skipTo) {
            this.skipToTarget = skipTo;
            return this;
        }
        public Builder withEndSurvey(Boolean endSurvey) {
            if (Boolean.TRUE.equals(endSurvey)) {
                this.endSurvey = endSurvey;    
            }
            return this;
        }
        public SurveyRule build() {
            return new SurveyRule(operator, value, skipToTarget, endSurvey);
        }
    }    
}