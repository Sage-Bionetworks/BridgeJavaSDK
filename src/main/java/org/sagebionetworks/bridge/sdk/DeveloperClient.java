package org.sagebionetworks.bridge.sdk;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.studies.Study;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.upload.UploadSchema;

public interface DeveloperClient {

    /**
     * Get the survey by its GUID for a particular DateTime revision.
     *
     * @param guid
     *            GUID identifying the survey.
     * @param revision
     *            The DateTime the survey was versioned on.
     * @return Survey
     */
    public Survey getSurvey(String guid, DateTime revision);

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
    public GuidCreatedOnVersionHolder versionUpdateAndPublishSurvey(Survey survey, boolean publish);

    /**
     * Get a specific survey instance as identified by the GUID and createdOn keys.
     * 
     * @param keys
     * @return
     */
    public Survey getSurvey(GuidCreatedOnVersionHolder keys);

    /**
     * Create a survey. Consented study participants cannot see or respond to the survey until it is published.
     *
     * @param survey
     *            The survey object Bridge will use to create a survey.
     * @return GuidVersionedOnHolder A holder containing the GUID identifying the survey and the DateTime on which it
     *         was versioned.
     */
    public GuidCreatedOnVersionHolder createSurvey(Survey survey);

    /**
     * Get all versions of a survey (the entire history of edits to that survey), most recent edit first in the list.
     * 
     * @param guid
     * @return
     */
    public ResourceList<Survey> getSurveyAllRevisions(String guid);

    /**
     * Get the most recent version of a survey (the version with the latest createdOn timestamp).
     * 
     * @param guid
     * @return
     */
    public Survey getSurveyMostRecent(String guid);

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
    public Survey getSurveyMostRecentlyPublished(String guid);

    /**
     * Get the most recent version of a survey that has been published (the version with the latest createdOn timestamp
     * that has been published). Note that this does not return "the survey version that was most recently switched to
     * the published state". Publishing versions out of their creation order does not change the version that is
     * returned by this method unless a later version is switched to the published state.
     * 
     * @param identifier
     *            The identifier of the survey
     * @return
     */
    public Survey getSurveyMostRecentlyPublishedByIdentifier(String identifier);

    /**
     * Get the most recent and published version of every survey in a study (each survey with a unique GUID).
     * 
     * @return
     */
    public ResourceList<Survey> getAllSurveysMostRecentlyPublished();

    /**
     * Get the most recent version of every survey in a study (each survey with a unique GUID).
     * 
     * @return
     */
    public ResourceList<Survey> getAllSurveysMostRecent();

    /**
     * Create a new version for the survey identified by a guid string and the DateTime it was versioned on.
     *
     * @param keys
     *            holder object containing a GUID string and DateTime of survey's version.
     * @return
     */
    public GuidCreatedOnVersionHolder versionSurvey(GuidCreatedOnVersionHolder keys);

    /**
     * Update a survey on Bridge.
     *
     * @param survey
     *            The survey object used to update the Survey on Bridge.
     * @return GuidVersionedOnHolder A holder containing the GUID identifying the updated survey and the DateTime the
     *         updated survey was versioned.
     */
    public GuidCreatedOnVersionHolder updateSurvey(Survey survey);

    /**
     * Publish a survey. A published survey is one consented users can see and respond to.
     *
     * @param keys
     *            holder object containing a GUID string identifying the survey and DateTime of survey's version.
     */
    public GuidCreatedOnVersionHolder publishSurvey(GuidCreatedOnVersionHolder keys);

    /**
     * Delete a survey.
     *
     * @param keys
     *            holder object containing a GUID string identifying the survey and DateTime of survey's version.
     */
    public void deleteSurvey(GuidCreatedOnVersionHolder keys);

    // SCHEDULE PLANS

    /**
     * Get all schedule plans.
     *
     * @return List<SchedulePlan>
     */
    public ResourceList<SchedulePlan> getSchedulePlans();

    /**
     * Create a schedule plan.
     *
     * @param plan
     *            The plan object Bridge will use to create a SchedulePlan.
     * @return GuidVersionHolder A holder containing the the guid and version of the created SchedulePlan.
     */
    public GuidVersionHolder createSchedulePlan(SchedulePlan plan);

    /**
     * Get a schedule plan.
     *
     * @param guid
     *            GUID identifying the schedule plan to retrieve.
     * @return SchedulePlan
     */
    public SchedulePlan getSchedulePlan(String guid);

    /**
     * Update a schedule plan.
     *
     * @param plan
     *            The plan object Bridge will use to update it's Schedule Plan.
     * @return GuidVersionHolder Holder containing the guid and version of the updated SchedulePlan.
     */
    public GuidVersionHolder updateSchedulePlan(SchedulePlan plan);

    /**
     * Delete a schedule plan.
     *
     * @param guid
     *            GUID identifying the schedule plan to delete.
     */
    public void deleteSchedulePlan(String guid);

    /**
     * Get the study this researcher is associated to. Researchers can edit studies.
     * 
     * @return study
     */
    public Study getStudy();

    /**
     * Update information about this study.
     * 
     * @param study
     * @return
     */
    public VersionHolder updateStudy(Study study);

    // UPLOAD SCHEMAS

    /**
     * This method creates an upload schema in the current study, using the schema ID of the specified schema, or
     * updates an existing schema if it already exists. This method returns the created schema, which has its revision
     * number properly updated.
     *
     * @param uploadSchema
     *            schema to create or update, must be non-null
     * @return the created or updated schema, will be non-null
     */
    public UploadSchema createOrUpdateUploadSchema(UploadSchema schema);

    /**
     * This method deletes all revisions of the upload schema with the specified schema ID. If there are no schemas with
     * this schema ID, this method throws an EntityNotFoundException.
     *
     * @param schemaId
     *            schema ID of the upload schemas to delete, must be non-null and non-empty
     */
    public void deleteUploadSchemaAllRevisions(String schemaId);

    /**
     * This method deletes an upload schema with the specified schema ID and revision. If the schema doesn't exist, this
     * method throws an EntityNotFoundException.
     *
     * @param schemaId
     *            schema ID of the upload schema to delete
     * @param revision
     *            revision number of the upload schema to delete, must be positive
     */
    public void deleteUploadSchema(String schemaId, int revision);

    /**
     * This method fetches an upload schema from the current study with the specified schema ID. If there is more than
     * one revision of the schema, this fetches the latest revision. If the schema doesn't exist, this method throws an
     * EntityNotFoundException.
     *
     * @param schemaId
     *            ID of the schema to fetch, must be non-null and non-empty
     * @return the fetched schema, will be non-null
     */
    public UploadSchema getUploadSchema(String schemaId);

    /**
     * Fetches all revisions of all upload schemas in the current study.
     *
     * @return a list of upload schemas
     */
    public ResourceList<UploadSchema> getAllUploadSchemasAllRevisions();
}
