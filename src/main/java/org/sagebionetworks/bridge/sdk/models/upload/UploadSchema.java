package org.sagebionetworks.bridge.sdk.models.upload;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

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

    /** Private constructor. Construction of an UploadSchema should go through the Builder. */
    private UploadSchema(List<UploadFieldDefinition> fieldDefinitions, String name, Integer revision,
            String schemaId, UploadSchemaType schemaType) {
        this.fieldDefinitions = fieldDefinitions;
        this.name = name;
        this.revision = revision;
        this.schemaId = schemaId;
        this.schemaType = schemaType;
    }

    /**
     * A list of fields defined in the schema. This can be changed across different schema revisions. This is always
     * non-null, non-empty, and immutable.
     * 
     * @return fieldDefinitions
     */
    public List<UploadFieldDefinition> getFieldDefinitions() {
        return fieldDefinitions;
    }

    /**
     * Human-friendly displayable schema name, such as "Tapping Activity Task". This can be changed across different
     * schema revisions. This is always non-null and non-empty.
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * <p>
     * Schema revision number. This is managed by the Bridge back-end. For creating new schemas, this should initially
     * be unset (or set to the default value of zero). For updating schemas, this should be set to the revision number
     * of the schema you are updating, to ensure that you aren't updating an older version of the schema. Upon creating
     * or updating a schema, the Bridge back-end will automatically increment this revision number by 1 (for updating
     * existing schemas) or from 0 to 1 (for creating new schemas).
     * </p>
     * <p>
     * This field is optional, but if set, must be non-negative.
     * </p>
     * 
     * @return revision
     */
    public Integer getRevision() {
        return revision;
    }

    /**
     * Unique identifier for the schema. This need only be unique to a given study. This should included in the upload
     * data. This can be human readable, such as "tapping-task". This cannot be changed across different schema
     * revisions. This is always non-null and non-empty.
     * 
     * @return schemaId
     */
    public String getSchemaId() {
        return schemaId;
    }

    /** 
     * Schema type, for example survey vs data. 
     * @return uploadSchemaType
     */
    public UploadSchemaType getSchemaType() {
        return schemaType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(fieldDefinitions);
        result = prime * result + Objects.hashCode(name);
        result = prime * result + Objects.hashCode(revision);
        result = prime * result + Objects.hashCode(schemaId);
        result = prime * result + Objects.hashCode(schemaType);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UploadSchema other = (UploadSchema) obj;
        return Objects.equals(fieldDefinitions, other.fieldDefinitions) && Objects.equals(name, other.name)
                && Objects.equals(revision, other.revision) && Objects.equals(schemaId, other.schemaId)
                && Objects.equals(this.schemaType, other.schemaType);
    }

    @Override
    public String toString() {
        return String.format("UploadSchema[name=%s, revision=%s, schemaId=%s, schemaType=%s, fieldDefinitions=[%s]]",
                name, revision, schemaId, schemaType.name(), Joiner.on(", ").join(fieldDefinitions));
    }

    /** Builder for UploadSchema */
    public static class Builder {
        private List<UploadFieldDefinition> fieldDefinitions;
        private String name;
        private Integer revision;
        private String schemaId;
        private UploadSchemaType schemaType;

        /**
         * Sets all builder fields to be a copy of the specified schema. This returns the builder, which can be used to
         * make additional changes to the schema being built. This is commonly used for updating a schema from a
         * pre-existing one.
         * @param uploadSchema
         * @return builder
         */
        public Builder copyOf(UploadSchema other) {
            this.fieldDefinitions = other.fieldDefinitions;
            this.name = other.name;
            this.revision = other.revision;
            this.schemaId = other.schemaId;
            this.schemaType = other.schemaType;
            return this;
        }

        /** 
         * @see org.sagebionetworks.bridge.sdk.models.upload.UploadSchema#getFieldDefinitions 
         * @return builder
         */
        public List<UploadFieldDefinition> getFieldDefinitions() {
            return fieldDefinitions;
        }

        /** 
         * @see org.sagebionetworks.bridge.sdk.models.upload.UploadSchema#getFieldDefinitions
         * @param fieldDefinitions 
         * @return builder
         */
        @JsonProperty("fieldDefinitions")
        public Builder withFieldDefinitions(List<UploadFieldDefinition> fieldDefinitions) {
            this.fieldDefinitions = fieldDefinitions;
            return this;
        }

        /** 
         * @see org.sagebionetworks.bridge.sdk.models.upload.UploadSchema#getFieldDefinitions
         * @param fieldDefinitions
         * @return builder
         */
        @JsonIgnore
        public Builder withFieldDefinitions(UploadFieldDefinition... fieldDefinitions) {
            this.fieldDefinitions = ImmutableList.copyOf(fieldDefinitions);
            return this;
        }

        /** 
         * @see org.sagebionetworks.bridge.sdk.models.upload.UploadSchema#getName
         * @return builder
         */
        public String getName() {
            return name;
        }

        /** 
         * @see org.sagebionetworks.bridge.sdk.models.upload.UploadSchema#getName
         * @param name
         * @return builder
         */
        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        /** 
         * @see org.sagebionetworks.bridge.sdk.models.upload.UploadSchema#getRevision
         * @return builder
         */
        public Integer getRevision() {
            return revision;
        }

        /** 
         * @see org.sagebionetworks.bridge.sdk.models.upload.UploadSchema#getRevision
         * @param revision
         * @return builder
         */
        public Builder withRevision(Integer revision) {
            this.revision = revision;
            return this;
        }

        /** 
         * @see org.sagebionetworks.bridge.sdk.models.upload.UploadSchema#getSchemaId
         * @return schemaId
         */
        public String getSchemaId() {
            return schemaId;
        }

        /** 
         * @see org.sagebionetworks.bridge.sdk.models.upload.UploadSchema#getSchemaId
         * @param schemaId;
         * @return builder
         */
        public Builder withSchemaId(String schemaId) {
            this.schemaId = schemaId;
            return this;
        }

        /** 
         * @see org.sagebionetworks.bridge.sdk.models.upload.UploadSchema#getSchemaType
         * @return uploadSchemaType
         */
        public UploadSchemaType getSchemaType() {
            return schemaType;
        }

        /** 
         * @see org.sagebionetworks.bridge.sdk.models.upload.UploadSchema#getSchemaType
         * @param schemaType
         * @return builder
         */
        public Builder withSchemaType(UploadSchemaType schemaType) {
            this.schemaType = schemaType;
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

            return new UploadSchema(ImmutableList.copyOf(fieldDefinitions), name, revision, schemaId, schemaType);
        }
    }
}
