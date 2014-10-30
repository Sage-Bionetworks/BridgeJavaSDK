package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.GuidVersionedOnHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

public interface ResearcherClient {

    public Survey getSurvey(String guid, DateTime versionedOn);

    public GuidVersionedOnHolder createSurvey(Survey survey);

    public List<Survey> getAllVersionsOfAllSurveys();

    public List<Survey> getPublishedVersionsOfAllSurveys();

    public List<Survey> getRecentVersionsOfAllSurveys();

    public List<Survey> getAllVersionsOfASurvey(String guid);

    public GuidVersionedOnHolder versionSurvey(String guid, DateTime versionedOn);

    public GuidVersionedOnHolder updateSurvey(Survey survey);

    public void publishSurvey(String guid, DateTime versionedOn);

    public void closeSurvey(String guid, DateTime versionedOn);
    
    public void deleteSurvey(String guid, DateTime versionedOn);

    public List<SchedulePlan> getSchedulePlans();

    public GuidVersionHolder createSchedulePlan(SchedulePlan plan);

    public SchedulePlan getSchedulePlan(String guid);

    public GuidVersionHolder updateSchedulePlan(SchedulePlan plan);

    public void deleteSchedulePlan(String guid);

}
