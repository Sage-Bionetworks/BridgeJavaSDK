package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.joda.time.DateTime;

import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleGuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

import com.fasterxml.jackson.core.type.TypeReference;

public class SurveyClient extends BaseApiCaller {
    
    private final TypeReference<ResourceList<Survey>> SURVEY_LIST_TYPE = new TypeReference<ResourceList<Survey>>() {};
    
    SurveyClient(BridgeSession session) {
        super(session);
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
        return get(config.getSurveyAllRevisionsApi(guid), SURVEY_LIST_TYPE);
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
        return get(config.getPublishedSurveysApi(), SURVEY_LIST_TYPE);
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
     * Get the most recent version of every survey in a study (each survey with a unique GUID).
     * 
     * @return
     */
    public ResourceList<Survey> getAllSurveysMostRecent() {
        session.checkSignedIn();
        return get(config.getRecentSurveysApi(), SURVEY_LIST_TYPE);
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
     * Delete a survey permanently. This delete will occur whether or not the survey version is in use 
     * or not; developers should use the deleteSurvey() method.
     * @param keys
     */
    public void deleteSurveyPermanently(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        delete(config.getDeleteSurveyPermanentlyApi(keys.getGuid(), keys.getCreatedOn()));
    }
}
