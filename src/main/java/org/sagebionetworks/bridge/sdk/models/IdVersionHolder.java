package org.sagebionetworks.bridge.sdk.models;

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

    public String getId() { return this.id; }
    public long getVersion() { return this.version; }

    @Override
    public String toString() {
        return "IdVersionHolder[id=" + this.id +
                ", version=" + this.version + "]";
    }
}
