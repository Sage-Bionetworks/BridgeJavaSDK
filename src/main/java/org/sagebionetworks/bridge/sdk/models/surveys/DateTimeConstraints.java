package org.sagebionetworks.bridge.sdk.models.surveys;


public class DateTimeConstraints extends Constraints {

    private boolean allowFuture = false;

    public DateTimeConstraints() {
        setDataType(DataType.DATETIME);
    }

    public boolean getAllowFuture() {
        return allowFuture;
    }
    public void setAllowFuture(boolean allowFuture) {
        this.allowFuture = allowFuture;
    }
}
