package org.sagebionetworks.bridge.sdk.rest;

import java.io.IOException;

import org.sagebionetworks.bridge.sdk.rest.exceptions.BridgeSDKException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

class Utilities {

    private static final ObjectMapper MAPPER = getObjectMapper();
    
    static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }
    
    static <T> T getJsonAsType(JsonNode json, Class<T> c) {
        try {
            return MAPPER.treeToValue(json, c);
        } catch (IOException e) {
            throw new BridgeSDKException("Error message: " + e.getMessage()
                    + "\nSomething went wrong while converting JSON into " + c.getSimpleName() + ": json=" + json, e);
        }
    }

}
