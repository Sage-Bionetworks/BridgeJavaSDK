package org.sagebionetworks.bridge.sdk.models.surveys;

public final class TimeConstraints extends Constraints {

    public TimeConstraints() {
        setDataType(DataType.TIME);
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
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TimeConstraints other = (TimeConstraints) obj;
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
        return true;
    }
}
