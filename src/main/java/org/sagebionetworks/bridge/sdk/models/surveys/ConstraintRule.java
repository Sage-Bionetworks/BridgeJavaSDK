package org.sagebionetworks.bridge.sdk.models.surveys;

public class ConstraintRule {
    
    private String operator;
    private Object value;
    private String gotoTarget;
    
    public String getOperator() {
        return operator;
    }
    public void setOperator(String operator) {
        this.operator = operator;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    public String getGotoTarget() {
        return gotoTarget;
    }
    public void setGotoTarget(String gotoTarget) {
        this.gotoTarget = gotoTarget;
    }

}
