package org.sagebionetworks.bridge.sdk;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.studies.Study;
import org.sagebionetworks.bridge.sdk.models.studies.StudyConsent;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

public interface ResearcherClient {

    // STUDY CONSENTS
    /**
     * Get all consent documents associated with the study currently signed into.
     *
     * @return List<StudyConsent>
     */
    public ResourceList<StudyConsent> getAllStudyConsents();

    /**
     * Get the most recently activated consent document. Can have more than one concurrently active consent document,
     * and this method retrieves only the most recent.
     *
     * @return StudyConsent
     */
    public StudyConsent getMostRecentlyActivatedStudyConsent();

    /**
     * Get a consent document that was created at a DateTime.
     *
     * @param createdOn
     *            The DateTime the consent document was created on (this DateTime identifies the consent document).
     * @return StudyConsent
     */
    public StudyConsent getStudyConsent(DateTime createdOn);

    /**
     * Add a consent document to the study.
     *
     * @param consent
     *            The consent document to add.
     */
    public void createStudyConsent(StudyConsent consent);

    /**
     * Activate a consent document created at a DateTime. Can have more than one concurrently active consent document.
     *
     * @param createdOn
     *            DateTime consent document was created. This acts as an identifier for the consent document.
     */
    public void activateStudyConsent(DateTime createdOn);

    /**
     * Get the survey versionedOn a particular DateTime and identified by the surveyGuid.
     *
     * @param guid
     *            GUID identifying the survey.
     * @param versionedOn
     *            The DateTime the survey was versioned on.
     * @return Survey
     */
    public Survey getSurvey(String guid, DateTime versionedOn);

    /**
     * Take the supplied instance of a survey, make it the most recent version of the survey, save it, and 
     * then immediately publish it if indicated. This combines several operations for the common case of making 
     * trivial fixes to a survey, 
     * 
     * @param survey
     *      survey to update (cannot be the initial creation of a survey) 
     * @param publish
     *      should this new version be published immediately after being saved?
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
     * @param guid
     * @return
     */
    public ResourceList<Survey> getSurveyAllVersions(String guid);
    
    /**
     * Get the most recent version of a survey (the version with the latest createdOn timestamp).
     * @param guid
     * @return
     */
    public Survey getSurveyMostRecentVersion(String guid);
    
    /**
     * Get the most recent version of a survey that has been published (the version with the latest createdOn timestamp
     * that has been published). Note that this does not return "the survey version that was most recently switched 
     * to the published state". Publishing versions out of their creation order does not change the version that is 
     * returned by this method unless a later version is switched to the published state.
     * 
     * @param guid
     *      The guid of the survey
     * @return
     */
    public Survey getSurveyMostRecentlyPublishedVersion(String guid);
    
    /**
     * Get the most recent version of a survey that has been published (the version with the latest createdOn timestamp
     * that has been published). Note that this does not return "the survey version that was most recently switched 
     * to the published state". Publishing versions out of their creation order does not change the version that is 
     * returned by this method unless a later version is switched to the published state.
     * @param identifier
     *      The identifier of the survey
     * @return
     */
    public Survey getSurveyMostRecentlyPublishedVersionByIdentifier(String identifier);
    
    /**
     * Get the most recent and published version of every survey in a study (each survey with a unique GUID).
     * @return
     */
    public ResourceList<Survey> getAllSurveysMostRecentlyPublishedVersion();
    
    /**
     * Get the most recent version of every survey in a study (each survey with a unique GUID).
     * @return
     */
    public ResourceList<Survey> getAllSurveysMostRecentVersion();

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
     * Close a survey. A closed survey is one consented users cannot see.
     *
     * @param keys
     *            holder object containing a GUID string identifying the survey and DateTime of survey's version.
     */
    public void closeSurvey(GuidCreatedOnVersionHolder keys);

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
     * @return study
     */
    public Study getStudy();
    
    /**
     * Update information about this study. 
     * @param study
     * @return 
     */
    public VersionHolder updateStudy(Study study);

}
