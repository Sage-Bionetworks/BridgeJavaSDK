package org.sagebionetworks.bridge.sdk.models.upload;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import org.sagebionetworks.bridge.sdk.exceptions.InvalidEntityException;

/**
 * This class represents a schema for the uploads sent by the mobile apps. This can be created and updated by study
 * researchers.
 */
@JsonDeserialize(builder = UploadSchema.Builder.class)
public final class UploadSchema {
    private final List<UploadFieldDefinition> fieldDefinitions;
    private final String name;
    private final Integer revision;
    private final String schemaId;
    private final UploadSchemaType schemaType;
    private final String surveyGuid;
    private final DateTime surveyCreatedOn;
    private final Long version;

    /** Private constructor. Construction of an UploadSchema should go through the Builder. */
    private UploadSchema(List<UploadFieldDefinition> fieldDefinitions, String name, Integer revision,
            String schemaId, UploadSchemaType schemaType, String surveyGuid, DateTime surveyCreatedOn, Long version) {
        this.fieldDefinitions = fieldDefinitions;
        this.name = name;
        this.revision = revision;
        this.schemaId = schemaId;
        this.schemaType = schemaType;
        this.surveyGuid = surveyGuid;
        this.surveyCreatedOn = surveyCreatedOn;
        this.version = version;
    }

    /**
     * A list of fields defined in the schema. This can be changed across different schema revisions. This is always
     * non-null, non-empty, and immutable.
     */
    public List<UploadFieldDefinition> getFieldDefinitions() {
        return fieldDefinitions;
    }

    /**
     * Human-friendly displayable schema name, such as "Tapping Activity Task". This can be changed across different
     * schema revisions. This is always non-null and non-empty.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>
     * Revision number. This is a secondary ID used to partition different Synapse tables based on breaking changes in
     * a schema.
     * </p>
     * <p>
     * This field is optional, but if set, must be non-negative.
     * </p>
     */
    public Integer getRevision() {
        return revision;
    }

    /**
     * Unique identifier for the schema. This need only be unique to a given study. This should included in the upload
     * data. This can be human readable, such as "tapping-task". This cannot be changed across different schema
     * revisions. This is always non-null and non-empty.
     */
    public String getSchemaId() {
        return schemaId;
    }

    /** Schema type, for example survey vs data. */
    public UploadSchemaType getSchemaType() {
        return schemaType;
    }

    /** The survey GUID, if this is a survey schema. */
    public String getSurveyGuid() {
        return surveyGuid;
    }

    /** The survey createdOn, if this is a survey schema. */
    public DateTime getSurveyCreatedOn() {
        return surveyCreatedOn;
    }

    /**
     * The version of this schema revision as used to implement optimistic locking. This must be passed back to the
     * server on an update, unmodified, in order for the update to succeed. If the schema revision has been
     * concurrently modified, the update will throw an error. Set this to null if you're creating a new schema
     * revision.
     */
    public Long getVersion() {
        return version;
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
        UploadSchema that = (UploadSchema) o;
        return Objects.equals(fieldDefinitions, that.fieldDefinitions) &&
                Objects.equals(name, that.name) &&
                Objects.equals(revision, that.revision) &&
                Objects.equals(schemaId, that.schemaId) &&
                schemaType == that.schemaType &&
                Objects.equals(surveyGuid, that.surveyGuid) &&
                Objects.equals(surveyCreatedOn, that.surveyCreatedOn) &&
                Objects.equals(version, that.version);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(fieldDefinitions, name, revision, schemaId, schemaType, surveyGuid, surveyCreatedOn,
                version);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "UploadSchema[" +
                "name=" + name +
                ", revision=" + revision +
                ", schemaId=" + schemaId +
                ", schemaType=" + schemaType.name() +
                ", surveyGuid=" + surveyGuid +
                ", surveyCreatedOn=" + surveyCreatedOn +
                ", version=" + version +
                ", fieldDefinitions=" + Joiner.on(", ").join(fieldDefinitions) +
                "]";
    }

    /** Builder for UploadSchema */
    public static class Builder {
        private List<UploadFieldDefinition> fieldDefinitions;
        private String name;
        private Integer revision;
        private String schemaId;
        private UploadSchemaType schemaType;
        private String surveyGuid;
        private DateTime surveyCreatedOn;
        private Long version;

