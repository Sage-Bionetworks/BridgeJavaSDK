package org.sagebionetworks.bridge.sdk.models.users;

import java.util.Objects;

import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.sdk.json.DateOnlySerializer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateDeserializer;

public final class ConsentSignature {

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
    // We use the standard LocalDateDeserializer from jackson-datatype-joda. However, we still need to use a
    // @JsonDeserialize annotation anyway or Jackson won't know what to do with it.
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
    // We use a custom serializer, because the standard LocalDateSerializer serializes into a very unusual format.
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(birthdate);
        result = prime * result + Objects.hashCode(imageData);
        result = prime * result + Objects.hashCode(imageMimeType);
        result = prime * result + Objects.hashCode(name);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ConsentSignature other = (ConsentSignature) obj;
        return (Objects.equals(birthdate, other.birthdate) && Objects.equals(imageData, other.imageData)
                && Objects.equals(imageMimeType, other.imageMimeType) && Objects.equals(name, other.name));
    }

    @Override
    public String toString() {
        return String.format("ConsentSignature[name=%s, birthdate=%s, hasImageData=%s, imageMimeType=%s]", 
                name, birthdate.toString(ISODateTimeFormat.date()), (imageData != null), imageMimeType);
    }
}
