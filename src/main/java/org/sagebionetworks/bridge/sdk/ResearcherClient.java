package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionedOnHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

public interface ResearcherClient {

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
     * @param guid
     *            GUID string identifying the survey.
     * @param versionedOn
     *            The DateTime of the survey's version.
     * @return
     */
    public GuidVersionedOnHolder versionSurvey(String guid, DateTime versionedOn);

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
     * @param guid
     *            GUID string identifying the survey.
     * @param versionedOn
     *            DateTime of the survey's version.
     */
    public void publishSurvey(String guid, DateTime versionedOn);

    /**
     * Close a survey. A closed survey is one consented users cannot see.
     *
     * @param guid
     * @param versionedOn
     */
    public void closeSurvey(String guid, DateTime versionedOn);

    /**
     * Delete a survey.
     *
     * @param guid
     *            GUID string identifying the survey.
     * @param versionedOn
     *            The DateTime of the survey's version.
     */
    public void deleteSurvey(String guid, DateTime versionedOn);

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
