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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.toString().hashCode());
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((guid == null) ? 0 : guid.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
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
        HealthDataRecord other = (HealthDataRecord) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.toString().equals(other.data.toString()))
            return false;
        if (endDate == null) {
            if (other.endDate != null)
                return false;
        } else if (!endDate.equals(other.endDate))
            return false;
        if (guid == null) {
            if (other.guid != null)
                return false;
        } else if (!guid.equals(other.guid))
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "HealthDataRecord [version=" + version + ", guid=" + guid + ", startDate=" + startDate + ", endDate="
                + endDate + ", data=" + data + "]";
    }
}
