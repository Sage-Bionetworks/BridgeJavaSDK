package org.sagebionetworks.bridge.sdk;

public interface Session {

    public void checkSignedIn();
    
    public String getUsername();

    public String getSessionToken();
    
    public ScopeOfSharing getScopeOfSharing();

    public boolean isConsented();
    
    public boolean isSignedIn();
    
    public void signOut();
    
    public UserClient getUserClient();
    
    public ResearcherClient getResearcherClient();
    
    public AdminClient getAdminClient();

}
