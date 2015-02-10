package org.sagebionetworks.bridge.sdk.models.users;

import java.util.Objects;

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
    
    public void setGuid(String guid) {
        this.guid = guid;
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(data);
        result = prime * result + Objects.hashCode(endDate);
        result = prime * result + Objects.hashCode(guid);
        result = prime * result + Objects.hashCode(startDate);
        result = prime * result + Objects.hashCode(version);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        HealthDataRecord other = (HealthDataRecord) obj;
        return (Objects.equals(data, other.data) && Objects.equals(endDate, other.endDate)
                && Objects.equals(startDate, other.startDate) && Objects.equals(guid, other.guid) && Objects.equals(
                version, other.version));
    }

    @Override
    public String toString() {
        return String.format("HealthDataRecord [version=%s, guid=%s, startDate=%s, endDate=%s, data=%s]", 
                version, guid, startDate, endDate, data);
    }
}
