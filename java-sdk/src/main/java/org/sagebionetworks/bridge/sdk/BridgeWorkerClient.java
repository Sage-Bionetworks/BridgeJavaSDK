package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.net.UrlEscapers;
import org.joda.time.DateTime;

import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.upload.UploadSchema;

/** Bridge implementation of the worker client. */
class BridgeWorkerClient extends BaseApiCaller implements WorkerClient {
    /**
     * Client constructor. Package-scoped because this should only be created through the BridgeSession.
     *
     * @param session
     *         Bridge session backing the client
     */
    BridgeWorkerClient(BridgeSession session) {
        super(session);
    }

    /** {@inheritDoc} */
    @Override
    public UploadSchema getSchema(String studyId, String schemaId, int revision) {
        session.checkSignedIn();
        checkArgument(isNotBlank(studyId), CANNOT_BE_BLANK, "studyId");
        checkArgument(isNotBlank(schemaId), CANNOT_BE_BLANK, "schemaId");
        checkArgument(revision > 0, "revision must be positive");

        String encodedSchemaId = UrlEscapers.urlPathSegmentEscaper().escape(schemaId);
        return get(config.getUploadSchemaApi(studyId, encodedSchemaId, revision), UploadSchema.class);
    }

    /** {@inheritDoc} */
    @Override
    public ResourceList<Survey> getAllSurveysMostRecentlyPublished(String studyId) {
        session.checkSignedIn();
        checkArgument(isNotBlank(studyId), "studyId");
        return get(config.getPublishedSurveysForStudyApi(studyId), new TypeReference<ResourceList<Survey>>(){});
    }

    /** {@inheritDoc} */
    @Override
    public Survey getSurvey(String guid, DateTime createdOn) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), CANNOT_BE_BLANK, "guid");
        checkNotNull(createdOn, CANNOT_BE_NULL, "createdOn");
        return get(config.getSurveyApi(guid, createdOn), Survey.class);
    }

    /** {@inheritDoc} */
    @Override
    public Survey getSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, CANNOT_BE_NULL, "key holder");
        checkArgument(isNotBlank(keys.getGuid()), CANNOT_BE_BLANK, "guid");
        checkNotNull(keys.getCreatedOn(), CANNOT_BE_NULL, "createdOn");
        return get(config.getSurveyApi(keys.getGuid(), keys.getCreatedOn()), Survey.class);
    }
}
