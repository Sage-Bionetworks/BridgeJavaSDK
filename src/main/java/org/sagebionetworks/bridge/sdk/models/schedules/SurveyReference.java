package org.sagebionetworks.bridge.sdk.models.schedules;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Objects;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A "soft" reference to a survey that may or may not specify a version with a specific timestamp.
 */
public final class SurveyReference {
    
    private final String identifier;
    private final String guid;
    private final DateTime createdOn;
    private final String href;
    
    @JsonCreator
    private SurveyReference(@JsonProperty("identifier") String identifier, @JsonProperty("guid") String guid,
                    @JsonProperty("createdOn") DateTime createdOn, @JsonProperty("href") String href) {
        checkArgument(isNotBlank(identifier));
        checkArgument(isNotBlank(guid));
        this.identifier = identifier;
        this.guid = guid;
        this.createdOn = (createdOn == null) ? null : createdOn;
        this.href = href;
    }
    
    public SurveyReference(String identifier, String guid) {
        this(identifier, guid, null, null);
    }

    public SurveyReference(String identifier, String guid, DateTime createdOn) {
        this(identifier, guid, createdOn, null);
    }
    
    public String getIdentifier() {
        return identifier;
    }
    public String getGuid() {
        return guid;
    }
    public DateTime getCreatedOn() {
        return createdOn;
    }
    @JsonIgnore
    public String getHref() {
        return href;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(createdOn);
        result = prime * result + Objects.hashCode(guid);
        result = prime * result + Objects.hashCode(identifier);
        result = prime * result + Objects.hashCode(href);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SurveyReference other = (SurveyReference) obj;
        return (Objects.equals(createdOn, other.createdOn) && Objects.equals(guid, other.guid) &&
            Objects.equals(identifier, other.identifier) && Objects.equals(href, other.href));
    }

    @Override
    public String toString() {
        return String.format("SurveyReference [identifier=%s, guid=%s, createdOn=%s, href=%s]",
            identifier, guid, createdOn, getHref());
    }    
}
