package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.LocalDate;
import org.sagebionetworks.bridge.sdk.json.DateOnlySerializer;
import org.sagebionetworks.bridge.sdk.models.users.ConsentSignature;
import org.sagebionetworks.bridge.sdk.models.users.SharingScope;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Not a public model object.
 */
class ConsentSubmission {

    private final String name;
    private final LocalDate birthdate;
    private final String imageData;
    private final String imageMimeType;
    private final SharingScope scope;
    
    public ConsentSubmission(ConsentSignature sig, SharingScope scope) {
        checkNotNull(sig);
        checkNotNull(scope);
        this.name = sig.getName();
        this.birthdate = sig.getBirthdate();
        this.imageData = sig.getImageData();
        this.imageMimeType = sig.getImageMimeType();
        this.scope = scope;
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
    
    public SharingScope getScope() {
        return scope;
    }
}
