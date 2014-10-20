package org.sagebionetworks.bridge;

import org.junit.After;
import org.junit.Before;
import org.sagebionetworks.bridge.sdk.BridgeResearcherClient;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.models.GuidVersionHolder;

public class SurveyTest {
    
    private ClientProvider provider;
    private BridgeResearcherClient client;
    
    @Before
    public void before() {
        // Assumes the default user is a researcher in the API test (which I am, for now)
        provider = ClientProvider.valueOf();
        provider.signIn();
        
        client = provider.getResearcherClient();
    }
    
    @After
    public void after() {
        provider.signOut();
    }
    
}
