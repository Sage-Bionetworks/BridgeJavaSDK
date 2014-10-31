package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.GuidVersionedOnHolder;
import org.sagebionetworks.bridge.sdk.models.StudyConsent;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
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
    public List<StudyConsent> getAllConsentDocuments() {
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
    public Survey getSurvey(String guid, DateTime versionedOn) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(versionedOn, Bridge.CANNOT_BE_NULL, "versionedOn");
        return surveyApi.getSurveyForResearcher(guid, versionedOn);
    }
    @Override
    public List<Survey> getAllVersionsOfAllSurveys() {
        session.checkSignedIn();
        return surveyApi.getAllVersionsOfAllSurveys();
    }
    @Override
    public List<Survey> getPublishedVersionsOfAllSurveys() {
        session.checkSignedIn();
        return surveyApi.getPublishedVersionsOfAllSurveys();
    }
    @Override
    public List<Survey> getRecentVersionsOfAllSurveys() {
        session.checkSignedIn();
        return surveyApi.getRecentVersionsOfAllSurveys();
    }
    @Override
    public List<Survey> getAllVersionsOfASurvey(String guid) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        return surveyApi.getAllVersionsOfASurvey(guid);
    }
    @Override
    public GuidVersionedOnHolder createSurvey(Survey survey) {
        session.checkSignedIn();
        checkNotNull(survey, Bridge.CANNOT_BE_NULL,"Survey object");
        return surveyApi.createSurvey(survey);
    }
    @Override
    public GuidVersionedOnHolder versionSurvey(String guid, DateTime versionedOn) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(versionedOn, Bridge.CANNOT_BE_NULL, "versionedOn");
        return surveyApi.versionSurvey(guid, versionedOn);
    }
    @Override
    public GuidVersionedOnHolder updateSurvey(Survey survey) {
        session.checkSignedIn();
        checkNotNull(survey, Bridge.CANNOT_BE_NULL,"Survey object");
        return surveyApi.updateSurvey(survey);
    }
    @Override
    public void publishSurvey(String guid, DateTime versionedOn) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(versionedOn, Bridge.CANNOT_BE_NULL, "versionedOn");
        surveyApi.publishSurvey(guid, versionedOn);
    }
    @Override
    public void closeSurvey(String guid, DateTime versionedOn) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(versionedOn, Bridge.CANNOT_BE_NULL, "versionedOn");
        surveyApi.closeSurvey(guid, versionedOn);
    }
    @Override
    public void deleteSurvey(String guid, DateTime versionedOn) {
        session.checkSignedIn();
        checkArgument(isNotBlank(guid), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(versionedOn, Bridge.CANNOT_BE_NULL, "versionedOn");
        surveyApi.deleteSurvey(guid, versionedOn);
    }
    @Override
    public List<SchedulePlan> getSchedulePlans() {
        session.checkSignedIn();
        return schedulePlanApi.getSchedulePlans();
    }
    @Override
    public GuidVersionHolder createSchedulePlan(SchedulePlan plan) {
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
    public GuidVersionHolder updateSchedulePlan(SchedulePlan plan) {
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
