package org.sagebionetworks.bridge.rest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.spy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sagebionetworks.bridge.rest.api.AuthenticationApi;
import org.sagebionetworks.bridge.rest.model.SignIn;

public class ApiClientProviderTest {
    
    private static final String BASE_URL = "https://developer.sagebridge.org";
    private static final String USER_AGENT = "Cardio Health/1 (Unknown iPhone; iOS/9.0.2) BridgeSDK/4";
    
    private SignIn signIn;
    
    private ApiClientProvider provider;
    
    @Before
    public void before() {
        signIn = new SignIn().email("email@email.com").password("password").study("test-study");
        provider = spy(new ApiClientProvider(BASE_URL, USER_AGENT, "en, fr"));
    }
    
    @Test
    public void test() {
        UserSessionInfoProvider sessionProvider = provider.getUserSessionInfoProvider(signIn);
        assertNull(sessionProvider);
        
        AuthenticationApi authApi = provider.getClient(AuthenticationApi.class, signIn);
        assertNotNull(authApi);
        
        sessionProvider = provider.getUserSessionInfoProvider(signIn);
        assertNotNull(sessionProvider);
    }
    
}
