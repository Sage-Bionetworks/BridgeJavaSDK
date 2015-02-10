package org.sagebionetworks.bridge.sdk.models.schedules;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

/**
 * A "soft" reference to a survey that may or may not specify a version with a specific timestamp.
 */
public final class SurveyReference {
    
    private static final String SURVEY_PATH_FRAGMENT = "/surveys/";
    private static final String PUBLISHED_FRAGMENT = "published";
    
    public static final boolean isSurveyRef(String ref) {
        return (ref != null && ref.contains(SURVEY_PATH_FRAGMENT));
    }

    private final String guid;
    private final String createdOn;
    
    public SurveyReference(String ref) {
        checkArgument(isNotBlank(ref));
        
        String[] parts = ref.split(SURVEY_PATH_FRAGMENT);
        String guidString = null;
        String createdOnString = null;
        if (parts.length == 2) {
            parts = parts[1].split("/");
            if (parts.length == 2) {
                guidString = parts[0];
                createdOnString = PUBLISHED_FRAGMENT.equals(parts[1]) ? null : parts[1];
            }
        }
        this.guid = guidString;
        this.createdOn = createdOnString;
    }

    public SurveyReference(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        this.guid = guid;
        this.createdOn = (createdOn == null) ? null : createdOn.toString(ISODateTimeFormat.dateTime());
    }

    /**
     * The guid for this survey (will always be present).
     */
    public String getGuid() {
        return guid;
    }

    /**
     * The created date of this survey version, give as an ISO 8601 datetime string 
     * (optional).
     */
    public String getCreatedOn() {
        return createdOn;
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
        return String.format("SurveyReference [guid=%s, createdOn=%s]", guid, createdOn);
    }
    
}
