package org.sagebionetworks.bridge.sdk.models.surveys;

public final class DecimalConstraints extends NumericalConstraints {

    public DecimalConstraints() {
        setDataType(DataType.DECIMAL);
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
        result = prime * result + ((maxValue == null) ? 0 : maxValue.hashCode());
        result = prime * result + ((minValue == null) ? 0 : minValue.hashCode());
        result = prime * result + ((step == null) ? 0 : step.hashCode());
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof DecimalConstraints))
            return false;
        DecimalConstraints other = (DecimalConstraints) obj;
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
        if (maxValue == null) {
            if (other.maxValue != null)
                return false;
        } else if (!maxValue.equals(other.maxValue))
            return false;
        if (minValue == null) {
            if (other.minValue != null)
                return false;
        } else if (!minValue.equals(other.minValue))
            return false;
        if (step == null) {
            if (other.step != null)
                return false;
        } else if (!step.equals(other.step))
            return false;
        if (unit != other.unit)
            return false;
        return true;
    }
}
