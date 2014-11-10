package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionedOnHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleGuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleGuidVersionedOnHolder;
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
    public List<StudyConsent> getAllStudyConsents() {
        session.checkSignedIn();

        return studyConsentApi.getAllStudyConsents();
    }
    @Override
    public StudyConsent getMostRecentlyActivatedStudyConsent() {
        session.checkSignedIn();

        return studyConsentApi.getActiveStudyConsent();
    }
    @Override
    public StudyConsent getStudyConsent(DateTime createdOn) {
        session.checkSignedIn();
        checkNotNull(createdOn, Bridge.CANNOT_BE_NULL, "createdOn");

        return studyConsentApi.getStudyConsent(createdOn);
    }
    @Override
    public void createStudyConsent(StudyConsent consent) {
        session.checkSignedIn();
        checkNotNull(consent, Bridge.CANNOT_BE_NULL, "consent");

        studyConsentApi.createStudyConsent(consent);
    }
    @Override
    public void activateStudyConsent(DateTime createdOn) {
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
    public Survey getSurvey(GuidVersionedOnHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/versionedOn keys");
        return surveyApi.getSurveyForResearcher(keys.getGuid(), keys.getVersionedOn());
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
    public SimpleGuidVersionedOnHolder createSurvey(Survey survey) {
        session.checkSignedIn();
        checkNotNull(survey, Bridge.CANNOT_BE_NULL,"Survey object");
        return surveyApi.createSurvey(survey);
    }
    @Override
    public SimpleGuidVersionedOnHolder versionSurvey(GuidVersionedOnHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/versionedOn keys");
        checkArgument(isNotBlank(keys.getGuid()), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(keys.getVersionedOn(), Bridge.CANNOT_BE_NULL, "versionedOn");
        return surveyApi.versionSurvey(keys.getGuid(), keys.getVersionedOn());
    }
    @Override
    public SimpleGuidVersionedOnHolder updateSurvey(Survey survey) {
        session.checkSignedIn();
        checkNotNull(survey, Bridge.CANNOT_BE_NULL,"Survey object");
        return surveyApi.updateSurvey(survey);
    }
    @Override
    public void publishSurvey(GuidVersionedOnHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/versionedOn keys");
        checkArgument(isNotBlank(keys.getGuid()), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(keys.getVersionedOn(), Bridge.CANNOT_BE_NULL, "versionedOn");
        surveyApi.publishSurvey(keys.getGuid(), keys.getVersionedOn());
    }
    @Override
    public void closeSurvey(GuidVersionedOnHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/versionedOn keys");
        checkArgument(isNotBlank(keys.getGuid()), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(keys.getVersionedOn(), Bridge.CANNOT_BE_NULL, "versionedOn");
        surveyApi.closeSurvey(keys.getGuid(), keys.getVersionedOn());
    }
    @Override
    public void deleteSurvey(GuidVersionedOnHolder keys) {
        session.checkSignedIn();
        checkNotNull(keys, Bridge.CANNOT_BE_NULL, "guid/versionedOn keys");
        checkArgument(isNotBlank(keys.getGuid()), Bridge.CANNOT_BE_BLANK, "guid");
        checkNotNull(keys.getVersionedOn(), Bridge.CANNOT_BE_NULL, "versionedOn");
        surveyApi.deleteSurvey(keys.getGuid(), keys.getVersionedOn());
    }
    @Override
    public List<SchedulePlan> getSchedulePlans() {
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
