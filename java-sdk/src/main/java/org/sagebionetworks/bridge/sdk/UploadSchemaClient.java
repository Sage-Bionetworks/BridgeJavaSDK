package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.upload.UploadSchema;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.net.UrlEscapers;

public class UploadSchemaClient extends BaseApiCaller {
    
    private static final TypeReference<ResourceList<UploadSchema>> TYPE_REF_UPLOAD_SCHEMA_LIST =
            new TypeReference<ResourceList<UploadSchema>>() {};
    
    UploadSchemaClient(BridgeSession session) {
        super(session);
    }
    
    /**
     * Creates a schema revision using the new V4 semantics. The schema ID and revision will be taken from the
     * UploadSchema object. If the revision isn't specified, we'll get the latest schema rev for the schema ID and use
     * that rev + 1.
     *
     * @param schema
     *         schema to create, must be non-null
     * @return the created schema, will be non-null
     */
    public UploadSchema createSchemaRevisionV4(UploadSchema schema) {
        session.checkSignedIn();
        checkNotNull(schema, CANNOT_BE_NULL, "schema");
        return post(config.getUploadSchemasV4Api(), schema, UploadSchema.class);
    }

    /**
     * Gets an upload schema by study ID, schema ID, and revision.
     *
     * @param studyId
     *         study the schema lives in
     * @param schemaId
     *         schema to fetch
     * @param revision
     *         revision to fetch
     * @return the specified schema
     */
    public UploadSchema getSchema(String studyId, String schemaId, int revision) {
        session.checkSignedIn();
        checkArgument(isNotBlank(studyId), CANNOT_BE_BLANK, "studyId");
        checkArgument(isNotBlank(schemaId), CANNOT_BE_BLANK, "schemaId");
        checkArgument(revision > 0, "revision must be positive");

        String encodedSchemaId = UrlEscapers.urlPathSegmentEscaper().escape(schemaId);
        return get(config.getUploadSchemaApi(studyId, encodedSchemaId, revision), UploadSchema.class);
    }
    
    /**
     * This method creates an upload schema in the current study, using the schema ID of the specified schema, or
     * updates an existing schema if it already exists. This method returns the created schema, which has its revision
     * number properly updated.
     *
     * @param uploadSchema
     *            schema to create or update, must be non-null
     * @return the created or updated schema, will be non-null
     */
    public UploadSchema createOrUpdateUploadSchema(UploadSchema schema) {
        session.checkSignedIn();
        checkNotNull(schema, CANNOT_BE_NULL, "schema");
        return post(config.getUploadSchemasApi(), schema, UploadSchema.class);
    }
    
    /**
     * This method deletes all revisions of the upload schema with the specified schema ID. If there are no schemas with
     * this schema ID, this method throws an EntityNotFoundException.
     *
     * @param schemaId
     *            schema ID of the upload schemas to delete, must be non-null and non-empty
     */
    public void deleteUploadSchemaAllRevisions(String schemaId) {
        session.checkSignedIn();
        checkArgument(isNotBlank(schemaId), CANNOT_BE_BLANK, "schemaId");

        String encodedSchemaId = UrlEscapers.urlPathSegmentEscaper().escape(schemaId);
        delete(config.getUploadSchemaAllRevisionsApi(encodedSchemaId));
    }
    
    /**
     * This method deletes an upload schema with the specified schema ID and revision. If the schema doesn't exist, this
     * method throws an EntityNotFoundException.
     *
     * @param schemaId
     *            schema ID of the upload schema to delete
     * @param revision
     *            revision number of the upload schema to delete, must be positive
     */
    public void deleteUploadSchema(String schemaId, int revision) {
        session.checkSignedIn();
        checkArgument(isNotBlank(schemaId), CANNOT_BE_BLANK, "schemaId");
        checkArgument(revision > 0, "revision must be positive");

        String encodedSchemaId = UrlEscapers.urlPathSegmentEscaper().escape(schemaId);
        delete(config.getUploadSchemaApi(encodedSchemaId, revision));
    }
    
    /**
     * This method fetches all revisions of an upload schema from the current study with the specified schema ID. If 
     * the schema doesn't exist, this method throws an EntityNotFoundException.
     *
     * @param schemaId
     *            ID of the schema to fetch, must be non-null and non-empty
     * @return the fetched schema, will be non-null
     */
    public ResourceList<UploadSchema> getUploadSchema(String schemaId) {
        session.checkSignedIn();
        checkArgument(isNotBlank(schemaId), CANNOT_BE_BLANK, "schemaId");

        String encodedSchemaId = UrlEscapers.urlPathSegmentEscaper().escape(schemaId);
        return get(config.getUploadSchemaAllRevisionsApi(encodedSchemaId), TYPE_REF_UPLOAD_SCHEMA_LIST);
    }
    
    /**
     * This method fetches the most recent revision of an upload schema from the current study with the specified 
     * schema ID (the version with the highest revision number). If the schema doesn't exist, this method throws an 
     * EntityNotFoundException.
     * @param schemaId
     * @return
     */
    public UploadSchema getMostRecentUploadSchemaRevision(String schemaId) {
        session.checkSignedIn();
        checkArgument(isNotBlank(schemaId), CANNOT_BE_BLANK, "schemaId");

        String encodedSchemaId = UrlEscapers.urlPathSegmentEscaper().escape(schemaId);
        return get(config.getMostRecentUploadSchemaApi(encodedSchemaId), UploadSchema.class);
    }
    
    /**
     * Fetches the most recent revision of all upload schemas in the current study.
     *
     * @return a list of upload schemas
     */
    public ResourceList<UploadSchema> getAllUploadSchemas() {
        session.checkSignedIn();
        return get(config.getUploadSchemasApi(), TYPE_REF_UPLOAD_SCHEMA_LIST);
    }
    
    /**
     * Updates a schema revision using V4 semantics. This updates the schema revision in place, keeping the same ID and
     * revision. A schema update cannot delete any fields or modified fields, except for adding the maxAppVersion
     * attribute to a field.
     *
     * @param schemaId
     *         schema ID to update, must be non-null and non-empty
     * @param revision
     *         schema revision to update, must be positive
     * @param schema
     *         schema that contains the updates to submit to the server, must be non-null
     * @return updated schema, will be non-null
     */
    public UploadSchema updateSchemaRevisionV4(String schemaId, int revision, UploadSchema schema) {
        session.checkSignedIn();
        checkArgument(isNotBlank(schemaId), CANNOT_BE_BLANK, "schemaId");
        checkArgument(revision > 0, "revision must be positive");
        checkNotNull(schema, CANNOT_BE_NULL, "schema");

        String encodedSchemaId = UrlEscapers.urlPathSegmentEscaper().escape(schemaId);
        return post(config.getUploadSchemaV4Api(encodedSchemaId, revision), schema, UploadSchema.class);
    }
}
