package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleGuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleGuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.studies.Study;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.upload.UploadSchema;

import com.fasterxml.jackson.core.type.TypeReference;

class BridgeDeveloperClient extends BaseApiCaller implements DeveloperClient {
    
    private final TypeReference<ResourceListImpl<Survey>> sType = new TypeReference<ResourceListImpl<Survey>>() {};
    private final TypeReference<ResourceListImpl<SchedulePlan>> spType = new TypeReference<ResourceListImpl<SchedulePlan>>() {};
    private static final TypeReference<ResourceListImpl<UploadSchema>> TYPE_REF_UPLOAD_SCHEMA_LIST =
            new TypeReference<ResourceListImpl<UploadSchema>>() {};

    BridgeDeveloperClient(BridgeSession session) {
        super(session);
    }

    @Override
    public Survey getSurvey(String guid, DateTime createdOn) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(createdOn, Bridge.CANNOT_BE_NULL, "createdOn");
        return get(config.getSurveyApi(guid, createdOn), Survey.class);
    }
    @Override
    public Survey getSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/createdOn keys");
        return get(config.getSurveyApi(keys.getGuid(), keys.getCreatedOn()), Survey.class);
    }
    @Override
    public ResourceList<Survey> getSurveyAllRevisions(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        return get(config.getSurveyRevisionsApi(guid), sType);
    }
    @Override
    public Survey getSurveyMostRecentlyPublished(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        return get(config.getSurveyMostRecentlyPublishedRevisionApi(guid), Survey.class);
    }
    @Override
    public Survey getSurveyMostRecentlyPublishedByIdentifier(String identifier) {
        session.checkSignedIn();
        checkArgument(isNotBlank(identifier), Bridge.CANNOT_BE_BLANK, "identifier");
        return get(config.getSurveyMostRecentlyPublishedRevisionByIdentifierApi(identifier), Survey.class);
    }
    @Override
    public Survey getSurveyMostRecent(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        return get(config.getSurveyMostRecentRevisionApi(guid), Survey.class);
    }
    @Override
    public ResourceList<Survey> getAllSurveysMostRecentlyPublished() {
        session.checkSignedIn();
        return get(config.getSurveysPublishedApi(), sType);
    }
    @Override
    public ResourceList<Survey> getAllSurveysMostRecent() {
        session.checkSignedIn();
        return get(config.getSurveysRecentApi(), sType);
    }
    @Override
    public GuidCreatedOnVersionHolder createSurvey(Survey survey) {
        session.checkSignedIn();
        checkNotNull(survey, Bridge.CANNOT_BE_NULL,"Survey object");
        
        GuidCreatedOnVersionHolder holder = post(config.getSurveysApi(), survey, SimpleGuidCreatedOnVersionHolder.class);
        survey.setGuidCreatedOnVersionHolder(holder);
        return holder;
    }
    @Override
    public GuidCreatedOnVersionHolder versionSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/createdOn keys");

        return post(config.getSurveyNewRevisionApi(keys.getGuid(), keys.getCreatedOn()), null, SimpleGuidCreatedOnVersionHolder.class);
    }
    @Override
    public GuidCreatedOnVersionHolder updateSurvey(Survey survey) {
        session.checkSignedIn();
        checkNotNull(survey, Bridge.CANNOT_BE_NULL,"Survey object");
        
        GuidCreatedOnVersionHolder holder = post(
                config.getSurveyApi(survey.getGuid(), new DateTime(survey.getCreatedOn())), survey,
                SimpleGuidCreatedOnVersionHolder.class);
        survey.setGuidCreatedOnVersionHolder(holder);
        return holder;
    }
    @Override
    public GuidCreatedOnVersionHolder publishSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/createdOn keys");

        return post(config.getPublishSurveyApi(keys.getGuid(), keys.getCreatedOn()), null, 
                SimpleGuidCreatedOnVersionHolder.class);
    }
    @Override
    public GuidCreatedOnVersionHolder versionUpdateAndPublishSurvey(Survey survey, boolean publish) {
        session.checkSignedIn();
        checkNotNull(survey, Bridge.CANNOT_BE_NULL, "survey");
        
        // in essence, updating new version to hold all the data of the supplied survey.
        GuidCreatedOnVersionHolder keys = versionSurvey(survey);
        survey.setGuidCreatedOnVersionHolder(keys);
        keys = updateSurvey(survey);
        survey.setGuidCreatedOnVersionHolder(keys);
        if (publish) {
            keys = publishSurvey(survey);
            survey.setGuidCreatedOnVersionHolder(keys);
        }
        return keys;
    }
    @Override
    public void closeSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/createdOn keys");

        post(config.getCloseSurveyApi(keys.getGuid(), keys.getCreatedOn()));
    }
    @Override
    public void deleteSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/createdOn keys");

        delete(config.getSurveyApi(keys.getGuid(), keys.getCreatedOn()));
    }
    @Override
    public ResourceList<SchedulePlan> getSchedulePlans() {
        session.checkSignedIn();
        return get(config.getSchedulePlansApi(), spType);
    }
    @Override
    public GuidVersionHolder createSchedulePlan(SchedulePlan plan) {
        session.checkSignedIn();
        checkNotNull(plan, Bridge.CANNOT_BE_NULL, "SchedulePlan");
        
        GuidVersionHolder holder = post(config.getSchedulePlansApi(), plan, SimpleGuidVersionHolder.class);
        plan.setGuid(holder.getGuid());
        plan.setVersion(holder.getVersion());
        return holder;
    }
    @Override
    public SchedulePlan getSchedulePlan(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        return get(config.getSchedulePlanApi(guid), SchedulePlan.class);
    }
    @Override
    public GuidVersionHolder updateSchedulePlan(SchedulePlan plan) {
        session.checkSignedIn();
        checkNotNull(plan, Bridge.CANNOT_BE_NULL, "SchedulePlan");
        GuidVersionHolder holder = post(config.getSchedulePlanApi(plan.getGuid()), plan, SimpleGuidVersionHolder.class);
        plan.setVersion(holder.getVersion());
        return holder;
    }
    @Override
    public void deleteSchedulePlan(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        delete(config.getSchedulePlanApi(guid));
    }
    @Override
    public Study getStudy() {
        session.checkSignedIn();
        return get(config.getResearcherStudyApi(), Study.class);
    }
    @Override
    public VersionHolder updateStudy(Study study) {
        session.checkSignedIn();
        checkNotNull(study, Bridge.CANNOT_BE_NULL, "study");
        checkNotNull(isNotBlank(study.getIdentifier()), Bridge.CANNOT_BE_BLANK, "study identifier");
        
        VersionHolder holder = post(config.getResearcherStudyApi(), study, SimpleVersionHolder.class);
        study.setVersion(holder.getVersion());
        return holder;
    }

    @Override
    public UploadSchema createOrUpdateUploadSchema(UploadSchema schema) {
        session.checkSignedIn();
        checkNotNull(schema, Bridge.CANNOT_BE_NULL, "schema");
        return post(config.getUploadSchemaApi(), schema, UploadSchema.class);
    }

    @Override
    public void deleteUploadSchemaAllRevisions(String schemaId) {
        session.checkSignedIn();
        checkArgument(isNotBlank(schemaId), Bridge.CANNOT_BE_BLANK, "schemaId");
        delete(config.getUploadSchemaByIdApi(schemaId));
    }

    @Override
    public void deleteUploadSchema(String schemaId, int revision) {
        session.checkSignedIn();
        checkArgument(isNotBlank(schemaId), Bridge.CANNOT_BE_BLANK, "schemaId");
        checkArgument(revision > 0, "revision must be positive");
        delete(config.getUploadSchemaByIdAndRevisionApi(schemaId, revision));
    }

    @Override
    public UploadSchema getUploadSchema(String schemaId) {
        session.checkSignedIn();
        checkArgument(isNotBlank(schemaId), Bridge.CANNOT_BE_BLANK, "schemaId");
        return get(config.getUploadSchemaByIdApi(schemaId), UploadSchema.class);
    }

    @Override
    public ResourceList<UploadSchema> getAllUploadSchemasAllRevisions() {
        session.checkSignedIn();
        return get(config.getUploadSchemaApi(), TYPE_REF_UPLOAD_SCHEMA_LIST);
    }
}
