package org.sagebionetworks.bridge.sdk;

import java.util.Map;

import org.sagebionetworks.bridge.sdk.models.accounts.StudyParticipant;
import org.sagebionetworks.bridge.sdk.models.subpopulations.ConsentStatus;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;

public interface Session {

    void checkSignedIn();

    String getSessionToken();
    
    StudyParticipant getStudyParticipant();

    boolean isConsented();
    
    boolean isSignedIn();
    
    void signOut();
    
    Map<SubpopulationGuid,ConsentStatus> getConsentStatuses();
    
    DeveloperClient getDeveloperClient();

    ResearcherClient getResearcherClient();

    WorkerClient getWorkerClient();
    
    UserClient getUserClient();
    
    AdminClient getAdminClient();
    
    ExternalIdentifiersClient getExternalIdentifiersClient();
    
    ParticipantClient getParticipantClient();
    
    SchedulePlanClient getSchedulePlanClient();
    
    StudyClient getStudyClient();
    
    StudyConsentClient getStudyConsentClient();
    
    SubpopulationClient getSubpopulationClient();
    
    SurveyClient getSurveyClient();
    
    UploadSchemaClient getUploadSchemaClient();
    
    ReportClient getReportClient();
}
