package org.sagebionetworks.bridge.sdk;

import static org.sagebionetworks.bridge.sdk.Preconditions.checkNotEmpty;
import static org.sagebionetworks.bridge.sdk.Preconditions.checkNotNull;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.GuidVersionedOnHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

class BridgeResearcherClient implements ResearcherClient {

    private final Session session;
    private final SurveyApiCaller surveyApi;
    private final SchedulePlanApiCaller schedulePlanApi;

    private BridgeResearcherClient(Session session) {
        this.session = session;
        this.surveyApi = SurveyApiCaller.valueOf(session);
        this.schedulePlanApi = SchedulePlanApiCaller.valueOf(session);
    }

    static BridgeResearcherClient valueOf(Session session) {
        return new BridgeResearcherClient(session);
    }
    @Override
    public Survey getSurvey(String guid, DateTime versionedOn) {
        session.checkSignedIn();
        checkNotEmpty(guid, "Guid is null or blank");
        checkNotNull(versionedOn, "VersionedOn is null");
        return surveyApi.getSurvey(guid, versionedOn);
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
    public List<Survey> getAllVersionsForSurvey(String guid) {
        session.checkSignedIn();
        checkNotEmpty(guid, "Guid is null or blank");
        return surveyApi.getAllVersionsOfAllSurveys();
    }
    @Override
    public GuidVersionedOnHolder createSurvey(Survey survey) {
        session.checkSignedIn();
        checkNotNull(survey, "Survey object is null");
        return surveyApi.createSurvey(survey);
    }
    @Override
    public GuidVersionedOnHolder versionSurvey(String guid, DateTime versionedOn) {
        session.checkSignedIn();
        checkNotEmpty(guid, "Guid is null or blank");
        checkNotNull(versionedOn, "VersionedOn is null");
        return surveyApi.createNewVersionForSurvey(guid, versionedOn);
    }
    @Override
    public GuidVersionedOnHolder updateSurvey(Survey survey) {
        session.checkSignedIn();
        checkNotNull(survey, "Survey object is null");
        return surveyApi.updateSurvey(survey);
    }
    @Override
    public void publishSurvey(String guid, DateTime versionedOn) {
        session.checkSignedIn();
        checkNotEmpty(guid, "Guid is null or blank");
        checkNotNull(versionedOn, "VersionedOn is null");
        surveyApi.publishSurvey(guid, versionedOn);
    }
    @Override
    public void closeSurvey(String guid, DateTime versionedOn) {
        session.checkSignedIn();
        checkNotEmpty(guid, "Guid is null or blank");
        checkNotNull(versionedOn, "VersionedOn is null");
        surveyApi.closeSurvey(guid, versionedOn);
    }
    @Override
    public List<SchedulePlan> getSchedulePlans() {
        session.checkSignedIn();
        return schedulePlanApi.getSchedulePlans();
    }
    @Override
    public GuidVersionHolder createSchedulePlan(SchedulePlan plan) {
        session.checkSignedIn();
        checkNotNull(plan, "Plan object is null");
        return schedulePlanApi.createSchedulePlan(plan);
    }
    @Override
    public SchedulePlan getSchedulePlan(String guid) {
        session.checkSignedIn();
        checkNotEmpty(guid, "Guid is null or blank");
        return schedulePlanApi.getSchedulePlan(guid);
    }
    @Override
    public GuidVersionHolder updateSchedulePlan(SchedulePlan plan) {
        session.checkSignedIn();
        checkNotNull(plan, "Plan object is null");
        return schedulePlanApi.updateSchedulePlan(plan);
    }
    @Override
    public void deleteSchedulePlan(String guid) {
        session.checkSignedIn();
        checkNotEmpty(guid, "Guid is null or blank");
        schedulePlanApi.deleteSchedulePlan(guid);
    }
}
