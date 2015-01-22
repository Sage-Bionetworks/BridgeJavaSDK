package org.sagebionetworks.bridge.sdk.models.surveys;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;

public abstract class Constraints {

    protected DataType dataType;
    protected List<SurveyRule> rules = Lists.newArrayList();

    // These are specific to the multi value constraints, and should be a separate
    // kind of constraint, but not until the Json parsing stuff above is fixed.
    protected List<SurveyQuestionOption> enumeration;
    protected boolean allowOther = false;
    protected boolean allowMultiple = false;

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

}
