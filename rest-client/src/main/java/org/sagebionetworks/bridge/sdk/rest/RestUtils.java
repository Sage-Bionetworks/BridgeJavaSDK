package org.sagebionetworks.bridge.sdk.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.sagebionetworks.bridge.sdk.rest.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.rest.json.LowercaseEnumModule;
import org.sagebionetworks.bridge.sdk.rest.model.ConsentStatus;
import org.sagebionetworks.bridge.sdk.rest.model.UserSessionInfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class RestUtils {

    private static final ObjectMapper MAPPER = getObjectMapper();
    
    static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JodaModule());
        mapper.registerModule(new LowercaseEnumModule());
        return mapper;
    }
    
    static <T> T getJsonAsType(JsonNode json, Class<T> c) {
        try {
            return MAPPER.treeToValue(json, c);
        } catch (IOException e) {
            String message = "Error message: " + e.getMessage() + 
                    "\nSomething went wrong while converting JSON into " + c.getSimpleName() + ": json=" + json;
            throw new BridgeSDKException(message, e);
        }
    }
    
    static <T> T last(List<T> list) {
        return (list == null || list.isEmpty()) ? null : list.get(list.size()-1);
    }
    
    /**
     * Are all required consents signed?
     * @param session
     * @return true if all required consents are signed, false if there are no consents, 
     *  or they are not all signed
     */
    public static boolean isUserConsented(UserSessionInfo session) {
        checkNotNull(session);
        Map<String,ConsentStatus> statuses = session.getConsentStatuses();
        checkNotNull(statuses);
        if (statuses.isEmpty()) {
            return false;
        }
        for (ConsentStatus status : statuses.values()) {
            if (isTrue(status.getRequired()) && !isTrue(status.getConsented())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Are all the required consents up-to-date?
     * @param session
     * @return true if all required consents are signed and up-to-date, false if there are no 
     *  consents, or they are not all signed, or they are not all up-to-date.
     */
    public static boolean isConsentCurrent(UserSessionInfo session) {
        checkNotNull(session);
        Map<String,ConsentStatus> statuses = session.getConsentStatuses();
        checkNotNull(statuses);
        if (statuses.isEmpty()) {
            return false;
        }
        for (ConsentStatus status : statuses.values()) {
            if (isTrue(status.getRequired()) && !isTrue(status.getSignedMostRecentConsent())) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean isTrue(Boolean bool) {
        return bool != null && bool == Boolean.TRUE;
    }
}
