package org.sagebionetworks.bridge.sdk.models.subpopulations;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

public final class SubpopulationGuid {
    private final String guid;

    public SubpopulationGuid(String guid) {
        checkNotNull(guid);
        this.guid = guid;
    }

    @JsonValue
    public String getGuid() {
        return guid;
    }

    public int hashCode() {
        return Objects.hash(guid);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SubpopulationGuid other = (SubpopulationGuid) obj;
        return Objects.equals(guid, other.guid);
    }

    @Override
    public String toString() {
        return guid;
    }
}
