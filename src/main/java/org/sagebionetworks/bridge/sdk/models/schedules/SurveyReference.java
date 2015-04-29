package org.sagebionetworks.bridge.sdk.models.schedules;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

/**
 * A "soft" reference to a survey that may or may not specify a version with a specific timestamp.
 */
public final class SurveyReference {
    
    private static final Pattern p = Pattern.compile("/surveys/(.*)/revisions/(.*)");
    private static final String SURVEY_PATH_FRAGMENT = "/surveys/";
    private static final String PUBLISHED_FRAGMENT = "published";
    
    public static final boolean isSurveyRef(String ref) {
        return (ref != null && ref.contains(SURVEY_PATH_FRAGMENT));
    }

    private final String guid;
    private final String createdOn;
    
    public SurveyReference(String ref) {
        checkNotNull(ref);
        Matcher m = p.matcher(ref);
        m.find();
        this.guid = m.group(1);
        this.createdOn = (PUBLISHED_FRAGMENT.equals(m.group(2))) ? null : m.group(2);
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
    
    public DateTime getCreatedOnDateTime() {
        return (createdOn == null) ? null : ISODateTimeFormat.dateTime().parseDateTime(createdOn);
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
