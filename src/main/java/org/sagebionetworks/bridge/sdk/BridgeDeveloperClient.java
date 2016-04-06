package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import org.joda.time.DateTime;

import org.sagebionetworks.bridge.sdk.models.PagedResourceList;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleGuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleGuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.studies.Study;
import org.sagebionetworks.bridge.sdk.models.subpopulations.StudyConsent;
import org.sagebionetworks.bridge.sdk.models.subpopulations.Subpopulation;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.upload.UploadSchema;
import org.sagebionetworks.bridge.sdk.models.users.ExternalIdentifier;

import com.fasterxml.jackson.core.type.TypeReference;

class BridgeDeveloperClient extends BaseApiCaller implements DeveloperClient {
    
    private final TypeReference<ResourceListImpl<StudyConsent>> scType = new TypeReference<ResourceListImpl<StudyConsent>>() {};
    private final TypeReference<ResourceListImpl<Survey>> sType = new TypeReference<ResourceListImpl<Survey>>() {};
    private final TypeReference<ResourceListImpl<SchedulePlan>> spType = new TypeReference<ResourceListImpl<SchedulePlan>>() {};
    private final TypeReference<ResourceListImpl<Subpopulation>> subpopType = new TypeReference<ResourceListImpl<Subpopulation>>() {};
    private static final TypeReference<ResourceListImpl<UploadSchema>> TYPE_REF_UPLOAD_SCHEMA_LIST =
            new TypeReference<ResourceListImpl<UploadSchema>>() {};
    private static final TypeReference<PagedResourceList<ExternalIdentifier>> EXTERNAL_IDENTIFIER_PAGED_RESOURCE_LIST = 
            new TypeReference<PagedResourceList<ExternalIdentifier>>() {};

    BridgeDeveloperClient(BridgeSession session) {
        super(session);
    }

    @Override
    public ResourceList<StudyConsent> getAllStudyConsents(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");

        return get(config.getConsentsApi(subpopGuid), scType);
    }
    @Override
    public StudyConsent getPublishedStudyConsent(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        
        return get(config.getPublishedStudyConsentApi(subpopGuid), StudyConsent.class);
    }
    @Override
    public StudyConsent getMostRecentStudyConsent(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        
        return get(config.getMostRecentStudyConsentApi(subpopGuid), StudyConsent.class);
    }
    @Override
    public StudyConsent getStudyConsent(SubpopulationGuid subpopGuid, DateTime createdOn) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        checkNotNull(createdOn, CANNOT_BE_NULL, "createdOn");

