package org.sagebionetworks.bridge.sdk;

public interface Session {

    public void checkSignedIn(); // TODO: REMOVE ME, although very convenient.
    
    public String getUsername();

    public String getSessionToken();

    public boolean isConsented();

    public boolean isDataSharing();
    
    public boolean isSignedIn();
    
    public void signOut();
    
    public UserClient getUserClient();
    
    public ResearcherClient getResearcherClient();
    
    public AdminClient getAdminClient();

}
