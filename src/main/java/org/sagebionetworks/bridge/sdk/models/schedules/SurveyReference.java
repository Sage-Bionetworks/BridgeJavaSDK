package org.sagebionetworks.bridge.sdk.models.schedules;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

/**
 * A "soft" reference to a survey that may or may not specify a version with a specific timestamp.
 */
public final class SurveyReference {
    
    private static final Pattern SURVEY_URL = Pattern.compile(".*/surveys/(.*)/(.*)");
    
    public static final boolean isSurveyRef(String ref) {
        return (ref != null && SURVEY_URL.matcher(ref).matches());
    }

    private final String guid;
    private final String createdOn;
    
    public SurveyReference(String ref) {
        checkArgument(isNotBlank(ref));
    
        Matcher match = SURVEY_URL.matcher(ref);
        if (match.matches() && match.groupCount() == 2) {
            this.guid = match.group(1);
            this.createdOn = ("published".equals(match.group(1))) ? null : match.group(2); 
        } else {
            this.guid = null;
            this.createdOn = null;
        }
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
