package org.sagebionetworks.bridge.sdk.models.upload;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

import org.sagebionetworks.bridge.sdk.exceptions.InvalidEntityException;

/**
 * This class represents a field definition for an upload schema. This could map to a top-level key-value pair in the
 * raw JSON, or to a column in a Synapse table.
 */
@JsonDeserialize(builder = UploadFieldDefinition.Builder.class)
public final class UploadFieldDefinition {
    private final String fileExtension;
    private final String mimeType;
    private final Integer minAppVersion;
    private final Integer maxAppVersion;
    private final Integer maxLength;
    private final List<String> multiChoiceAnswerList;
    private final String name;
    private final boolean required;
    private final UploadFieldType type;

    /**
     * Private constructor. Construction of an UploadFieldDefinition should go through the Builder. This constructor
     * should not be made public, since we don't want to potentially end up with dozens of "convenience constructors"
     * if we add more fields to this class.
     */
    private UploadFieldDefinition(String fileExtension, String mimeType, Integer minAppVersion, Integer maxAppVersion,
            Integer maxLength, List<String> multiChoiceAnswerList, String name, boolean required, UploadFieldType type)
            throws InvalidEntityException {
        if (StringUtils.isBlank(name)) {
            throw new InvalidEntityException("name cannot be blank");
        }
        if (type == null) {
            throw new InvalidEntityException("type cannot be null");
        }

        this.fileExtension = fileExtension;
        this.mimeType = mimeType;
        this.minAppVersion = minAppVersion;
        this.maxAppVersion = maxAppVersion;
        this.maxLength = maxLength;
        this.multiChoiceAnswerList = multiChoiceAnswerList;
        this.name = name;
        this.required = required;
        this.type = type;
    }

    /**
     * Convenience constructor for UploadFieldDefinition. This constructor takes in only the required parameters (name
     * and type) and sets all other fields to defaults. For more sophisticated construction options, please use the
     * Builder.
     *
     * @param name
     *         field name
     * @param type
     *         field type
     * @throws InvalidEntityException
     *         if called with invalid fields
     */
    public UploadFieldDefinition(String name, UploadFieldType type) throws InvalidEntityException {
        this(null, null, null, null, null, null, name, true, type);
    }

    /**
     * Used for ATTACHMENT_V2 types. Used as a hint by BridgeEX to preserve the file extension as a quality-of-life
     * improvement. Optional, defaults to ".tmp".
     */
    public String getFileExtension() {
        return fileExtension;
    }

    /**
     * Used for ATTACHMENT_V2 types. Used as a hint by BridgeEX to mark a Synapse file handle with the correct MIME
     * type as a quality-of-life improvement. Optional, defaults to "application/octet-stream".
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * The oldest app version number for which this field is required. App versions before this will treat this field
     * as optional, as it doesn't exist yet. Does nothing if required is false.
     */
    public Integer getMinAppVersion() {
        return minAppVersion;
    }

    /**
     * Similar to minAppVersion. This is used for when required fields are removed from the app, but we want to re-use
     * the old Synapse table.
     */
    public Integer getMaxAppVersion() {
        return maxAppVersion;
    }

    /**
     * Used for STRING, SINGLE_CHOICE, and INLINE_JSON_BLOB types. This is a hint for BridgeEX to create a Synapse
     * column with the right width.
     */
    public Integer getMaxLength() {
        return maxLength;
    }

    /**
     * Used for MULTI_CHOICE types. This lists all valid answers for this field. It is used by BridgeEX to create the
     * Synapse table columns for MULTI_CHOICE fields. This is a list because order matters, in terms of Synapse
     * column order. Must be specified if the field type is a MULTI_CHOICE.
     */
    public List<String> getMultiChoiceAnswerList() {
        return multiChoiceAnswerList;
    }

    /** The field name, always non-null and non-empty. */
    public String getName() {
        return name;
    }

    /** True if the field is required to have data, false otherwise. */
    public boolean isRequired() {
        return required;
    }

