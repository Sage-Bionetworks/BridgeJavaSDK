package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.ConsentDocument;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionedOnHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

public interface ResearcherClient {


    // STUDY CONSENTS
    /**
     * Get all consent documents associated with the study currently signed into.
     *
     * @return List<StudyConsent>
     */
    public List<ConsentDocument> getAllConsentDocuments();

    /**
     * Get the most recently activated consent document. Can have more than one concurrently active consent document,
     * and this method retrieves only the most recent.
     *
     * @return StudyConsent
     */
    public ConsentDocument getMostRecentlyActivatedConsentDocument();

    /**
     * Get a consent document that was created at a DateTime.
     *
     * @param createdOn
     *            The DateTime the consent document was created on (this DateTime identifies the consent document).
     * @return StudyConsent
     */
    public ConsentDocument getConsentDocument(DateTime createdOn);

    /**
     * Add a consent document to the study.
     *
     * @param consent
     *            The consent document to add.
     */
    public void createConsentDocument(ConsentDocument consent);

    /**
     * Activate a consent document created at a DateTime. Can have more than one concurrently active consent document.
     *
     * @param createdOn
     *            DateTime consent document was created. This acts as an identifier for the consent document.
     */
    public void activateConsentDocument(DateTime createdOn);

    // SURVEYS
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

    public Survey getSurvey(GuidVersionedOnHolder keys);

    /**
     * Create a survey. Consented study participants cannot see or respond to the survey until it is published.
     *
     * @param survey
     *            The survey object Bridge will use to create a survey.
     * @return GuidVersionedOnHolder A holder containing the GUID identifying the survey and the DateTime on which it
     *         was versioned.
     */
    public GuidVersionedOnHolder createSurvey(Survey survey);

    /**
     * Get all versions of all surveys associated with the Study currently signed in to.
     *
     * @return List<Survey>
     */
    public List<Survey> getAllVersionsOfAllSurveys();

    /**
     * Get the published versions of all surveys associated with Study currently signed in to.
     *
     * @return List<Survey>
     */
    public List<Survey> getPublishedVersionsOfAllSurveys();

    /**
     * Get the youngest (newest, most recent, etc) version of every survey in the study.
     *
     * @return List<Survey>
     */
    public List<Survey> getRecentVersionsOfAllSurveys();

    /**
     * Get every version associated with a particular Survey.
     *
     * @param guid
     *            The GUID identifying the survey to retrieve.
     * @return List<Survey>
     */
    public List<Survey> getAllVersionsOfASurvey(String guid);

    /**
     * Create a new version for the survey identified by a guid string and the DateTime it was versioned on.
     *
     * @param keys
     *            holder object containing a GUID string and DateTime of survey's version.
     * @return
     */
    public GuidVersionedOnHolder versionSurvey(GuidVersionedOnHolder keys);

    /**
     * Update a survey on Bridge.
     *
     * @param survey
     *            The survey object used to update the Survey on Bridge.
     * @return GuidVersionedOnHolder A holder containing the GUID identifying the updated survey and the DateTime the
     *         updated survey was versioned.
     */
    public GuidVersionedOnHolder updateSurvey(Survey survey);

    /**
     * Publish a survey. A published survey is one consented users can see and respond to.
     *
     * @param keys
     *            holder object containing a GUID string identifying the survey and DateTime of survey's version.
     */
    public void publishSurvey(GuidVersionedOnHolder keys);

    /**
     * Close a survey. A closed survey is one consented users cannot see.
     *
     * @param keys
     *            holder object containing a GUID string identifying the survey and DateTime of survey's version.
     */
    public void closeSurvey(GuidVersionedOnHolder keys);

    /**
     * Delete a survey.
     *
     * @param keys
     *            holder object containing a GUID string identifying the survey and DateTime of survey's version.
     */
    public void deleteSurvey(GuidVersionedOnHolder keys);

    // SCHEDULE PLANS

    /**
     * Get all schedule plans.
     *
     * @return List<SchedulePlan>
     */
    public List<SchedulePlan> getSchedulePlans();

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

}
