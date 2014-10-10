package org.sagebionetworks.bridge.sdk.models;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.sdk.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.Utilities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class HealthDataRecord {

    private static final ObjectMapper mapper = Utilities.getMapper();

    private final long version;
    private final long id;
    private final DateTime startDate;
    private final DateTime endDate;
    private String data;

    @JsonCreator
    HealthDataRecord(@JsonProperty("version") long version, @JsonProperty("recordId") long id,
            @JsonProperty("startDate") String startDate, @JsonProperty("endDate") String endDate,
            @JsonProperty("data") String data) {
        this.version = version;
        this.id = id;
        this.startDate = DateTime.parse(startDate, ISODateTimeFormat.dateTime());
        this.endDate = DateTime.parse(endDate, ISODateTimeFormat.dateTime());
        this.data = data;
    }

    public static HealthDataRecord valueOf(long version, long id, DateTime startDate, DateTime endDate, String data) {
        String start = startDate.toString(ISODateTimeFormat.dateTime());
        String end = endDate.toString(ISODateTimeFormat.dateTime());
        return new HealthDataRecord(version, id, start, end, data);
    }

    public static HealthDataRecord valueOf(String json) {
        if (json == null) {
            throw new IllegalArgumentException("json cannot be null.");
        }

        HealthDataRecord record = null;
        try {
            record = mapper.readValue(json, HealthDataRecord.class);
        } catch (IOException e) {
            throw new BridgeSDKException(
                    "Something went wrong while converting JSON into HealthDataRecord: json="
                            + json, e);
        }
        return record;
    }

    public long getVersion() { return this.version; }
    public long getId() { return this.id; }
    public String getData() { return this.data; }
    public DateTime getStartDate() { return this.startDate; }
    public DateTime getEndDate() { return this.endDate; }

    public void setData(String data) { this.data = data; }

    @Override
    public String toString() {
        return "HealthDataRecord[version=" + this.version +
                ", id=" + this.id +
                ", data=" + this.data +
                ", startDate=" + startDate.toString(ISODateTimeFormat.dateTime()) +
                ", endDate=" + endDate.toString(ISODateTimeFormat.dateTime()) + "]";
    }

}