        /**
         * Sets all builder fields to be a copy of the specified schema. This returns the builder, which can be used to
         * make additional changes to the schema being built. This is commonly used for updating a schema from a
         * pre-existing one.
         */
        public Builder copyOf(UploadSchema other) {
            this.fieldDefinitions = other.fieldDefinitions;
            this.name = other.name;
            this.revision = other.revision;
            this.schemaId = other.schemaId;
            this.schemaType = other.schemaType;
            this.surveyGuid = other.surveyGuid;
            this.surveyCreatedOn = other.surveyCreatedOn;
            this.version = other.version;
            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadSchema#getFieldDefinitions */
        @JsonProperty("fieldDefinitions")
        public Builder withFieldDefinitions(List<UploadFieldDefinition> fieldDefinitions) {
            this.fieldDefinitions = fieldDefinitions;
            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadSchema#getFieldDefinitions */
        @JsonIgnore
        public Builder withFieldDefinitions(UploadFieldDefinition... fieldDefinitions) {
            this.fieldDefinitions = ImmutableList.copyOf(fieldDefinitions);
            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadSchema#getName */
        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadSchema#getRevision */
        public Builder withRevision(Integer revision) {
            this.revision = revision;
            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadSchema#getSchemaId */
        public Builder withSchemaId(String schemaId) {
            this.schemaId = schemaId;
            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadSchema#getSchemaType */
        public Builder withSchemaType(UploadSchemaType schemaType) {
            this.schemaType = schemaType;
            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadSchema#getSurveyGuid */
        public Builder withSurveyGuid(String surveyGuid) {
            this.surveyGuid = surveyGuid;
            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadSchema#getSurveyCreatedOn */
        public Builder withSurveyCreatedOn(DateTime surveyCreatedOn) {
            this.surveyCreatedOn = surveyCreatedOn;
            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadSchema#getVersion */
        public Builder withVersion(Long version) {
            this.version = version;
            return this;
        }

        /**
         * <p>
         * Builds and validates an UploadSchema. This will throw a InvalidEntityException under the following conditions:
         * </p>
         *   <ul>
         *     <li>fieldDefinitions is null or empty</li>
         *     <li>fieldDefinitions contains null or invalid entries</li>
         *     <li>name is null or empty</li>
         *     <li>revision is negative</li>
         *     <li>schemaId is null or empty</li>
         *     <li>schemaType is null</li>
         *   </ul>
         * <p>
         * The constructed UploadSchema's field definition list will not be backed by the list passed in by
         * {@link #withFieldDefinitions}. That is, changes to the list passed in by {@link #withFieldDefinitions} will
         * not be propagated to the field definition list in the constructed UploadSchema.
         * </p>
         *
         * @return validated UploadSchema
         * @throws InvalidEntityException
         *         if called with invalid fields
         */
        public UploadSchema build() throws InvalidEntityException {
            // fieldDefinitions
            // We do not need to validate inside the elements of fieldDefinitions, because (1)
            // UploadFieldDefinition is self-validating and (2) we copy this to an ImmutableList, which does not
            // permit null values.
            if (fieldDefinitions == null || fieldDefinitions.isEmpty()) {
                throw new InvalidEntityException("fieldDefinitions cannot be null or empty");
            }

            // name
            if (StringUtils.isBlank(name)) {
                throw new InvalidEntityException("name cannot be blank");
            }

            // revision is optional, but if specified, must be non-negative. (0 is allowed if it's a new schema.
            // revisions 1 and above are saved schemas)
            if (revision != null && revision < 0) {
                throw new InvalidEntityException("revision cannot be negative");
            }

            // schema ID
            if (StringUtils.isBlank(schemaId)) {
                throw new InvalidEntityException("schemaId cannot be blank");
            }

            // schema type
            if (schemaType == null) {
                throw new InvalidEntityException("schemaType cannot be null");
            }

            return new UploadSchema(ImmutableList.copyOf(fieldDefinitions), name, revision, schemaId, schemaType,
                    surveyGuid, surveyCreatedOn, version);
        }
    }
}
