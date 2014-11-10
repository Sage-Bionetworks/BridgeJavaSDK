package org.sagebionetworks.bridge.sdk.models.studies;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Tracker {

    private final String name;
    private final String type;
    private final String schemaUrl;
    private final String identifier;

    @JsonCreator
    private Tracker(@JsonProperty("name") String name, @JsonProperty("type") String type,
            @JsonProperty("schemaUrl") String schemaUrl, @JsonProperty("identifier") String identifier) {
        this.name = name;
        this.type = type;
        this.schemaUrl = schemaUrl;
        this.identifier = identifier;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public String getSchemaUrl() {
        return this.schemaUrl;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String toString() {
        return "Tracker[name=" + name + ", type=" + type + ", schemeUrl=" + schemaUrl + ", id=" + identifier + "]";
    }

}
