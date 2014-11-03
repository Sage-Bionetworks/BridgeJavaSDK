package org.sagebionetworks.bridge.sdk.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class SimpleIdVersionHolder implements IdVersionHolder {

    private final String id;
    private final long version;

    @JsonCreator
    public SimpleIdVersionHolder(@JsonProperty("id") String id, @JsonProperty("version") long version) {
        this.id = id;
        this.version = version;
    }

    public String getId() {
        return this.id;
    }

    public long getVersion() {
        return this.version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + (int) (version ^ (version >>> 32));
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
        SimpleIdVersionHolder other = (SimpleIdVersionHolder) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (version != other.version)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SimpleIdVersionHolder [id=" + id + ", version=" + version + "]";
    }

}
