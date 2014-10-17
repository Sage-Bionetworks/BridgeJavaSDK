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

    public Survey getSurvey(String guid, DateTime timestamp) {
        Preconditions.checkNotEmpty(guid, "Guid is null or blank");
        Preconditions.checkNotNull(timestamp, "Timestamp is null");
        return surveyApi.getSurvey(guid, timestamp);
    }

    public GuidVersionedOnHolder createSurvey(Survey survey) {
        Preconditions.checkNotNull(survey, "Survey object is null");
        return surveyApi.createSurvey(survey);
    }

    public void publishSurvey(String guid, DateTime timestamp) {
        Preconditions.checkNotEmpty(guid, "Guid is null or blank");
        Preconditions.checkNotNull(timestamp, "Timestamp is null");
        surveyApi.publishSurvey(guid, timestamp);
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
