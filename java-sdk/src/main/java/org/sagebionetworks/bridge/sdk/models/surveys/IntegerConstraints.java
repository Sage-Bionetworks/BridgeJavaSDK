package org.sagebionetworks.bridge.sdk.models.surveys;

import java.util.Objects;

public final class IntegerConstraints extends NumericalConstraints {

    public IntegerConstraints() {
        setDataType(DataType.INTEGER);
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
        result = prime * result + Objects.hashCode(maxValue);
        result = prime * result + Objects.hashCode(minValue);
        result = prime * result + Objects.hashCode(step);
        result = prime * result + Objects.hashCode(unit);
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        IntegerConstraints other = (IntegerConstraints) obj;
        return (Objects.equals(dataType, other.dataType) && Objects.equals(enumeration, other.enumeration)
                && Objects.equals(rules, other.rules) && Objects.equals(maxValue, other.maxValue)
                && Objects.equals(minValue, other.minValue) && Objects.equals(step, other.step)
                && Objects.equals(unit, other.unit) && allowMultiple == other.allowMultiple && allowOther == other.allowOther);
    }
}
