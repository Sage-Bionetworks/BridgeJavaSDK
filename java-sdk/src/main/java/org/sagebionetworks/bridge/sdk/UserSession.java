package org.sagebionetworks.bridge.sdk;

import static org.sagebionetworks.bridge.sdk.utils.BridgeUtils.TO_STRING_STYLE;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.sagebionetworks.bridge.sdk.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.json.SubpopulationGuidKeyDeserializer;
import org.sagebionetworks.bridge.sdk.models.accounts.StudyParticipant;
import org.sagebionetworks.bridge.sdk.models.subpopulations.ConsentStatus;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;
import org.sagebionetworks.bridge.sdk.utils.BridgeUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableMap;

final class UserSession {

    private static final TypeReference<Map<SubpopulationGuid, ConsentStatus>> CONSENT_STATUS_MAP = new TypeReference<Map<SubpopulationGuid, ConsentStatus>>() {};
    private static final ObjectMapper MAPPER = BridgeUtils.getMapper();
    
    @JsonCreator
    public static final UserSession fromJSON(JsonNode node) {
        try {
            String sessionToken = node.get("sessionToken").asText();
            boolean authenticated = node.get("authenticated").asBoolean();
            Map<SubpopulationGuid, ConsentStatus> consentStatuses = MAPPER.convertValue(
                    node.get("consentStatuses"), CONSENT_STATUS_MAP);
            StudyParticipant studyParticipant = MAPPER.treeToValue(node, StudyParticipant.class);
            return new UserSession(sessionToken, authenticated, consentStatuses, studyParticipant);
        } catch(JsonProcessingException e) {
            throw new BridgeSDKException(e.getMessage(), e);
        }
    }
    
    
    private final String sessionToken;
    private final boolean authenticated;
    @JsonDeserialize(keyUsing = SubpopulationGuidKeyDeserializer.class)
    private final Map<SubpopulationGuid,ConsentStatus> consentStatuses;
    private final StudyParticipant studyParticipant;

    UserSession(String sessionToken, boolean authenticated, Map<SubpopulationGuid, ConsentStatus> consentStatuses,
            StudyParticipant studyParticipant) {
        this.sessionToken = sessionToken;
        this.authenticated = authenticated;
        this.studyParticipant = studyParticipant;
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
    
    public StudyParticipant getStudyParticipant() {
        return this.studyParticipant;
    }
    
    public boolean hasSignedMostRecentConsent() {
        return ConsentStatus.isConsentCurrent(consentStatuses);
    }
    
    public Map<SubpopulationGuid,ConsentStatus> getConsentStatuses() { 
        return consentStatuses;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE).append("sessionToken", sessionToken)
                .append("authenticated", authenticated).append("consented", isConsented())
                .append("signedMostRecentConsent", hasSignedMostRecentConsent())
                .append("studyParticipant", studyParticipant).append("consentStatuses", consentStatuses).toString();
    }

}
