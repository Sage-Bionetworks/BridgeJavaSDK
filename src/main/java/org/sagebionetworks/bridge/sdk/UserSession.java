package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.json.DataGroupsDeserializer;
import org.sagebionetworks.bridge.sdk.json.DataGroupsSerializer;
import org.sagebionetworks.bridge.sdk.models.users.DataGroups;
import org.sagebionetworks.bridge.sdk.models.users.SharingScope;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

final class UserSession {

    private final String username;
    private final String sessionToken;
    private final SharingScope sharingScope;
    private final boolean authenticated;
    private final boolean consented;
    private final boolean signedMostRecentConsent;
    @JsonDeserialize(using=DataGroupsDeserializer.class)
    @JsonSerialize(using=DataGroupsSerializer.class)
    private DataGroups dataGroups;

    @JsonCreator
    private UserSession(@JsonProperty("username") String username, @JsonProperty("sessionToken") String sessionToken,
            @JsonProperty("authenticated") boolean authenticated, @JsonProperty("consented") boolean consented,
            @JsonProperty("sharingScope") SharingScope sharingScope,
            @JsonProperty("signedMostRecentConsent") boolean signedMostRecentConsent,
            @JsonProperty("dataGroups") 
            @JsonDeserialize(using=DataGroupsDeserializer.class)
            @JsonSerialize(using=DataGroupsSerializer.class)
            DataGroups dataGroups) {
        
        this.username = username;
        this.sessionToken = sessionToken;
        this.consented = consented;
        this.authenticated = authenticated;
        this.sharingScope = sharingScope;
        this.signedMostRecentConsent = signedMostRecentConsent;
        this.dataGroups = dataGroups;
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

    @Override
    public String toString() {
        return String.format("UserSession[username=%s, sessionToken=%s, authenticated=%s, consented=%s, sharingScope=%s, signedMostRecentConsent=%s, dataGroups=%s]", 
                username, sessionToken, authenticated, consented, sharingScope, signedMostRecentConsent, dataGroups);
    }

}