    /**
     * The field's type, always non-null.
     *
     * @see org.sagebionetworks.bridge.sdk.models.upload.UploadFieldType
     */
    public UploadFieldType getType() {
        return type;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UploadFieldDefinition that = (UploadFieldDefinition) o;
        return required == that.required &&
                Objects.equals(fileExtension, that.fileExtension) &&
                Objects.equals(mimeType, that.mimeType) &&
                Objects.equals(minAppVersion, that.minAppVersion) &&
                Objects.equals(maxAppVersion, that.maxAppVersion) &&
                Objects.equals(maxLength, that.maxLength) &&
                Objects.equals(multiChoiceAnswerList, that.multiChoiceAnswerList) &&
                Objects.equals(name, that.name) &&
                type == that.type;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(fileExtension, mimeType, minAppVersion, maxAppVersion, maxLength, multiChoiceAnswerList,
                name, required, type);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "UploadFieldDefinition[" +
                "fileExtension=" + fileExtension +
                ", mimeType=" + mimeType +
                ", minAppVersion=" + minAppVersion +
                ", maxAppVersion=" + maxAppVersion +
                ", maxLength=" + maxLength +
                ", maxLength=" + Joiner.on(", ").join(multiChoiceAnswerList) +
                ", name=" + name +
                ", required=" + required +
                ", type=" + type.name() +
                "]";
    }

    /** Builder for UploadFieldDefinition */
    public static class Builder {
        private String fileExtension;
        private String mimeType;
        private Integer minAppVersion;
        private Integer maxAppVersion;
        private Integer maxLength;
        private List<String> multiChoiceAnswerList;
        private String name;
        private Boolean required;
        private UploadFieldType type;

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadFieldDefinition#getFileExtension */
        public Builder withFileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadFieldDefinition#getMimeType */
        public Builder withMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadFieldDefinition#getMinAppVersion */
        public Builder withMinAppVersion(Integer minAppVersion) {
            this.minAppVersion = minAppVersion;
            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadFieldDefinition#getMaxAppVersion */
        public Builder withMaxAppVersion(Integer maxAppVersion) {
            this.maxAppVersion = maxAppVersion;
            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadFieldDefinition#getMaxLength */
        public Builder withMaxLength(Integer maxLength) {
            this.maxLength = maxLength;
            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadFieldDefinition#getMultiChoiceAnswerList */
        @JsonSetter
        public Builder withMultiChoiceAnswerList(List<String> multiChoiceAnswerList) {
            this.multiChoiceAnswerList = multiChoiceAnswerList;
            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadFieldDefinition#getMultiChoiceAnswerList */
        public Builder withMultiChoiceAnswerList(String... multiChoiceAnswerList) {
            this.multiChoiceAnswerList = ImmutableList.copyOf(multiChoiceAnswerList);
            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadFieldDefinition#getName */
        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadFieldDefinition#isRequired */
        public Builder withRequired(Boolean required) {
            this.required = required;
            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadFieldDefinition#getType */
        public Builder withType(UploadFieldType type) {
            this.type = type;
            return this;
        }

        /**
         * Builds and validates an UploadFieldDefinition. name must be non-null and non-empty. type must be
         * non-null. required may be null and defaults to true. If this is called with invalid fields, it will throw an
         * BridgeSDKException.
         *
         * @return validated UploadFieldDefinition
         * @throws InvalidEntityException
         *         if called with invalid fields
         */
        public UploadFieldDefinition build() throws InvalidEntityException {
            if (required == null) {
                required = true;
            }

            // If the answer list was specified, make an immutable copy.
            List<String> multiChoiceAnswerListCopy = null;
            if (multiChoiceAnswerList != null) {
                multiChoiceAnswerListCopy = ImmutableList.copyOf(multiChoiceAnswerList);
            }

            return new UploadFieldDefinition(fileExtension, mimeType, minAppVersion, maxAppVersion, maxLength,
                    multiChoiceAnswerListCopy, name, required, type);
        }
    }
}
