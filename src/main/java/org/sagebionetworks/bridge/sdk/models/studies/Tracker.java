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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((schemaUrl == null) ? 0 : schemaUrl.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        Tracker other = (Tracker) obj;
        if (identifier == null) {
            if (other.identifier != null)
                return false;
        } else if (!identifier.equals(other.identifier))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (schemaUrl == null) {
            if (other.schemaUrl != null)
                return false;
        } else if (!schemaUrl.equals(other.schemaUrl))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Tracker[name=" + name + ", type=" + type + ", schemeUrl=" + schemaUrl + ", id=" + identifier + "]";
    }

}
