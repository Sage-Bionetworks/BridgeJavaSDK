package org.sagebionetworks.bridge.sdk.models.studies;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Tracker {

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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(identifier);
        result = prime * result + Objects.hashCode(name);
        result = prime * result + Objects.hashCode(schemaUrl);
        result = prime * result + Objects.hashCode(type);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Tracker other = (Tracker) obj;
        return (Objects.equals(identifier, other.identifier) && Objects.equals(name, other.name)
                && Objects.equals(schemaUrl, other.schemaUrl) && Objects.equals(type, other.type));
    }

    @Override
    public String toString() {
        return String.format("Tracker[name=%s, type=%s, schemeUrl=%s, id=%s]", name, type, schemaUrl, identifier);
    }

}
