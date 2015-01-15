package org.sagebionetworks.bridge.sdk.models.surveys;

public class DurationConstraints extends Constraints {

    private DurationUnit unit;
    private Long minValue;
    private Long maxValue;
    
    public DurationConstraints() {
        setDataType(DataType.DURATION);
    }
    
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
    public DurationUnit getUnit() {
        return unit;
    }
    public void setUnit(DurationUnit unit) {
        this.unit = unit;
    }
    
}
