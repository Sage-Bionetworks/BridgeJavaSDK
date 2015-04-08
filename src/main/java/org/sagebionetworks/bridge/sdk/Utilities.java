package org.sagebionetworks.bridge.sdk;

import static org.apache.commons.validator.routines.UrlValidator.ALLOW_LOCAL_URLS;
import static org.apache.commons.validator.routines.UrlValidator.NO_FRAGMENTS;

import java.io.IOException;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.sagebionetworks.bridge.sdk.json.LowercaseEnumModule;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.GuidHolder;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleGuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleGuidHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleGuidVersionHolder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public final class Utilities {

    private static final String[] schemes = { "http", "https" };
    private static final UrlValidator urlValidator = new UrlValidator(schemes, NO_FRAGMENTS + ALLOW_LOCAL_URLS);
    private static final EmailValidator emailValidator = EmailValidator.getInstance();

    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        SimpleModule mod = new SimpleModule();
        mod.addAbstractTypeMapping(GuidHolder.class, SimpleGuidHolder.class);
        mod.addAbstractTypeMapping(GuidVersionHolder.class, SimpleGuidVersionHolder.class);
        mod.addAbstractTypeMapping(GuidCreatedOnVersionHolder.class, SimpleGuidCreatedOnVersionHolder.class);
        
        /* This works, but we don't want to do it. While it's nice to have shorter dates and times in some classes, like 
         * the Schedule (where seconds/milliseconds aren't needed), we do not know if this creates challenges for some 
         * language libraries when parsing ISO 8601 strings, and there are DateTimes in the system that we do want to 
         * server as timestamps to the millisecond. If used, we'd need to scope to specific properties of specific objects.
        mod.addSerializer(LocalTime.class, new StdSerializer<LocalTime>(LocalTime.class) {
            @Override public void serialize(LocalTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
                jgen.writeString(value.toString("HH:mm"));
            }
        });
        mod.addSerializer(DateTime.class, new StdSerializer<DateTime>(DateTime.class) {
            @Override public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
                jgen.writeString(value.toString("yyyy-MM-dd'T'HH:mmZZ"));
            }
        });
         */
        
        // WRITE_DATES_AS_TIMESTAMPS causes JodaModule to use our preferred timestamp format. Great module, but 
        // timestamps are overly precise (milliseconds).
        mapper.registerModule(new JodaModule());
        mapper.registerModule(mod);
        mapper.registerModule(new LowercaseEnumModule());
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    static boolean isValidEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null.");
        }
        return emailValidator.isValid(email);
    }

    static boolean isValidUrl(String url) {
        if (url == null) {
            throw new IllegalArgumentException("URL cannot be null.");
        }
        return urlValidator.isValid(url);
    }

    static boolean isConnectableUrl(String url, int timeout) {
        if (!isValidUrl(url)) {
            throw new IllegalArgumentException("URL is not a valid one: " + url);
        } else if (timeout <= 0 || 10 * 1000 <= timeout) {
            throw new IndexOutOfBoundsException("timeout isn't in the valid range (0 < timeout < 10 minutes): "
                    + timeout);
        }
        url = url.replaceFirst("https", "http");
        try {
            Response response = Request.Head(url).connectTimeout(timeout).execute();
            int statusCode = response.returnResponse().getStatusLine().getStatusCode();
            return (200 <= statusCode && statusCode <= 399);
        } catch (IOException exception) {
            return false;
        }
    }
}
