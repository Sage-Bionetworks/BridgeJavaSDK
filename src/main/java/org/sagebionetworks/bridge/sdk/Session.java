package org.sagebionetworks.bridge.sdk;

import java.util.Map;

import org.sagebionetworks.bridge.sdk.models.subpopulations.ConsentStatus;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;
import org.sagebionetworks.bridge.sdk.models.users.DataGroups;
import org.sagebionetworks.bridge.sdk.models.users.SharingScope;

public interface Session {

    void checkSignedIn();

    String getSessionToken();
    
    SharingScope getSharingScope();
    
    String getId();

    boolean isConsented();
    
    boolean isSignedIn();
    
    void signOut();
    
    DataGroups getDataGroups();
    
    Map<SubpopulationGuid,ConsentStatus> getConsentStatuses();
    
    UserClient getUserClient();
    
    DeveloperClient getDeveloperClient();
    
    ResearcherClient getResearcherClient();
    
    AdminClient getAdminClient();

    /** Gets the client used for worker APIs. */
    WorkerClient getWorkerClient();
}
