package org.sagebionetworks.bridge.sdk.models;

import java.io.IOException;

import org.sagebionetworks.bridge.sdk.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.Utilities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IdVersionHolder {

    private static final ObjectMapper mapper = Utilities.getMapper();

    private final String id;
    private final long version;

    @JsonCreator
    private IdVersionHolder(@JsonProperty("id") String id, @JsonProperty("version") long version) {
        this.id = id;
        this.version = version;
    }

    public static IdVersionHolder valueOf(String json) {
        assert json != null;

        IdVersionHolder holder = null;
        try {
            holder = mapper.readValue(json, IdVersionHolder.class);
        } catch (IOException e) {
            throw new BridgeSDKException(
                    "Something went wrong while converting JSON into IdVersionHolder: json="
                            + json, e);
        }
        return holder;
    }

    public String getId() { return this.id; }
    public long getVersion() { return this.version; }

    @Override
    public String toString() {
        return "IdVersionHolder[id=" + this.id +
                ", version=" + this.version + "]";
    }
}
