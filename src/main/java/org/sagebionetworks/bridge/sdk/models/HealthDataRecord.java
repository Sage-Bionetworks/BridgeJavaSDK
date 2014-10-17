package org.sagebionetworks.bridge.sdk.models;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public final class HealthDataRecord {

    private final long version;
    private final String recordId;
    private final DateTime startDate;
    private final DateTime endDate;
    private JsonNode data;

    @JsonCreator
    HealthDataRecord(@JsonProperty("version") long version, @JsonProperty("recordId") String recordId,
            @JsonProperty("startDate") String startDate, @JsonProperty("endDate") String endDate,
            @JsonProperty("data") JsonNode data) {
        this.version = version;
        this.recordId = recordId;
        this.startDate = DateTime.parse(startDate, ISODateTimeFormat.dateTime());
        this.endDate = DateTime.parse(endDate, ISODateTimeFormat.dateTime());
        this.data = data;
    }

    // Need this, but only for testing. How to get around?
    public static HealthDataRecord valueOf(long version, String recordId, DateTime startDate, DateTime endDate, JsonNode data) {
        String start = startDate.toString(ISODateTimeFormat.dateTime());
        String end = endDate.toString(ISODateTimeFormat.dateTime());
        return new HealthDataRecord(version, recordId, start, end, data);
    }

    public long getVersion() { return this.version; }
    public String getRecordId() { return this.recordId; }
    public JsonNode getData() { return this.data; }
    public DateTime getStartDate() { return this.startDate; }
    public DateTime getEndDate() { return this.endDate; }

    public void setData(JsonNode data) { this.data = data; }

    @Override
    public String toString() {
        return "HealthDataRecord[version=" + this.version +
                ", id=" + this.recordId +
                ", data=" + this.data +
                ", startDate=" + startDate.toString(ISODateTimeFormat.dateTime()) +
                ", endDate=" + endDate.toString(ISODateTimeFormat.dateTime()) + "]";
    }

}
