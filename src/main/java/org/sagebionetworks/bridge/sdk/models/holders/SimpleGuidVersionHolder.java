package org.sagebionetworks.bridge.sdk.models.holders;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class SimpleGuidVersionHolder implements GuidVersionHolder {

    private final String guid;
    private final Long version;
    
    @JsonCreator
    public SimpleGuidVersionHolder(@JsonProperty("guid") String guid, @JsonProperty("version") Long version) {
        checkNotNull(isNotBlank(guid), "%s cannot be blank", "guid");
        this.guid = guid;
        this.version = version;
    }
    
    @Override
    public String getGuid() {
        return guid;
    }

    @Override
    public Long getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        SimpleGuidVersionHolder other = (SimpleGuidVersionHolder) obj;
        return Objects.equals(guid, other.guid) && Objects.equals(version, other.version);
    }

    @Override
    public String toString() {
        return String.format("SimpleGuidVersionHolder [guid=%s, version=%s]", guid, version);
    }

}
