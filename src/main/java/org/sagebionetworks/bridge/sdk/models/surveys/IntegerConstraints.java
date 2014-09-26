package org.sagebionetworks.bridge.sdk.models.surveys;

public class IntegerConstraints extends Constraints {
    
    private Long minValue;
    private Long maxValue;
    private Long step;
    
    public Long getMinValue() {
        return minValue;
    }
    public void setMinValue(Long minValue) {
        this.minValue = minValue;
    }
    public Long getMaxValue() {
        return maxValue;
    }
    public void setMaxValue(Long maxValue) {
        this.maxValue = maxValue;
    }
    public Long getStep() {
        return step;
    }
    public void setStep(Long step) {
        this.step = step;
    }

}
