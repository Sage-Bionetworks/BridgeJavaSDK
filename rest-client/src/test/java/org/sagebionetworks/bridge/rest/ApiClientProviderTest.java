package org.sagebionetworks.bridge.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.rest.api.AuthenticationApi;
import org.sagebionetworks.bridge.rest.model.SignIn;

public class ApiClientProviderTest {
    
    private static final String BASE_URL = "https://developer.sagebridge.org";
    private static final String USER_AGENT = "Cardio Health/1 (Unknown iPhone; iOS/9.0.2) BridgeSDK/4";
    
    private SignIn signIn;
    private ApiClientProvider.AuthenticatedClientProvider provider;
    
    @Before
    public void before() {
        signIn = new SignIn().email("email@email.com").password("password").appId("test-app");
        provider = spy(
                new ApiClientProvider(BASE_URL, USER_AGENT, "en, fr", signIn.getAppId())
                        .getAuthenticatedClientProviderBuilder()
                        .withEmail(signIn.getEmail())
                        .withPassword(signIn.getPassword()).build());
    }
    
    @Test
    public void test() {
        AuthenticationApi authApi = provider.getClient(AuthenticationApi.class);
        assertNotNull(authApi);

        UserSessionInfoProvider sessionProvider = provider.getUserSessionInfoProvider();
        assertNotNull(sessionProvider);

        assertEquals(USER_AGENT, provider.getUserAgent());
    }
    
}
