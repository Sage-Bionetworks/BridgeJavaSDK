package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.models.users.SharingScope;

public interface Session {

    public void checkSignedIn();
    
    public String getUsername();

    public String getSessionToken();
    
    public SharingScope getSharingScope();

    public boolean isConsented();
    
    public boolean isSignedIn();
    
    public void signOut();
    
    public UserClient getUserClient();
    
    public DeveloperClient getDeveloperClient();
    
    public ResearcherClient getResearcherClient();
    
    public AdminClient getAdminClient();

}
