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
    
    private final String guid;
    private final DateTime createdOn;
    private final String href;
    
    @JsonCreator
    private SurveyReference(@JsonProperty("guid") String guid, @JsonProperty("createdOn") DateTime createdOn,
            @JsonProperty("href") String href) {
        checkArgument(isNotBlank(guid));
        this.guid = guid;
        this.createdOn = (createdOn == null) ? null : createdOn;
        this.href = href;
    }
    
    public SurveyReference(String guid) {
        this(guid, null, null);
    }

    public SurveyReference(String guid, DateTime createdOn) {
        this(guid, createdOn, null);
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
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SurveyReference other = (SurveyReference) obj;
        return (Objects.equals(createdOn, other.createdOn) && Objects.equals(guid, other.guid));
    }

    @Override
    public String toString() {
        return String.format("SurveyReference [guid=%s, createdOn=%s, href=%s]",
            guid, createdOn, getHref());
    }    
}
