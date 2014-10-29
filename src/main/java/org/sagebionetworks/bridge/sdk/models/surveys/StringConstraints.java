package org.sagebionetworks.bridge.sdk.models.surveys;

public class StringConstraints extends Constraints {

    private Integer minLength;
    private Integer maxLength;
    private String pattern;

    public StringConstraints() {
        setDataType(DataType.STRING);
    }

    public Integer getMinLength() {
        return minLength;
    }
    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }
    public Integer getMaxLength() {
        return maxLength;
    }
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }
    public String getPattern() {
        return pattern;
    }
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

}
