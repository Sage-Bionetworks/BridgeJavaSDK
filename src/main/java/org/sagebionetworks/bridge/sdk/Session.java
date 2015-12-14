package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.sagebionetworks.bridge.sdk.models.subpopulations.ConsentStatus;
import org.sagebionetworks.bridge.sdk.models.users.DataGroups;
import org.sagebionetworks.bridge.sdk.models.users.SharingScope;

public interface Session {

    public void checkSignedIn();
    
    public String getUsername();

    public String getSessionToken();
    
    public SharingScope getSharingScope();

    public boolean isConsented();
    
    public boolean isSignedIn();
    
    public void signOut();
    
    public DataGroups getDataGroups();
    
    public List<ConsentStatus> getConsentStatuses();
    
    public UserClient getUserClient();
    
    public DeveloperClient getDeveloperClient();
    
    public ResearcherClient getResearcherClient();
    
    public AdminClient getAdminClient();

}
