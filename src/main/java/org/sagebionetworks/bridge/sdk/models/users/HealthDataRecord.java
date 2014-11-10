package org.sagebionetworks.bridge.sdk.models.users;

import org.joda.time.DateTime;

import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.sdk.models.GuidVersionHolder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=HealthDataRecord.class) // strangely, Jackson needs this
public final class HealthDataRecord implements GuidVersionHolder {

    private Long version;
    private String guid;
    private DateTime startDate;
    private DateTime endDate;
    private JsonNode data;

    @JsonCreator
    public HealthDataRecord(@JsonProperty("version") long version, @JsonProperty("guid") String guid,
            @JsonProperty("startDate") DateTime startDate, @JsonProperty("endDate") DateTime endDate,
            @JsonProperty("data") JsonNode data) {
        this.version = version;
        this.guid = guid;
        this.startDate = startDate;
        this.endDate = endDate;
        this.data = data;
    }
    
    public HealthDataRecord() {
    }

    public Long getVersion() {
        return this.version;
    }

    public String getGuid() {
        return this.guid;
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
        return "HealthDataRecord[version=" + this.version + ", guid=" + this.guid + ", data=" + this.data
                + ", startDate=" + startDate.toString(ISODateTimeFormat.dateTime()) + ", endDate="
                + endDate.toString(ISODateTimeFormat.dateTime()) + "]";
    }

}
