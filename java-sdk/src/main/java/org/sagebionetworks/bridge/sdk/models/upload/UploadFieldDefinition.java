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
    private final Boolean allowOtherChoices;
    private final String fileExtension;
    private final String mimeType;
    private final Integer maxLength;
    private final List<String> multiChoiceAnswerList;
    private final String name;
    private final boolean required;
    private final UploadFieldType type;
    private final Boolean unboundedText;

    /**
     * Private constructor. Construction of an UploadFieldDefinition should go through the Builder. This constructor
     * should not be made public, since we don't want to potentially end up with dozens of "convenience constructors"
     * if we add more fields to this class.
     */
    private UploadFieldDefinition(Boolean allowOtherChoices, String fileExtension, String mimeType,
            Integer maxLength, List<String> multiChoiceAnswerList,
            String name, boolean required, UploadFieldType type, Boolean unboundedText)
            throws InvalidEntityException {
        if (StringUtils.isBlank(name)) {
            throw new InvalidEntityException("name cannot be blank");
        }
        if (type == null) {
            throw new InvalidEntityException("type cannot be null");
        }

        this.allowOtherChoices = allowOtherChoices;
        this.fileExtension = fileExtension;
        this.mimeType = mimeType;
        this.maxLength = maxLength;
        this.multiChoiceAnswerList = multiChoiceAnswerList;
        this.name = name;
        this.required = required;
        this.type = type;
        this.unboundedText = unboundedText;
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
        this(null, null, null, null, null, name, true, type, null);
    }

    /**
     * Used for MULTI_CHOICE. True if the multi-choice field allows an "other" answer with user freeform text. This
     * tells BridgeEX to reserve an "other" column for this field. Can be null, so that the number of field parameters
     * doesn't explode.
     */
    public Boolean getAllowOtherChoices() {
        return allowOtherChoices;
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
     * <p>
     * Used for STRING, SINGLE_CHOICE, and INLINE_JSON_BLOB types. This is a hint for BridgeEX to create a Synapse
     * column with the right width.
     * </p>
     * <p>
     * If not specified, Bridge will use the default max length of 100 (if applicable).
     * </p>
     */
    public Integer getMaxLength() {
        return maxLength;
    }

    /**
     * <p>
     * Used for MULTI_CHOICE types. This lists all valid answers for this field. It is used by BridgeEX to create the
     * Synapse table columns for MULTI_CHOICE fields. This is a list because order matters, in terms of Synapse
     * column order. Must be specified if the field type is a MULTI_CHOICE.
     * </p>
     * <p>
     * For schemas generated from surveys, this list will be the "value" in the survey question option, or the "label"
     * if value is not specified.
     * </p>
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

    /**
     * <p>
     * True if this field is a text-field with unbounded length. (Only applies to fields that are serialized as text,
     * such as INLINE_JSON_BLOB, SINGLE_CHOICE, or STRING. Can be null, so that the number of field parameters doesn't
     * explode.
     * </p>
     * <p>
     * This flag takes precedence over the maxLength value.
     * </p>
     */
    public Boolean isUnboundedText() {
        return unboundedText;
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
                Objects.equals(allowOtherChoices, that.allowOtherChoices) &&
                Objects.equals(fileExtension, that.fileExtension) &&
                Objects.equals(mimeType, that.mimeType) &&
                Objects.equals(maxLength, that.maxLength) &&
                Objects.equals(multiChoiceAnswerList, that.multiChoiceAnswerList) &&
                Objects.equals(name, that.name) &&
                type == that.type &&
                Objects.equals(unboundedText, that.unboundedText);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(allowOtherChoices, fileExtension, mimeType, maxLength,
                multiChoiceAnswerList, name, required, type, unboundedText);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "UploadFieldDefinition[" +
                ", allowOtherChoices=" + String.valueOf(allowOtherChoices) +
                ", fileExtension=" + fileExtension +
                ", mimeType=" + mimeType +
                ", maxLength=" + maxLength +
                ", maxLength=" + (multiChoiceAnswerList == null ? "null" :
                    Joiner.on(", ").useForNull("null").join(multiChoiceAnswerList)) +
                ", name=" + name +
                ", required=" + required +
                ", type=" + type.name() +
                ", unboundedText=" + String.valueOf(unboundedText) +
                "]";
    }

    /** Builder for UploadFieldDefinition */
    public static class Builder {
        private Boolean allowOtherChoices;
        private String fileExtension;
        private String mimeType;
        private Integer maxLength;
        private List<String> multiChoiceAnswerList;
        private String name;
        private Boolean required;
        private UploadFieldType type;
        private Boolean unboundedText;

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadFieldDefinition#getAllowOtherChoices */
        public Builder withAllowOtherChoices(Boolean allowOtherChoices) {
            this.allowOtherChoices = allowOtherChoices;
            return this;
        }

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

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadFieldDefinition#isUnboundedText */
        public Builder withUnboundedText(Boolean unboundedText) {
            this.unboundedText = unboundedText;
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

            return new UploadFieldDefinition(allowOtherChoices, fileExtension, mimeType, maxLength,
                    multiChoiceAnswerListCopy, name, required, type, unboundedText);
        }
    }
}
