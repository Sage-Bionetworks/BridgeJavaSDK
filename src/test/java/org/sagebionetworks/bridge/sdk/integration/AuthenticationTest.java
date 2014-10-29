package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.apache.http.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.BridgeServerException;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.TestApiCaller;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.models.SignInCredentials;

public class AuthenticationTest {

    private Properties properties;
    private TestUser user;
    
    @Before
    public void before() {
        user = TestUserHelper.createAndSignInUser(AuthenticationTest.class, true);
        properties = Tests.getApplicationProperties();
    }
    
    @After
    public void after() {
        user.signOutAndDeleteUser();
    }
    
    @Test
    public void signInNoCredentialsFailsWith400() {
        try {
            user.getSession().signOut();
            ClientProvider.signIn(SignInCredentials.valueOf().setUsername(null).setPassword(null));
            fail("Should have thrown an exception");
        } catch(BridgeServerException e) {
            assertEquals("Exception is a 400 Bad Request", 400, e.getStatusCode());
        }
    }

    @Test
    public void signInGarbageCredentialsFailsWith400() {
        try {
            TestApiCaller caller = new TestApiCaller(null);
            String url = properties.getProperty(Config.Props.AUTH_SIGNIN_API.getPropertyName());
            
            HttpResponse response = caller.post(url, "username=bob&password=foo");
            assertEquals("Response should be 400 Bad Request", 400, response.getStatusLine().getStatusCode());
            fail("Should have thrown an exception");
        } catch(BridgeServerException e) {
            assertEquals("Exception is a 400 Bad Request", 400, e.getStatusCode());
        }
    }
    
    // canSignIn/canSignOut -- we do this over and over and over in the tests already. Server verifies
    // that the session is removed from Redis, cookies, etc.
    
}
