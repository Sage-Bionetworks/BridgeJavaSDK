package org.sagebionetworks.bridge.sdk;

import java.util.Map;

import org.sagebionetworks.bridge.sdk.models.subpopulations.ConsentStatus;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;
import org.sagebionetworks.bridge.sdk.models.users.DataGroups;
import org.sagebionetworks.bridge.sdk.models.users.SharingScope;

public interface Session {

    public void checkSignedIn();

    public String getSessionToken();
    
    public SharingScope getSharingScope();

    public boolean isConsented();
    
    public boolean isSignedIn();
    
    public void signOut();
    
    public DataGroups getDataGroups();
    
    public Map<SubpopulationGuid,ConsentStatus> getConsentStatuses();
    
    public UserClient getUserClient();
    
    public DeveloperClient getDeveloperClient();
    
    public ResearcherClient getResearcherClient();
    
    public AdminClient getAdminClient();

}
