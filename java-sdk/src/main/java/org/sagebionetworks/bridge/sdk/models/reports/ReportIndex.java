package org.sagebionetworks.bridge.sdk.models.reports;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * An index for a type of report, either for studies as a whole or for participants in the 
 * study. Right now, the index just includes the identifier for the study. Note that the 
 * same identifier could be used for both a study and a participant report (e.g. "default"), 
 * these are effectively namespaced by report type.
 */
public final class ReportIndex {

    private final String identifier;
    
    @JsonCreator
    private ReportIndex(@JsonProperty("identifier") String identifier) {
        this.identifier = identifier;
    }
    
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ReportIndex other = (ReportIndex) obj;
        return Objects.equal(identifier, other.identifier);
    }

    @Override
    public String toString() {
        return "ReportIndex [identifier=" + identifier + "]";
    }
}
