package org.sagebionetworks.bridge.sdk.models.surveys;

import java.util.List;

public class MultiValueConstraints extends Constraints {
    
    private List<SurveyQuestionOption> enumeration;
    private boolean allowOther = false;
    private boolean allowMultiple = false;
    
    public MultiValueConstraints() {
        setDataType(DataType.STRING);
    }
    
    public MultiValueConstraints(DataType dataType) {
        setDataType(dataType);
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
