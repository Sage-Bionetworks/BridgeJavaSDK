package org.sagebionetworks.bridge.sdk.models;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.sdk.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.Utilities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResearchConsent {

    private static final ObjectMapper mapper = Utilities.getMapper();

    private final String name;
    private final DateTime birthdate;

    @JsonCreator
    private ResearchConsent(@JsonProperty("name") String name, @JsonProperty("birthdate") DateTime birthdate) {
        this.name = name;
        this.birthdate = birthdate;
    }

    public static ResearchConsent valueOf(String name, DateTime birthdate) {
        return new ResearchConsent(name, birthdate);
    }

    public static ResearchConsent valueOf(String json) {
        if (json == null) {
            throw new IllegalArgumentException("json cannot be null.");
        }

        ResearchConsent record = null;
        try {
            record = mapper.readValue(json, ResearchConsent.class);
        } catch (IOException e) {
            throw new BridgeSDKException(
                    "Something went wrong while converting JSON into ResearchConsent: json="
                            + json, e);
        }
        return record;
    }

    public String getName() { return name; }
    public DateTime getBirthdate() { return birthdate; }

    @Override
    public String toString() {
        return "ResearchConsent[name=" + name +
        		", birthdate=" + birthdate.toString(ISODateTimeFormat.dateTime()) + "]";
    }

}
