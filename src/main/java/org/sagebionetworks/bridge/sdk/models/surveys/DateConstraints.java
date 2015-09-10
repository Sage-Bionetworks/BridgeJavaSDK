package org.sagebionetworks.bridge.sdk.models.surveys;

import java.util.Objects;

import org.joda.time.LocalDate;

public final class DateConstraints extends Constraints {

    private boolean allowFuture = false;
    private LocalDate earliestValue;
    private LocalDate latestValue;

    public DateConstraints() {
        setDataType(DataType.DATE);
    }

    public boolean getAllowFuture() {
        return allowFuture;
    }
    public void setAllowFuture(boolean allowFuture) {
        this.allowFuture = allowFuture;
    }
    public LocalDate getEarliestValue() {
        return earliestValue;
    }
    public void setEarliestValue(LocalDate earliestValue) {
        this.earliestValue = earliestValue;
    }
    public LocalDate getLatestValue() {
        return latestValue;
    }
    public void setLatestValue(LocalDate latestValue) {
        this.latestValue = latestValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (allowMultiple ? 1231 : 1237);
        result = prime * result + (allowOther ? 1231 : 1237);
        result = prime * result + (allowFuture ? 1231 : 1237);
        result = prime * result + Objects.hashCode(dataType);
        result = prime * result + Objects.hashCode(enumeration);
        result = prime * result + Objects.hashCode(rules);
        result = prime * result + Objects.hashCode(earliestValue);
        result = prime * result + Objects.hashCode(latestValue);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        DateConstraints other = (DateConstraints) obj;
        return (Objects.equals(dataType, other.dataType) && Objects.equals(enumeration, other.enumeration)
                && Objects.equals(rules, other.rules) && Objects.equals(earliestValue, other.earliestValue)
                && Objects.equals(latestValue, other.latestValue) && allowMultiple == other.allowMultiple
                && allowOther == other.allowOther && allowFuture == other.allowFuture);
    }
}
