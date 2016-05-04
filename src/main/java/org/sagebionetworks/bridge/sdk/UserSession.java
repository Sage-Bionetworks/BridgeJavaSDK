package org.sagebionetworks.bridge.sdk;

import static org.sagebionetworks.bridge.sdk.utils.Utilities.TO_STRING_STYLE;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.sagebionetworks.bridge.sdk.json.SubpopulationGuidKeyDeserializer;
import org.sagebionetworks.bridge.sdk.models.subpopulations.ConsentStatus;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;
import org.sagebionetworks.bridge.sdk.models.users.SharingScope;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableMap;

final class UserSession {

    private final String sessionToken;
    private final SharingScope sharingScope;
    private final boolean authenticated;
    private final String id;
    private final Set<String> dataGroups;
    @JsonDeserialize(keyUsing = SubpopulationGuidKeyDeserializer.class)
    private final Map<SubpopulationGuid,ConsentStatus> consentStatuses;

    @JsonCreator
    UserSession(@JsonProperty("sessionToken") String sessionToken,
            @JsonProperty("authenticated") boolean authenticated, 
            @JsonProperty("sharingScope") SharingScope sharingScope,
            @JsonProperty("dataGroups") Set<String> dataGroups,
            @JsonProperty("id") String id,
            @JsonProperty("consentStatuses") Map<SubpopulationGuid,ConsentStatus> consentStatuses) {
        
        this.sessionToken = sessionToken;
        this.authenticated = authenticated;
        this.sharingScope = sharingScope;
        this.dataGroups = dataGroups;
        this.id = id;
        this.consentStatuses = (consentStatuses == null) ? ImmutableMap.<SubpopulationGuid,ConsentStatus>of() : 
            ImmutableMap.copyOf(consentStatuses);
    }

    public String getSessionToken() {
        return this.sessionToken;
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public boolean isConsented() {
        return ConsentStatus.isUserConsented(consentStatuses);
    }
    
    public SharingScope getSharingScope() {
        return this.sharingScope;
    }
    
    public boolean hasSignedMostRecentConsent() {
        return ConsentStatus.isConsentCurrent(consentStatuses);
    }
    
    public Set<String> getDataGroups() {
        return dataGroups;
    }
    
    public String getId() {
        return id;
    }
    
    public Map<SubpopulationGuid,ConsentStatus> getConsentStatuses() { 
        return consentStatuses;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("sessionToken", sessionToken).append("authenticated", authenticated)
                .append("consented", isConsented()).append("sharingScope", sharingScope)
                .append("signedMostRecentConsent", hasSignedMostRecentConsent()).append("dataGroups", dataGroups)
                .append("id", id).append("consentStatuses", consentStatuses).toString();
    }

}
