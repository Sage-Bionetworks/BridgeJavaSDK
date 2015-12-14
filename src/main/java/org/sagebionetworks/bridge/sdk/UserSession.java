package org.sagebionetworks.bridge.sdk;

import static org.sagebionetworks.bridge.sdk.utils.Utilities.TO_STRING_STYLE;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.sagebionetworks.bridge.sdk.json.DataGroupsDeserializer;
import org.sagebionetworks.bridge.sdk.json.DataGroupsSerializer;
import org.sagebionetworks.bridge.sdk.models.subpopulations.ConsentStatus;
import org.sagebionetworks.bridge.sdk.models.users.DataGroups;
import org.sagebionetworks.bridge.sdk.models.users.SharingScope;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;

final class UserSession {

    private final String username;
    private final String sessionToken;
    private final SharingScope sharingScope;
    private final boolean authenticated;
    private final boolean consented;
    private final boolean signedMostRecentConsent;
    @JsonDeserialize(using=DataGroupsDeserializer.class)
    @JsonSerialize(using=DataGroupsSerializer.class)
    private final DataGroups dataGroups;
    private final List<ConsentStatus> consentStatuses;

    @JsonCreator
    private UserSession(@JsonProperty("username") String username, @JsonProperty("sessionToken") String sessionToken,
            @JsonProperty("authenticated") boolean authenticated, @JsonProperty("consented") boolean consented,
            @JsonProperty("sharingScope") SharingScope sharingScope,
            @JsonProperty("signedMostRecentConsent") boolean signedMostRecentConsent,
            @JsonProperty("dataGroups") DataGroups dataGroups,
            @JsonProperty("consentStatuses") List<ConsentStatus> consentStatuses) {
        
        this.username = username;
        this.sessionToken = sessionToken;
        this.consented = consented;
        this.authenticated = authenticated;
        this.sharingScope = sharingScope;
        this.signedMostRecentConsent = signedMostRecentConsent;
        this.dataGroups = dataGroups;
        this.consentStatuses = (consentStatuses == null) ? ImmutableList.<ConsentStatus> of()
                : ImmutableList.copyOf(consentStatuses);
    }

    public String getUsername() {
        return this.username;
    }

    public String getSessionToken() {
        return this.sessionToken;
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public boolean isConsented() {
        return this.consented;
    }
    
    public SharingScope getSharingScope() {
        return this.sharingScope;
    }
    
    public boolean hasSignedMostRecentConsent() {
        return signedMostRecentConsent;
    }
    
    public DataGroups getDataGroups() {
        return dataGroups;
    }
    
    public List<ConsentStatus> getConsentStatuses() { 
        return consentStatuses;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE).append("username", username)
                .append("sessionToken", sessionToken).append("authenticated", authenticated)
                .append("consented", consented).append("sharingScope", sharingScope)
                .append("signedMostRecentConsent", signedMostRecentConsent).append("dataGroups", dataGroups)
                .append("consentStatuses", consentStatuses).toString();
    }

}
