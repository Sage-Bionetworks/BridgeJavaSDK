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
    
    UserClient getUserClient();
    
    DeveloperClient getDeveloperClient();
    
    ResearcherClient getResearcherClient();
    
    AdminClient getAdminClient();

    /** Gets the client used for worker APIs. */
    WorkerClient getWorkerClient();
}
