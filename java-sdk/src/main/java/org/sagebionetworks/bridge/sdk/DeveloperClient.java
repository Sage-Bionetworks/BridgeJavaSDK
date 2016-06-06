package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import com.google.common.net.UrlEscapers;
import org.joda.time.DateTime;

import org.sagebionetworks.bridge.sdk.models.PagedResourceList;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.accounts.ExternalIdentifier;
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

import com.fasterxml.jackson.core.type.TypeReference;

public class DeveloperClient extends StudyStaffClient {
    
    private final TypeReference<ResourceList<StudyConsent>> scType = new TypeReference<ResourceList<StudyConsent>>() {};
    private final TypeReference<ResourceList<Survey>> sType = new TypeReference<ResourceList<Survey>>() {};
    private final TypeReference<ResourceList<SchedulePlan>> spType = new TypeReference<ResourceList<SchedulePlan>>() {};
    private final TypeReference<ResourceList<Subpopulation>> subpopType = new TypeReference<ResourceList<Subpopulation>>() {};
    private static final TypeReference<ResourceList<UploadSchema>> TYPE_REF_UPLOAD_SCHEMA_LIST =
            new TypeReference<ResourceList<UploadSchema>>() {};
    private static final TypeReference<PagedResourceList<ExternalIdentifier>> EXTERNAL_IDENTIFIER_PAGED_RESOURCE_LIST = 
            new TypeReference<PagedResourceList<ExternalIdentifier>>() {};

    DeveloperClient(BridgeSession session) {
        super(session);
    }

    /**
     * Get all revisions of the consent document for the study of the current researcher.
     * @param subpopGuid
     * @return List<StudyConsent>
     */
    public ResourceList<StudyConsent> getAllStudyConsents(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");

        return get(config.getConsentsApi(subpopGuid), scType);
    }
    
    /**
     * Get the published consent document revision. This is the revision that is sent to users, and should be a
     * version of the consent that has been approved by your IRB. Only one revision of your consent is published 
     * at any given time.
     * @param subpopGuid
     * @return StudyConsent
     */
    public StudyConsent getPublishedStudyConsent(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        
        return get(config.getPublishedStudyConsentApi(subpopGuid), StudyConsent.class);
    }
    
    /**
     * Get the most recent revision of the consent document.
     * @param subpopGuid
     * @return StudyConsent
     */
    public StudyConsent getMostRecentStudyConsent(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        
        return get(config.getMostRecentStudyConsentApi(subpopGuid), StudyConsent.class);
    }
    
    /**
     * Get a consent document that was created at a DateTime.
     *
     * @param subpopGuid
     * @param createdOn
     *            The DateTime the consent document was created on (this DateTime identifies the consent document).
     * @return StudyConsent
     */
    public StudyConsent getStudyConsent(SubpopulationGuid subpopGuid, DateTime createdOn) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        checkNotNull(createdOn, CANNOT_BE_NULL, "createdOn");

