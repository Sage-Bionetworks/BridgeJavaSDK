package org.sagebionetworks.bridge.sdk.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Tracker {

    private final String name;
    private final String type;
    private final String schemaUrl;
    private final long id;

    @JsonCreator
    private Tracker(@JsonProperty("name") String name, @JsonProperty("type") String type,
            @JsonProperty("schemaUrl") String schemaUrl, @JsonProperty("id") long id) {
        this.name = name;
        this.type = type;
        this.schemaUrl = schemaUrl;
        this.id = id;
    }

    public String getName() { return this.name; }
    public String getType() { return this.type; }
    public String getSchemaUrl() { return this.schemaUrl; }
    public long getId() { return this.id; }

}
