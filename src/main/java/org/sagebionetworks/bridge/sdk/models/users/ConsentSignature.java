package org.sagebionetworks.bridge.sdk.models.users;

import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;

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
        result = prime * result + ((birthdate == null) ? 0 : birthdate.hashCode());
        result = prime * result + ((imageData == null) ? 0 : imageData.hashCode());
        result = prime * result + ((imageMimeType == null) ? 0 : imageMimeType.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConsentSignature other = (ConsentSignature) obj;
        if (birthdate == null) {
            if (other.birthdate != null)
                return false;
        } else if (!birthdate.equals(other.birthdate))
            return false;
        if (imageData == null) {
            if (other.imageData != null)
                return false;
        } else if (!imageData.equals(other.imageData))
            return false;
        if (imageMimeType == null) {
            if (other.imageMimeType != null)
                return false;
        } else if (!imageMimeType.equals(other.imageMimeType))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ConsentSignature[name=" + name + ", birthdate=" + birthdate.toString(ISODateTimeFormat.date()) +
                ", hasImageData=" + (imageData != null) + ", imageMimeType=" + imageMimeType + "]";
    }
}