        return get(config.getConsentApi(subpopGuid, createdOn), StudyConsent.class);
    }
    
    /**
     * Create a consent document revision.
     * @param subpopGuid
     * @param consent
     *            The consent document to add.
     */
    public void createStudyConsent(SubpopulationGuid subpopGuid, StudyConsent consent) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        checkNotNull(consent, CANNOT_BE_NULL, "consent");

        post(config.getConsentsApi(subpopGuid), consent, StudyConsent.class);
    }
    
    /**
     * Publish a consent document created at a DateTime. The prior published revision will no longer be published.
     * @param subpopGuid
     * @param createdOn
     *            DateTime consent document was created. This acts as an identifier for the consent document.
     */
    public void publishStudyConsent(SubpopulationGuid subpopGuid, DateTime createdOn) {
        session.checkSignedIn();
        checkNotNull(subpopGuid, CANNOT_BE_NULL, "subpopGuid");
        checkNotNull(createdOn, CANNOT_BE_NULL, "createdOn");

        post(config.getPublishStudyConsentApi(subpopGuid, createdOn));
    }
    
    /**
     * Get the survey by its GUID for a particular DateTime revision.
     *
     * @param guid
     *            GUID identifying the survey.
     * @param revision
     *            The DateTime the survey was versioned on.
     * @return Survey
     */
    public Survey getSurvey(String guid, DateTime createdOn) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), CANNOT_BE_BLANK, "guid");
        checkNotNull(createdOn, CANNOT_BE_NULL, "createdOn");
        return get(config.getSurveyApi(guid, createdOn), Survey.class);
    }
    
    /**
     * Get a specific survey instance as identified by the GUID and createdOn keys.
     * 
     * @param keys
     * @return
     */
    public Survey getSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, CANNOT_BE_NULL, "guid/createdOn keys");
        return get(config.getSurveyApi(keys.getGuid(), keys.getCreatedOn()), Survey.class);
    }
    
    /**
     * Get all versions of a survey (the entire history of edits to that survey), most recent edit first in the list.
     * 
     * @param guid
     * @return
     */
    public ResourceList<Survey> getSurveyAllRevisions(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), CANNOT_BE_BLANK, "guid");
        return get(config.getSurveyAllRevisionsApi(guid), sType);
    }
    
    /**
     * Get the most recent version of a survey that has been published (the version with the latest createdOn timestamp
     * that has been published). Note that this does not return "the survey version that was most recently switched to
     * the published state". Publishing versions out of their creation order does not change the version that is
     * returned by this method unless a later version is switched to the published state.
     * 
     * @param guid
     *            The guid of the survey
     * @return
     */
    public Survey getSurveyMostRecentlyPublished(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), CANNOT_BE_BLANK, "guid");
        return get(config.getMostRecentlyPublishedSurveyRevisionApi(guid), Survey.class);
    }
    
    /**
     * Get the most recent version of a survey (the version with the latest createdOn timestamp).
     * 
     * @param guid
     * @return
     */
    public Survey getSurveyMostRecent(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), CANNOT_BE_BLANK, "guid");
        return get(config.getMostRecentSurveyRevisionApi(guid), Survey.class);
    }
    
    /**
     * Get the most recent and published version of every survey in a study (each survey with a unique GUID).
     * 
     * @return
     */
    public ResourceList<Survey> getAllSurveysMostRecentlyPublished() {
        session.checkSignedIn();
        return get(config.getPublishedSurveysApi(), sType);
    }
    
    /**
     * Get the most recent version of every survey in a study (each survey with a unique GUID).
     * 
     * @return
     */
    public ResourceList<Survey> getAllSurveysMostRecent() {
        session.checkSignedIn();
        return get(config.getRecentSurveysApi(), sType);
    }
    
    /**
     * Create a survey. Consented study participants cannot see or respond to the survey until it is published.
     *
     * @param survey
     *            The survey object Bridge will use to create a survey.
     * @return GuidVersionedOnHolder A holder containing the GUID identifying the survey and the DateTime on which it
     *         was versioned.
     */
    public GuidCreatedOnVersionHolder createSurvey(Survey survey) {
        session.checkSignedIn();
        checkNotNull(survey, CANNOT_BE_NULL,"Survey object");
        
        GuidCreatedOnVersionHolder holder = post(config.getSurveysApi(), survey, SimpleGuidCreatedOnVersionHolder.class);
        survey.setGuidCreatedOnVersionHolder(holder);
        return holder;
    }
    
    /**
     * Create a new version for the survey identified by a guid string and the DateTime it was versioned on.
     *
     * @param keys
     *            holder object containing a GUID string and DateTime of survey's version.
     * @return
     */
    public GuidCreatedOnVersionHolder versionSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, CANNOT_BE_NULL, "guid/createdOn keys");

        return post(config.getVersionSurveyApi(keys.getGuid(), keys.getCreatedOn()), null, SimpleGuidCreatedOnVersionHolder.class);
    }
    
    /**
     * Update a survey on Bridge.
     *
     * @param survey
     *            The survey object used to update the Survey on Bridge.
     * @return GuidVersionedOnHolder A holder containing the GUID identifying the updated survey and the DateTime the
     *         updated survey was versioned.
     */
    public GuidCreatedOnVersionHolder updateSurvey(Survey survey) {
        session.checkSignedIn();
        checkNotNull(survey, CANNOT_BE_NULL,"Survey object");
        
        GuidCreatedOnVersionHolder holder = post(
                config.getSurveyApi(survey.getGuid(), new DateTime(survey.getCreatedOn())), survey,
                SimpleGuidCreatedOnVersionHolder.class);
        survey.setGuidCreatedOnVersionHolder(holder);
        return holder;
    }
    
    /**
     * Publish a survey. A published survey is one consented users can see and respond to.
     *
     * @param keys
     *            holder object containing a GUID string identifying the survey and DateTime of survey's version.
     */
    public GuidCreatedOnVersionHolder publishSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, CANNOT_BE_NULL, "guid/createdOn keys");

        return post(config.getPublishSurveyApi(keys.getGuid(), keys.getCreatedOn()), null, 
                SimpleGuidCreatedOnVersionHolder.class);
    }
    
    /**
     * Take the supplied instance of a survey, make it the most recent version of the survey, save it, and then
     * immediately publish it if indicated. This combines several operations for the common case of making trivial fixes
     * to a survey,
     * 
     * @param survey
     *            survey to update (cannot be the initial creation of a survey)
     * @param publish
     *            should this new version be published immediately after being saved?
     * @return
     */
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
    
    /**
     * Delete a survey.
     *
     * @param keys
     *            holder object containing a GUID string identifying the survey and DateTime of survey's version.
     */
    public void deleteSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, CANNOT_BE_NULL, "guid/createdOn keys");

        delete(config.getSurveyApi(keys.getGuid(), keys.getCreatedOn()));
    }
    
    /**
     * Get all schedule plans.
     *
     * @return List<SchedulePlan>
     */
    public ResourceList<SchedulePlan> getSchedulePlans() {
        session.checkSignedIn();
        return get(config.getSchedulePlansApi(), spType);
    }
    
    /**
     * Create a schedule plan.
     *
     * @param plan
     *            The plan object Bridge will use to create a SchedulePlan.
     * @return GuidVersionHolder A holder containing the the guid and version of the created SchedulePlan.
     */
    public GuidVersionHolder createSchedulePlan(SchedulePlan plan) {
        session.checkSignedIn();
        checkNotNull(plan, CANNOT_BE_NULL, "SchedulePlan");
        
        GuidVersionHolder holder = post(config.getSchedulePlansApi(), plan, SimpleGuidVersionHolder.class);
        plan.setGuid(holder.getGuid());
        plan.setVersion(holder.getVersion());
        return holder;
    }
    
    /**
     * Get a schedule plan.
     *
     * @param guid
     *            GUID identifying the schedule plan to retrieve.
     * @return SchedulePlan
     */
    public SchedulePlan getSchedulePlan(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), CANNOT_BE_BLANK, "guid");
        return get(config.getSchedulePlanApi(guid), SchedulePlan.class);
    }
    
    /**
     * Update a schedule plan.
     *
     * @param plan
     *            The plan object Bridge will use to update it's Schedule Plan.
     * @return GuidVersionHolder Holder containing the guid and version of the updated SchedulePlan.
     */
    public GuidVersionHolder updateSchedulePlan(SchedulePlan plan) {
        session.checkSignedIn();
        checkNotNull(plan, CANNOT_BE_NULL, "SchedulePlan");
        GuidVersionHolder holder = post(config.getSchedulePlanApi(plan.getGuid()), plan, SimpleGuidVersionHolder.class);
        plan.setVersion(holder.getVersion());
        return holder;
    }
    
    /**
     * Delete a schedule plan.
     *
     * @param guid
     *            GUID identifying the schedule plan to delete.
     */
    public void deleteSchedulePlan(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), CANNOT_BE_BLANK, "guid");
        delete(config.getSchedulePlanApi(guid));
    }
    
    /**
     * Update information about this study.
     * 
     * @param study
     * @return
     */
    public VersionHolder updateStudy(Study study) {
        session.checkSignedIn();
        checkNotNull(study, CANNOT_BE_NULL, "study");
        checkNotNull(isNotBlank(study.getIdentifier()), CANNOT_BE_BLANK, "study identifier");
        
        VersionHolder holder = post(config.getCurrentStudyApi(), study, SimpleVersionHolder.class);
        study.setVersion(holder.getVersion());
        return holder;
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
    
    /**
     * Get all subpopulations defined for this study.
     * @return
     */
    public ResourceList<Subpopulation> getAllSubpopulations() {
        session.checkSignedIn();
        return get(config.getSubpopulations(), subpopType);
    }
    
    /**
     * Create a new subpopulation.
     * @param subpopulation
     */
    public GuidVersionHolder createSubpopulation(Subpopulation subpopulation) {
        session.checkSignedIn();
        return post(config.getSubpopulations(), subpopulation, GuidVersionHolder.class);
    }
    
    /**
     * Get a subpopulation using its GUID.
     * @param subpopGuid
     * @return
     */
    public Subpopulation getSubpopulation(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        return get(config.getSubpopulation(subpopGuid.getGuid()), Subpopulation.class);
    }
    
    /**
     * Update a subpopulation with the given GUID.
     * @param subpopulation
     */
    public GuidVersionHolder updateSubpopulation(Subpopulation subpopulation) {
        session.checkSignedIn();
        return post(config.getSubpopulation(subpopulation.getGuid().toString()), subpopulation, GuidVersionHolder.class);
    }
    
    /**
     * Delete a subpopulation. Studies start with a default subpopulation that cannot 
     * be deleted (although it can be edited). But other subpopulations can be deleted. 
     * Records of consent for a particular subpopulation are not deleted and historical 
     * records can be produced of consent to deleted subpopulations.
     * @param subpopGuid
     */
    public void deleteSubpopulation(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        delete(config.getSubpopulation(subpopGuid.getGuid()));
    }
    
    /**
     * Get a page of external identifiers added to Bridge. All arguments are optional. 
     * 
     * @param offsetKey
     *      Optional. If provided, records will be returned after this key in the list of identifiers. 
     *      Similar to an offsetBy index, but not as flexible, this essentially allows you to work page by 
     *      page through the records. 
     * @param pageSize
     *      Number of records to return for this request (1-100 records).
     * @param idFilter
     *      Optional string that used to match against the start of an external identifier string
     * @param assignmentFilter
     *      Optional boolean filter to return only assigned or unassigned identifiers (if not provided, both 
     *      are retuend).
     * @return
     */
    public PagedResourceList<ExternalIdentifier> getExternalIds(
            String offsetKey, Integer pageSize, String idFilter, Boolean assignmentFilter) {
        session.checkSignedIn();
        
        return get(config.getExternalIdsApi(
                offsetKey, pageSize, idFilter, assignmentFilter), EXTERNAL_IDENTIFIER_PAGED_RESOURCE_LIST);
    }
    
    /**
     * Add external identifiers to Bridge. Existing identifiers will be silently ignored.
     */
    public void addExternalIds(List<String> externalIdentifiers) {
        session.checkSignedIn();
        checkNotNull(externalIdentifiers);

        post(config.getExternalIdsApi(), externalIdentifiers);
    }
    
    /**
     * Delete external identifiers (only allows if external ID validation is diabled).
     * 
     * @param externalIdentifiers
     */
    public void deleteExternalIds(List<String> externalIdentifiers) {
        session.checkSignedIn();
        checkNotNull(externalIdentifiers);
        
        delete(config.getExternalIdsApi(externalIdentifiers));
    }
}
