package org.sagebionetworks.bridge.sdk;

import org.joda.time.DateTime;

import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

/** Client for worker APIs, used by Bridge-EX and other worker processes. */
public interface WorkerClient {
    /**
     * Gets the most recently published version of all surveys for a given study.
     *
     * @param studyId
     *         study to get surveys for
     * @return list of surveys
     */
    ResourceList<Survey> getAllSurveysMostRecentlyPublished(String studyId);

    /**
     * Gets the specified survey by guid and createdOn
     *
     * @param guid
     *         survey guid
     * @param createdOn
     *         created-on timestamp for the survey
     * @return the specified survey
     */
    Survey getSurvey(String guid, DateTime createdOn);

    /**
     * Gets the specified survey by key holder.
     *
     * @param keys
     *         key holder containing the survey guid and createdOn
     * @return the specified survey
     */
    Survey getSurvey(GuidCreatedOnVersionHolder keys);
}
