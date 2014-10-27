package org.sagebionetworks.bridge.sdk.models;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class IdVersionHolder {

    private final String id;
    private final long version;

    @JsonCreator
    private IdVersionHolder(@JsonProperty("id") String id, @JsonProperty("version") long version) {
        this.id = id;
        this.version = version;
    }

    public static IdVersionHolder valueOf(String id, long version) {
        return new IdVersionHolder(id, version);
    }

    public String getId() { return this.id; }
    public long getVersion() { return this.version; }

    @Override
    public String toString() {
        return "IdVersionHolder[id=" + this.id +
                ", version=" + this.version + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        }
        final IdVersionHolder that = (IdVersionHolder) obj;
        return Objects.equals(this.id, that.id) && Objects.equals(this.version, that.version);
    }
}
