package org.sagebionetworks.bridge.sdk.models.surveys;

public class DecimalConstraints extends Constraints {

    private Double minValue;
    private Double maxValue;
    private Double step;

    public DecimalConstraints() {
        setDataType(DataType.DECIMAL);
    }

    public Double getMinValue() {
        return minValue;
    }
    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }
    public Double getMaxValue() {
        return maxValue;
    }
    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }
    public Double getStep() {
        return step;
    }
    public void setStep(Double step) {
        this.step = step;
    }

}
