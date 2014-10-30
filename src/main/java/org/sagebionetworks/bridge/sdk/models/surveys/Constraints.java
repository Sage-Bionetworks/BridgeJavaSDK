package org.sagebionetworks.bridge.sdk.models.surveys;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;

// This won't work because it won't pick up multi-valued constraints. We need a more sophisticated
// strategy for this.
@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "dataType")
@JsonSubTypes({
    @Type(name="boolean", value=BooleanConstraints.class),
    @Type(name="integer", value=IntegerConstraints.class),
    @Type(name="decimal", value=DecimalConstraints.class),
    @Type(name="string", value=StringConstraints.class),
    @Type(name="datetime", value=DateTimeConstraints.class),
    @Type(name="date", value=DateConstraints.class),
    @Type(name="time", value=TimeConstraints.class),
    @Type(name="duration", value=DurationConstraints.class)
})
public abstract class Constraints {

    private DataType dataType;
    private List<SurveyRule> rules = Lists.newArrayList();

    // These are specific to the multi value constraints, and should be a separate
    // kind of constraint, but not until the Json parsing stuff above is fixed.
    private List<SurveyQuestionOption> enumeration;
    private boolean allowOther = false;
    private boolean allowMultiple = false;

    @JsonSerialize(using = EnumSerializer.class)
    public DataType getDataType() {
        return dataType;
    }
    @JsonDeserialize(using = DataTypeDeserializer.class)
    void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
    public List<SurveyRule> getRules() {
        return rules;
    }
    public void setRules(List<SurveyRule> rules) {
        this.rules = rules;
    }
    public List<SurveyQuestionOption> getEnumeration() {
        return enumeration;
    }
    public void setEnumeration(List<SurveyQuestionOption> enumeration) {
        this.enumeration = enumeration;
    }
    public boolean getAllowOther() {
        return allowOther;
    }
    public void setAllowOther(boolean allowOther) {
        this.allowOther = allowOther;
    }
    public boolean getAllowMultiple() {
        return allowMultiple;
    }
    public void setAllowMultiple(boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (allowMultiple ? 1231 : 1237);
        result = prime * result + (allowOther ? 1231 : 1237);
        result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
        result = prime * result + ((enumeration == null) ? 0 : enumeration.hashCode());
        result = prime * result + ((rules == null) ? 0 : rules.hashCode());
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
        Constraints other = (Constraints) obj;
        if (allowMultiple != other.allowMultiple)
            return false;
        if (allowOther != other.allowOther)
            return false;
        if (dataType != other.dataType)
            return false;
        if (enumeration == null) {
            if (other.enumeration != null)
                return false;
        } else if (!enumeration.equals(other.enumeration))
            return false;
        if (rules == null) {
            if (other.rules != null)
                return false;
        } else if (!rules.equals(other.rules))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "Constraints [dataType=" + dataType + ", rules=" + rules + ", enumeration=" + enumeration
                + ", allowOther=" + allowOther + ", allowMultiple=" + allowMultiple + "]";
    }


}
