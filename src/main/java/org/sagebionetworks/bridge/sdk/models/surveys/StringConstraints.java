package org.sagebionetworks.bridge.sdk.models.surveys;

import java.util.Objects;

public final class StringConstraints extends Constraints {

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (allowMultiple ? 1231 : 1237);
        result = prime * result + (allowOther ? 1231 : 1237);
        result = prime * result + Objects.hashCode(dataType);
        result = prime * result + Objects.hashCode(enumeration);
        result = prime * result + Objects.hashCode(rules);
        result = prime * result + Objects.hashCode(maxLength);
        result = prime * result + Objects.hashCode(minLength);
        result = prime * result + Objects.hashCode(pattern);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        StringConstraints other = (StringConstraints) obj;
        return (Objects.equals(dataType, other.dataType) && Objects.equals(enumeration, other.enumeration)
                && Objects.equals(rules, other.rules) && Objects.equals(maxLength, other.maxLength)
                && Objects.equals(minLength, other.minLength) && Objects.equals(pattern, other.pattern)
                && allowMultiple == other.allowMultiple && allowOther == other.allowOther);
    }

}
