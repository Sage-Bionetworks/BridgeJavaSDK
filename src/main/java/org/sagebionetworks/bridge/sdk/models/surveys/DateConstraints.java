package org.sagebionetworks.bridge.sdk.models.surveys;

import org.joda.time.DateTime;

public final class DateConstraints extends Constraints {

    private boolean allowFuture = false;
    private DateTime earliestValue;
    private DateTime latestValue;

    public DateConstraints() {
        setDataType(DataType.DATE);
    }

    public boolean getAllowFuture() {
        return allowFuture;
    }
    public void setAllowFuture(boolean allowFuture) {
        this.allowFuture = allowFuture;
    }
    public DateTime getEarliestValue() {
        return earliestValue;
    }
    public void setEarliestValue(DateTime earliestValue) {
        this.earliestValue = earliestValue;
    }
    public DateTime getLatestValue() {
        return latestValue;
    }
    public void setLatestValue(DateTime latestValue) {
        this.latestValue = latestValue;
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
        result = prime * result + (allowFuture ? 1231 : 1237);
        result = prime * result + ((earliestValue == null) ? 0 : earliestValue.hashCode());
        result = prime * result + ((latestValue == null) ? 0 : latestValue.hashCode());
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
        DateConstraints other = (DateConstraints) obj;
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
        if (allowFuture != other.allowFuture)
            return false;
        if (earliestValue == null) {
            if (other.earliestValue != null)
                return false;
        } else if (!earliestValue.equals(other.earliestValue))
            return false;
        if (latestValue == null) {
            if (other.latestValue != null)
                return false;
        } else if (!latestValue.equals(other.latestValue))
            return false;
        return true;
    }
}
