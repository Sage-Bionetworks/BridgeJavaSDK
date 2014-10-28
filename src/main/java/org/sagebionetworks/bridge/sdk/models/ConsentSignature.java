package org.sagebionetworks.bridge.sdk.models;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ConsentSignature {

    private final String name;
    private final DateTime birthdate;

    @JsonCreator
    private ConsentSignature(@JsonProperty("name") String name, @JsonProperty("birthdate") DateTime birthdate) {
        this.name = name;
        this.birthdate = birthdate;
    }

    public static ConsentSignature valueOf(String name, DateTime birthdate) {
        return new ConsentSignature(name, birthdate);
    }

    public String getName() {
        return name;
    }

    public DateTime getBirthdate() {
        return birthdate;
    }

    @Override
    public String toString() {
        return "ResearchConsent[name=" + name +
        		", birthdate=" + birthdate.toString(ISODateTimeFormat.dateTime()) + "]";
    }

}
