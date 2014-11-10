package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.SimpleGuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.SimpleGuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.studies.StudyConsent;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

class BridgeResearcherClient implements ResearcherClient {

    private final Session session;
    private final SurveyApiCaller surveyApi;
    private final SchedulePlanApiCaller schedulePlanApi;
    private final StudyConsentApiCaller studyConsentApi;

    private BridgeResearcherClient(Session session) {
        this.session = session;
        this.surveyApi = SurveyApiCaller.valueOf(session);
        this.studyConsentApi = StudyConsentApiCaller.valueOf(session);
        this.schedulePlanApi = SchedulePlanApiCaller.valueOf(session);
    }

    static BridgeResearcherClient valueOf(Session session) {
        return new BridgeResearcherClient(session);
    }
    
    @Override
    public ResourceList<StudyConsent> getAllConsentDocuments() {
        session.checkSignedIn();

        return studyConsentApi.getAllStudyConsents();
    }
    @Override
    public StudyConsent getMostRecentlyActivatedConsentDocument() {
        session.checkSignedIn();

        return studyConsentApi.getActiveStudyConsent();
    }
    @Override
    public StudyConsent getConsentDocument(DateTime createdOn) {
        session.checkSignedIn();
        checkNotNull(createdOn, Bridge.CANNOT_BE_NULL, "createdOn");

        return studyConsentApi.getStudyConsent(createdOn);
    }
    @Override
    public void createConsentDocument(StudyConsent consent) {
        session.checkSignedIn();
        checkNotNull(consent, Bridge.CANNOT_BE_NULL, "consent");

        studyConsentApi.createStudyConsent(consent);
    }
    @Override
    public void activateConsentDocument(DateTime createdOn) {
        session.checkSignedIn();
        checkNotNull(createdOn, Bridge.CANNOT_BE_NULL, "createdOn");

        studyConsentApi.setActiveStudyConsent(createdOn);
    }
    
    @Override
    public Survey getSurvey(String guid, DateTime createdOn) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(createdOn, Bridge.CANNOT_BE_NULL, "createdOn");
        return surveyApi.getSurveyForResearcher(guid, createdOn);
    }
    @Override
    public Survey getSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/createdOn keys");
        return surveyApi.getSurveyForResearcher(keys.getGuid(), keys.getCreatedOn());
    }
    @Override
    public ResourceList<Survey> getAllVersionsOfAllSurveys() {
        session.checkSignedIn();
        return surveyApi.getAllVersionsOfAllSurveys();
    }
    @Override
    public ResourceList<Survey> getPublishedVersionsOfAllSurveys() {
        session.checkSignedIn();
        return surveyApi.getPublishedVersionsOfAllSurveys();
    }
    @Override
    public ResourceList<Survey> getRecentVersionsOfAllSurveys() {
        session.checkSignedIn();
        return surveyApi.getRecentVersionsOfAllSurveys();
    }
    @Override
    public ResourceList<Survey> getAllVersionsOfASurvey(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        return surveyApi.getAllVersionsOfASurvey(guid);
    }
    @Override
    public SimpleGuidCreatedOnVersionHolder createSurvey(Survey survey) {
        session.checkSignedIn();
        checkNotNull(survey, Bridge.CANNOT_BE_NULL,"Survey object");
        return surveyApi.createSurvey(survey);
    }
    @Override
    public SimpleGuidCreatedOnVersionHolder versionSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/createdOn keys");
        checkArgument(isNotBlank(keys.getGuid()), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(keys.getCreatedOn(), Bridge.CANNOT_BE_NULL, "createdOn");
        return surveyApi.versionSurvey(keys.getGuid(), keys.getCreatedOn());
    }
    @Override
    public SimpleGuidCreatedOnVersionHolder updateSurvey(Survey survey) {
        session.checkSignedIn();
        checkNotNull(survey, Bridge.CANNOT_BE_NULL,"Survey object");
        return surveyApi.updateSurvey(survey);
    }
    @Override
    public void publishSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/createdOn keys");
        checkArgument(isNotBlank(keys.getGuid()), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(keys.getCreatedOn(), Bridge.CANNOT_BE_NULL, "createdOn");
        surveyApi.publishSurvey(keys.getGuid(), keys.getCreatedOn());
    }
    @Override
    public void closeSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/createdOn keys");
        checkArgument(isNotBlank(keys.getGuid()), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(keys.getCreatedOn(), Bridge.CANNOT_BE_NULL, "createdOn");
        surveyApi.closeSurvey(keys.getGuid(), keys.getCreatedOn());
    }
    @Override
    public void deleteSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/createdOn keys");
        checkArgument(isNotBlank(keys.getGuid()), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(keys.getCreatedOn(), Bridge.CANNOT_BE_NULL, "createdOn");
        surveyApi.deleteSurvey(keys.getGuid(), keys.getCreatedOn());
    }
    @Override
    public ResourceList<SchedulePlan> getSchedulePlans() {
        session.checkSignedIn();
        return schedulePlanApi.getSchedulePlans();
    }
    @Override
    public SimpleGuidVersionHolder createSchedulePlan(SchedulePlan plan) {
        session.checkSignedIn();
        checkNotNull(plan, Bridge.CANNOT_BE_NULL, "SchedulePlan");
        return schedulePlanApi.createSchedulePlan(plan);
    }
    @Override
    public SchedulePlan getSchedulePlan(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        return schedulePlanApi.getSchedulePlan(guid);
    }
    @Override
    public SimpleGuidVersionHolder updateSchedulePlan(SchedulePlan plan) {
        session.checkSignedIn();
        checkNotNull(plan, Bridge.CANNOT_BE_NULL, "SchedulePlan");
        return schedulePlanApi.updateSchedulePlan(plan);
    }
    @Override
    public void deleteSchedulePlan(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        schedulePlanApi.deleteSchedulePlan(guid);
    }
}
