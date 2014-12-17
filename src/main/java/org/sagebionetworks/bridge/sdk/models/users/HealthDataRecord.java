package org.sagebionetworks.bridge.sdk.models.users;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=HealthDataRecord.class)
public final class HealthDataRecord implements GuidVersionHolder {

    private Long version;
    private String guid;
    private DateTime startDate;
    private DateTime endDate;
    private JsonNode data;

    @JsonCreator
    private HealthDataRecord(@JsonProperty("version") long version, @JsonProperty("guid") String guid,
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
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getGuid() {
        return guid;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HealthDataRecord [version=" + version + ", guid=" + guid + ", startDate=" + startDate + ", endDate="
                + endDate + ", data=" + data + "]";
    }
}
