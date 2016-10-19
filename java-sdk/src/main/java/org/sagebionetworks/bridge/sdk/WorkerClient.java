package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.joda.time.DateTime;

import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.healthData.RecordExportStatusRequest;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.upload.UploadSchema;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.net.UrlEscapers;

/** Bridge implementation of the worker client. */
public class WorkerClient extends BaseApiCaller {
    /**
     * Client constructor. Package-scoped because this should only be created through the BridgeSession.
     *
     * @param session
     *         Bridge session backing the client
     */
    WorkerClient(BridgeSession session) {
        super(session);
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
     * Gets the most recently published version of all surveys for a given study.
     *
     * @param studyId
     *         study to get surveys for
     * @return list of surveys
     */
    public ResourceList<Survey> getAllSurveysMostRecentlyPublished(String studyId) {
        session.checkSignedIn();
        checkArgument(isNotBlank(studyId), "studyId");
        return get(config.getPublishedSurveysForStudyApi(studyId), new TypeReference<ResourceList<Survey>>(){});
    }

    /**
     * Gets the specified survey by guid and createdOn
     *
     * @param guid
     *         survey guid
     * @param createdOn
     *         created-on timestamp for the survey
     * @return the specified survey
     */
    public Survey getSurvey(String guid, DateTime createdOn) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), CANNOT_BE_BLANK, "guid");
        checkNotNull(createdOn, CANNOT_BE_NULL, "createdOn");
        return get(config.getSurveyApi(guid, createdOn), Survey.class);
    }

    /**
     * Gets the specified survey by key holder.
     *
     * @param keys
     *         key holder containing the survey guid and createdOn
     * @return the specified survey
     */
    public Survey getSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, CANNOT_BE_NULL, "key holder");
        checkArgument(isNotBlank(keys.getGuid()), CANNOT_BE_BLANK, "guid");
        checkNotNull(keys.getCreatedOn(), CANNOT_BE_NULL, "createdOn");
        return get(config.getSurveyApi(keys.getGuid(), keys.getCreatedOn()), Survey.class);
    }

    /**
     * Marks a file upload session as completed.
     *
     * @param uploadId upload session id
     */
    public void completeUpload(String uploadId) {
        session.checkSignedIn();
        post(config.getCompleteUploadApi(uploadId));
    }

    /**
     * update record's exporter status
     */
    public void updateRecordExporterStatus(RecordExportStatusRequest recordExportStatusRequest) {
        session.checkSignedIn();
        checkNotNull(recordExportStatusRequest);
        checkNotNull(recordExportStatusRequest.getRecordIds());
        checkNotNull(recordExportStatusRequest.getSynapseExporterStatus());
        post(config.getUpdateRecordExportStatusesApi(), recordExportStatusRequest);
    }
}
