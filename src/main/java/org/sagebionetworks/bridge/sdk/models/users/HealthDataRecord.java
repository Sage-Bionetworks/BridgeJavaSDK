package org.sagebionetworks.bridge.sdk.models.users;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.sdk.models.holders.IdVersionHolder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public final class HealthDataRecord implements IdVersionHolder {

    private final long version;
    private final String recordId;
    private final DateTime startDate;
    private final DateTime endDate;
    private JsonNode data;

    @JsonCreator
    public HealthDataRecord(@JsonProperty("version") long version, @JsonProperty("recordId") String recordId,
            @JsonProperty("startDate") DateTime startDate, @JsonProperty("endDate") DateTime endDate,
            @JsonProperty("data") JsonNode data) {
        this.version = version;
        this.recordId = recordId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.data = data;
    }

    @Override
    public long getVersion() {
        return this.version;
    }

    @Override
    @JsonIgnore
    public String getId() {
        return this.recordId;
    }

    public String getRecordId() {
        return this.recordId;
    }

    public JsonNode getData() {
        return this.data;
    }

    public DateTime getStartDate() {
        return this.startDate;
    }

    public DateTime getEndDate() {
        return this.endDate;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HealthDataRecord[version=" + this.version + ", id=" + this.recordId + ", data=" + this.data
                + ", startDate=" + startDate.toString(ISODateTimeFormat.dateTime()) + ", endDate="
                + endDate.toString(ISODateTimeFormat.dateTime()) + "]";
    }

}
