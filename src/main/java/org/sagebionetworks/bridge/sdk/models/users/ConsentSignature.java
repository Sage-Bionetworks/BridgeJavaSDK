package org.sagebionetworks.bridge.sdk.models.users;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateDeserializer;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;

public class ConsentSignature {

    private final String name;
    private final LocalDate birthdate;
    private final String imageData;
    private final String imageMimeType;

    /**
     * <p>
     * Simple constructor.
     * </p>
     * <p>
     * imageData and imageMimeType are optional. However, if one of them is specified, both of them must be specified.
     * If they are specified, they must be non-empty.
     * </p>
     *
     * @param name
     *         name of the user giving consent, must be non-null and non-empty
     * @param birthdate
     *         user's birth date, must be non-null
     * @param imageData
     *         signature image data as a Base64 encoded string, optional
     * @param imageMimeType
     *         signature image MIME type (ex: image/png), optional
     */
    @JsonCreator
    public ConsentSignature(@JsonProperty("name") String name, @JsonProperty("birthdate")
            @JsonDeserialize(using=LocalDateDeserializer.class) LocalDate birthdate,
            @JsonProperty("imageData") String imageData, @JsonProperty("imageMimeType") String imageMimeType) {
        this.name = name;
        this.birthdate = birthdate;
        this.imageData = imageData;
        this.imageMimeType = imageMimeType;
    }

    /** Name of the user giving consent. */
    public String getName() {
        return name;
    }

    /** User's birth date. */
    @JsonSerialize(using=DateOnlySerializer.class)
    public LocalDate getBirthdate() {
        return birthdate;
    }

    /** Signature image data as a Base64 encoded string. */
    public String getImageData() {
        return imageData;
    }

    /** Signature image MIME type (ex: image/png). */
    public String getImageMimeType() {
        return imageMimeType;
    }

    @Override
    public String toString() {
        return "ResearchConsent[name=" + name + ", birthdate=" + birthdate.toString(ISODateTimeFormat.date()) +
                ", hasImageData=" + (imageData != null) + ", imageMimeType=" + imageMimeType + "]";
    }
}
