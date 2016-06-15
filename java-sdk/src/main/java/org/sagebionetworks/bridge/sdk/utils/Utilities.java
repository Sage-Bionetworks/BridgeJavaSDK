package org.sagebionetworks.bridge.sdk.utils;

import static org.apache.commons.validator.routines.UrlValidator.ALLOW_LOCAL_URLS;
import static org.apache.commons.validator.routines.UrlValidator.NO_FRAGMENTS;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.sagebionetworks.bridge.sdk.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.json.LowercaseEnumModule;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.GuidHolder;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleGuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleGuidHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleGuidVersionHolder;

public final class Utilities {
    private static final String[] schemes = { "http", "https" };
    private static final UrlValidator urlValidator = new UrlValidator(schemes, NO_FRAGMENTS + ALLOW_LOCAL_URLS);
    private static final EmailValidator emailValidator = EmailValidator.getInstance();
    public static final TypeReference<Map<String, Object>> TYPE_REF_RAW_MAP =
            new TypeReference<Map<String, Object>>(){};

    /**
     * This mimics the style of the toString() methods we've been writing in a more manual way.
     */
    @SuppressWarnings("serial")
    private static final class Style extends ToStringStyle {
        public Style() {
            super();
            setFieldSeparator(", ");
            setUseShortClassName(true);
            setUseIdentityHashCode(false);
        }
    }
    
    public static final ToStringStyle TO_STRING_STYLE = new Style();
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        MAPPER.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        SimpleModule mod = new SimpleModule();
        mod.addAbstractTypeMapping(GuidHolder.class, SimpleGuidHolder.class);
        mod.addAbstractTypeMapping(GuidVersionHolder.class, SimpleGuidVersionHolder.class);
        mod.addAbstractTypeMapping(GuidCreatedOnVersionHolder.class, SimpleGuidCreatedOnVersionHolder.class);
        MAPPER.registerModule(mod);
        MAPPER.registerModule(new JodaModule());
        MAPPER.registerModule(new LowercaseEnumModule());
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    public static boolean isValidEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null.");
        }
        return emailValidator.isValid(email);
    }

    public static boolean isValidUrl(String url) {
        if (url == null) {
            throw new IllegalArgumentException("URL cannot be null.");
        }
        return urlValidator.isValid(url);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> LinkedHashSet<T> newLinkedHashSet(LinkedHashSet<T> set) {
        LinkedHashSet<T> copy = new LinkedHashSet<T>();
        copy.addAll(set);
        return copy;
    }

    public static String getObjectAsJson(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            String message = String.format("Could not process %s: %s into JSON", object.getClass().getSimpleName(), object.toString());
            throw new BridgeSDKException(message, e);
        }
    }

    public static <T> T getJsonAsType(String json, Class<T> c) {
        try {
            return MAPPER.readValue(json, c);
        } catch (IOException e) {
            throw new BridgeSDKException("Error message: " + e.getMessage()
                    + "\nSomething went wrong while converting JSON into " + c.getSimpleName()
                    + ": json=" + json, e);
        }
    }
}
