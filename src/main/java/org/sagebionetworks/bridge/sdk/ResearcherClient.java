package org.sagebionetworks.bridge.sdk;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.schedules.SchedulePlan;
import org.sagebionetworks.bridge.sdk.models.studies.StudyConsent;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

public interface ResearcherClient {

    // STUDY CONSENTS
    
    public ResourceList<StudyConsent> getAllConsentDocuments();

    public StudyConsent getMostRecentlyActivatedConsentDocument();

    public StudyConsent getConsentDocument(DateTime createdOn);

    public void createConsentDocument(StudyConsent consent);

    public void activateConsentDocument(DateTime createdOn);
    
    // SURVEYS
    
    public Survey getSurvey(String guid, DateTime createdOn);
    
    public Survey getSurvey(GuidCreatedOnVersionHolder keys);

    public GuidCreatedOnVersionHolder createSurvey(Survey survey);

    public ResourceList<Survey> getAllVersionsOfAllSurveys();

    public ResourceList<Survey> getPublishedVersionsOfAllSurveys();

    public ResourceList<Survey> getRecentVersionsOfAllSurveys();

    public ResourceList<Survey> getAllVersionsOfASurvey(String guid);

    public GuidCreatedOnVersionHolder versionSurvey(GuidCreatedOnVersionHolder keys);

    public GuidCreatedOnVersionHolder updateSurvey(Survey survey);

    public void publishSurvey(GuidCreatedOnVersionHolder keys);

    public void closeSurvey(GuidCreatedOnVersionHolder keys);
    
    public void deleteSurvey(GuidCreatedOnVersionHolder keys);
    
    // SCHEDULE PLANS

    public ResourceList<SchedulePlan> getSchedulePlans();

    public GuidVersionHolder createSchedulePlan(SchedulePlan plan);

    public SchedulePlan getSchedulePlan(String guid);

    public GuidVersionHolder updateSchedulePlan(SchedulePlan plan);

    public void deleteSchedulePlan(String guid);

}
