package org.sagebionetworks.bridge.sdk.rest;

import java.io.IOException;
import java.util.List;

import org.sagebionetworks.bridge.sdk.rest.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.rest.json.LowercaseEnumModule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

class Utilities {

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
}
