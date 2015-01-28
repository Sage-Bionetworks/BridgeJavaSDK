package org.sagebionetworks.bridge.sdk.models.schedules;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A soft reference to a survey that may or may not specify a specific version.
 * @author alxdark
 *
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
        checkNotNull(ref);
        String[] parts = ref.split(SURVEY_PATH_FRAGMENT)[1].split("/");
        this.guid = parts[0];
        this.createdOn = PUBLISHED_FRAGMENT.equals(parts[1]) ? null : parts[1];
    }
    
    public String getGuid() {
        return guid;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
        result = prime * result + ((guid == null) ? 0 : guid.hashCode());
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
        SurveyReference other = (SurveyReference) obj;
        if (createdOn == null) {
            if (other.createdOn != null)
                return false;
        } else if (!createdOn.equals(other.createdOn))
            return false;
        if (guid == null) {
            if (other.guid != null)
                return false;
        } else if (!guid.equals(other.guid))
            return false;
        return true;
    }
    
}
