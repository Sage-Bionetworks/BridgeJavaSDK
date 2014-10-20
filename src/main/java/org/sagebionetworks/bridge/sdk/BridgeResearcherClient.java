package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.GuidVersionedOnHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BridgeResearcherClient extends BaseApiCaller {

    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.setSerializationInclusion(Include.NON_NULL);
    }

    private final SurveyApiCaller surveyApi;
    private final SchedulePlanApiCaller schedulePlanApi;

    private BridgeResearcherClient(ClientProvider provider) {
        super(provider);
        this.surveyApi = SurveyApiCaller.valueOf(provider);
        this.schedulePlanApi = SchedulePlanApiCaller.valueOf(provider);
    }

    static BridgeResearcherClient valueOf(ClientProvider provider) {
        return new BridgeResearcherClient(provider);
    }

    public Survey getSurvey(String guid, DateTime versionedOn) {
        Preconditions.checkArgument(provider.isSignedIn(), "Not signed in");
        Preconditions.checkNotEmpty(guid, "Guid is null or blank");
        Preconditions.checkNotNull(versionedOn, "VersionedOn is null");
        return surveyApi.getSurvey(guid, versionedOn);
    }
    public GuidVersionedOnHolder createSurvey(Survey survey) {
        Preconditions.checkArgument(provider.isSignedIn(), "Not signed in");
        Preconditions.checkNotNull(survey, "Survey object is null");
        return surveyApi.createNewSurvey(survey);
    }
    public List<Survey> getAllVersionsOfAllSurveys() {
        Preconditions.checkArgument(provider.isSignedIn(), "Not signed in");
        return surveyApi.getAllVersionsOfAllSurveys();
    }
    public List<Survey> getPublishedVersionsOfAllSurveys() {
        Preconditions.checkArgument(provider.isSignedIn(), "Not signed in");
        return surveyApi.getPublishedVersionsOfAllSurveys();
    }
    public List<Survey> getRecentVersionsOfAllSurveys() {
        Preconditions.checkArgument(provider.isSignedIn(), "Not signed in");
        return surveyApi.getRecentVersionsOfAllSurveys();
    }
    public List<Survey> getAllVersionsForSurvey(String guid) {
        Preconditions.checkArgument(provider.isSignedIn(), "Not signed in");
        Preconditions.checkNotEmpty(guid, "Guid is null or blank");
        return surveyApi.getAllVersionsOfAllSurveys();
    }
    public GuidVersionedOnHolder createNewSurvey(Survey survey) {
        Preconditions.checkArgument(provider.isSignedIn(), "Not signed in");
        Preconditions.checkNotNull(survey, "Survey object is null");
        return surveyApi.createNewSurvey(survey);
    }
    public GuidVersionedOnHolder versionSurvey(String guid, DateTime versionedOn) {
        Preconditions.checkArgument(provider.isSignedIn(), "Not signed in");
        Preconditions.checkNotEmpty(guid, "Guid is null or blank");
        Preconditions.checkNotNull(versionedOn, "VersionedOn is null");
        return surveyApi.versionSurvey(guid, versionedOn);
    }
    public GuidVersionedOnHolder updateSurvey(Survey survey) {
        Preconditions.checkArgument(provider.isSignedIn(), "Not signed in");
        Preconditions.checkNotNull(survey, "Survey object is null");
        return surveyApi.updateSurvey(survey);
    }
    public void publishSurvey(String guid, DateTime versionedOn) {
        Preconditions.checkArgument(provider.isSignedIn(), "Not signed in");
        Preconditions.checkNotEmpty(guid, "Guid is null or blank");
        Preconditions.checkNotNull(versionedOn, "VersionedOn is null");
        surveyApi.publishSurvey(guid, versionedOn);
    }
    public void closeSurvey(String guid, DateTime versionedOn) {
        Preconditions.checkArgument(provider.isSignedIn(), "Not signed in");
        Preconditions.checkNotEmpty(guid, "Guid is null or blank");
        Preconditions.checkNotNull(versionedOn, "VersionedOn is null");
        surveyApi.closeSurvey(guid, versionedOn);
    }
    
    public List<SchedulePlan> getSchedulePlans() {
        return schedulePlanApi.getSchedulePlans();
    }

    public GuidVersionHolder createSchedulePlan(SchedulePlan plan) {
        Preconditions.checkNotNull(plan, "Plan object is null");
        return schedulePlanApi.createSchedulePlan(plan);
    }

    public SchedulePlan getSchedulePlan(String guid) {
        Preconditions.checkNotEmpty(guid, "Guid is null or blank");
        return schedulePlanApi.getSchedulePlan(guid);
    }

    public GuidVersionHolder updateSchedulePlan(SchedulePlan plan) {
        Preconditions.checkNotNull(plan, "Plan object is null");
        return schedulePlanApi.updateSchedulePlan(plan);
    }

    public void deleteSchedulePlan(String guid) {
        Preconditions.checkNotEmpty(guid, "Guid is null or blank");
        schedulePlanApi.deleteSchedulePlan(guid);
    }

}