        return get(config.getConsentApi(subpopGuid, createdOn), StudyConsent.class);
    }
    @Override
    public void createStudyConsent(SubpopulationGuid subpopGuid, StudyConsent consent) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        checkNotNull(consent, CANNOT_BE_NULL, "consent");

        post(config.getConsentsApi(subpopGuid), consent, StudyConsent.class);
    }
    @Override
    public void publishStudyConsent(SubpopulationGuid subpopGuid, DateTime createdOn) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        checkNotNull(createdOn, CANNOT_BE_NULL, "createdOn");

        post(config.getPublishStudyConsentApi(subpopGuid, createdOn));
    }
    @Override
    public Survey getSurvey(String guid, DateTime createdOn) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), CANNOT_BE_BLANK, "guid");
        checkNotNull(createdOn, CANNOT_BE_NULL, "createdOn");
        return get(config.getSurveyApi(guid, createdOn), Survey.class);
    }
    @Override
    public Survey getSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, CANNOT_BE_NULL, "guid/createdOn keys");
        return get(config.getSurveyApi(keys.getGuid(), keys.getCreatedOn()), Survey.class);
    }
    @Override
    public ResourceList<Survey> getSurveyAllRevisions(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), CANNOT_BE_BLANK, "guid");
        return get(config.getSurveyAllRevisionsApi(guid), sType);
    }
    @Override
    public Survey getSurveyMostRecentlyPublished(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), CANNOT_BE_BLANK, "guid");
        return get(config.getMostRecentlyPublishedSurveyRevisionApi(guid), Survey.class);
    }
    @Override
    public Survey getSurveyMostRecent(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), CANNOT_BE_BLANK, "guid");
        return get(config.getMostRecentSurveyRevisionApi(guid), Survey.class);
    }
    @Override
    public ResourceList<Survey> getAllSurveysMostRecentlyPublished() {
        session.checkSignedIn();
        return get(config.getPublishedSurveysApi(), sType);
    }
    @Override
    public ResourceList<Survey> getAllSurveysMostRecent() {
        session.checkSignedIn();
        return get(config.getRecentSurveysApi(), sType);
    }
    @Override
    public GuidCreatedOnVersionHolder createSurvey(Survey survey) {
        session.checkSignedIn();
        checkNotNull(survey, CANNOT_BE_NULL,"Survey object");
        
        GuidCreatedOnVersionHolder holder = post(config.getSurveysApi(), survey, SimpleGuidCreatedOnVersionHolder.class);
        survey.setGuidCreatedOnVersionHolder(holder);
        return holder;
    }
    @Override
    public GuidCreatedOnVersionHolder versionSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, CANNOT_BE_NULL, "guid/createdOn keys");

        return post(config.getVersionSurveyApi(keys.getGuid(), keys.getCreatedOn()), null, SimpleGuidCreatedOnVersionHolder.class);
    }
    @Override
    public GuidCreatedOnVersionHolder updateSurvey(Survey survey) {
        session.checkSignedIn();
        checkNotNull(survey, CANNOT_BE_NULL,"Survey object");
        
        GuidCreatedOnVersionHolder holder = post(
                config.getSurveyApi(survey.getGuid(), new DateTime(survey.getCreatedOn())), survey,
                SimpleGuidCreatedOnVersionHolder.class);
        survey.setGuidCreatedOnVersionHolder(holder);
        return holder;
    }
    @Override
    public GuidCreatedOnVersionHolder publishSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, CANNOT_BE_NULL, "guid/createdOn keys");

        return post(config.getPublishSurveyApi(keys.getGuid(), keys.getCreatedOn()), null, 
                SimpleGuidCreatedOnVersionHolder.class);
    }
    @Override
    public GuidCreatedOnVersionHolder versionUpdateAndPublishSurvey(Survey survey, boolean publish) {
        session.checkSignedIn();
        checkNotNull(survey, CANNOT_BE_NULL, "survey");
        
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
    public void deleteSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, CANNOT_BE_NULL, "guid/createdOn keys");

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
        checkNotNull(plan, CANNOT_BE_NULL, "SchedulePlan");
        
        GuidVersionHolder holder = post(config.getSchedulePlansApi(), plan, SimpleGuidVersionHolder.class);
        plan.setGuid(holder.getGuid());
        plan.setVersion(holder.getVersion());
        return holder;
    }
    @Override
    public SchedulePlan getSchedulePlan(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), CANNOT_BE_BLANK, "guid");
        return get(config.getSchedulePlanApi(guid), SchedulePlan.class);
    }
    @Override
    public GuidVersionHolder updateSchedulePlan(SchedulePlan plan) {
        session.checkSignedIn();
        checkNotNull(plan, CANNOT_BE_NULL, "SchedulePlan");
        GuidVersionHolder holder = post(config.getSchedulePlanApi(plan.getGuid()), plan, SimpleGuidVersionHolder.class);
        plan.setVersion(holder.getVersion());
        return holder;
    }
    @Override
    public void deleteSchedulePlan(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), CANNOT_BE_BLANK, "guid");
        delete(config.getSchedulePlanApi(guid));
    }
    @Override
    public Study getStudy() {
        session.checkSignedIn();
        return get(config.getCurrentStudyApi(), Study.class);
    }
    @Override
    public VersionHolder updateStudy(Study study) {
        session.checkSignedIn();
        checkNotNull(study, CANNOT_BE_NULL, "study");
        checkNotNull(isNotBlank(study.getIdentifier()), CANNOT_BE_BLANK, "study identifier");
        
        VersionHolder holder = post(config.getCurrentStudyApi(), study, SimpleVersionHolder.class);
        study.setVersion(holder.getVersion());
        return holder;
    }

    @Override
    public UploadSchema createOrUpdateUploadSchema(UploadSchema schema) {
        session.checkSignedIn();
        checkNotNull(schema, CANNOT_BE_NULL, "schema");
        return post(config.getUploadSchemasApi(), schema, UploadSchema.class);
    }

    @Override
    public void deleteUploadSchemaAllRevisions(String schemaId) {
        session.checkSignedIn();
        checkArgument(isNotBlank(schemaId), CANNOT_BE_BLANK, "schemaId");
        delete(config.getUploadSchemaAllRevisionsApi(schemaId));
    }

    @Override
    public void deleteUploadSchema(String schemaId, int revision) {
        session.checkSignedIn();
        checkArgument(isNotBlank(schemaId), CANNOT_BE_BLANK, "schemaId");
        checkArgument(revision > 0, "revision must be positive");
        delete(config.getUploadSchemaApi(schemaId, revision));
    }

    @Override
    public ResourceList<UploadSchema> getUploadSchema(String schemaId) {
        session.checkSignedIn();
        checkArgument(isNotBlank(schemaId), CANNOT_BE_BLANK, "schemaId");
        return get(config.getUploadSchemaAllRevisionsApi(schemaId), TYPE_REF_UPLOAD_SCHEMA_LIST);
    }
    
    @Override
    public UploadSchema getMostRecentUploadSchemaRevision(String schemaId) {
        session.checkSignedIn();
        checkArgument(isNotBlank(schemaId), CANNOT_BE_BLANK, "schemaId");
        return get(config.getMostRecentUploadSchemaApi(schemaId), UploadSchema.class);
    }

    @Override
    public ResourceList<UploadSchema> getAllUploadSchemas() {
        session.checkSignedIn();
        return get(config.getUploadSchemasApi(), TYPE_REF_UPLOAD_SCHEMA_LIST);
    }

    @Override
    public ResourceList<Subpopulation> getAllSubpopulations() {
        session.checkSignedIn();
        return get(config.getSubpopulations(), subpopType);
    }

    @Override
    public GuidVersionHolder createSubpopulation(Subpopulation subpopulation) {
        session.checkSignedIn();
        return post(config.getSubpopulations(), subpopulation, GuidVersionHolder.class);
    }

    @Override
    public Subpopulation getSubpopulation(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        return get(config.getSubpopulation(subpopGuid.getGuid()), Subpopulation.class);
    }

    @Override
    public GuidVersionHolder updateSubpopulation(Subpopulation subpopulation) {
        session.checkSignedIn();
        return post(config.getSubpopulation(subpopulation.getGuid().toString()), subpopulation, GuidVersionHolder.class);
    }

    @Override
    public void deleteSubpopulation(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        delete(config.getSubpopulation(subpopGuid.getGuid()));
    }
    @Override
    public PagedResourceList<ExternalIdentifier> getExternalIds(
            String offsetKey, Integer pageSize, String idFilter, Boolean assignmentFilter) {
        session.checkSignedIn();
        
        return get(config.getExternalIdsApi(
                offsetKey, pageSize, idFilter, assignmentFilter), EXTERNAL_IDENTIFIER_PAGED_RESOURCE_LIST);
    }
    @Override
    public void addExternalIds(List<String> externalIdentifiers) {
        session.checkSignedIn();
        checkNotNull(externalIdentifiers);

        post(config.getExternalIdsApi(), externalIdentifiers);
    }
    @Override
    public void deleteExternalIds(List<String> externalIdentifiers) {
        session.checkSignedIn();
        checkNotNull(externalIdentifiers);
        
        delete(config.getExternalIdsApi(externalIdentifiers));
    }
}
