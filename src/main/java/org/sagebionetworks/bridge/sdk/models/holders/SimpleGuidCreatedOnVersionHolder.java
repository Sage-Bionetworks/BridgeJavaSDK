package org.sagebionetworks.bridge.sdk.models.holders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Objects;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.Bridge;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class SimpleGuidCreatedOnVersionHolder implements GuidCreatedOnVersionHolder {

    private final String guid;
    private final DateTime createdOn;
    private final Long version;

    @JsonCreator
    public SimpleGuidCreatedOnVersionHolder(@JsonProperty("guid") String guid,
            @JsonProperty("createdOn") DateTime createdOn, @JsonProperty("version") Long version) {
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(createdOn, Bridge.CANNOT_BE_NULL, "createdOn");
        
        this.guid = guid;
        this.createdOn = createdOn;
        this.version = version;
    }
    
    public String getGuid() {
        return guid;
    }

    public DateTime getCreatedOn() {
        return createdOn;
    }
    
    public Long getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(createdOn);
        result = prime * result + Objects.hashCode(guid);
        result = prime * result + Objects.hashCode(version);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SimpleGuidCreatedOnVersionHolder other = (SimpleGuidCreatedOnVersionHolder) obj;
        return (Objects.equals(createdOn, other.createdOn) && Objects.equals(guid, other.guid) && Objects.equals(
                version, other.version));
    }

    @Override
    public String toString() {
        return String.format("SimpleGuidCreatedOnVersionHolder [guid=%s, createdOn=$s, version=%s]", 
                guid, createdOn, version);
    }


}
