package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.sagebionetworks.bridge.IntegrationSmokeTest;
import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.AdminClient;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.Config.Props;
import org.sagebionetworks.bridge.sdk.Session;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.exceptions.BridgeServerException;
import org.sagebionetworks.bridge.sdk.models.studies.Study;
import org.sagebionetworks.bridge.sdk.models.users.EmailCredentials;
import org.sagebionetworks.bridge.sdk.models.users.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.users.SignUpCredentials;

@Category(IntegrationSmokeTest.class)
public class AuthenticationTest {

    @Test
    public void canResendEmailVerification() {
        String username = TestUserHelper.makeUserName(AuthenticationTest.class);
        String email = username + "@sagebase.org";
        String password = "P4ssword";
        
        try {
            
            SignUpCredentials signUp = new SignUpCredentials(Tests.TEST_KEY, username, email, password);
            ClientProvider.signUp(signUp);
            
            // Beyond an exception being thrown, there's not a lot you can test here.
            ClientProvider.resendEmailVerification(new EmailCredentials(Tests.TEST_KEY, email));
            
        } finally {
            Config config = ClientProvider.getConfig();
            Session session = ClientProvider.signIn(config.getAdminCredentials());
            session.getAdminClient().deleteUser(email);
        }
    }
    
    @Test(expected = BridgeServerException.class)
    public void cannotSendToAnyRandomEmail() throws Exception {
        ClientProvider.resendEmailVerification(new EmailCredentials(Tests.TEST_KEY, "fooboo-sagebridge@antwerp.com"));
    }
    
    @Test
    public void accountWithOneStudySeparateFromAccountWithSecondStudy() {
        TestUser testUser1 = TestUserHelper.createAndSignInUser(AuthenticationTest.class, true);
        Config config = ClientProvider.getConfig();
        AdminClient client = ClientProvider.signIn(config.getAdminCredentials()).getAdminClient();
        try {
            // Make a second study for this test:
            Study study = new Study();
            study.setIdentifier("secondstudy");
            study.setName("Second Study");
            study.setSupportEmail("bridge-testing+support@sagebase.org");
            study.setConsentNotificationEmail("bridge-testing+consent@sagebase.org");
            study.setTechnicalEmail("bridge-testing+technical@sagebase.org");
            client.createStudy(study);
            
            // Can we sign in to secondstudy? No.
            try {
                config.set(Props.STUDY_IDENTIFIER, "secondstudy");
                ClientProvider.signIn(new SignInCredentials("secondstudy", testUser1.getUsername(), testUser1.getPassword()));
                fail("Should not have allowed sign in");
            } catch(BridgeServerException e) {
                assertEquals(404, e.getStatusCode());
            }
        } finally {
            config.set(Props.STUDY_IDENTIFIER, "api");
            client.deleteStudy("secondstudy");
            testUser1.signOutAndDeleteUser();
        }
    }
    
    // BRIDGE-465. We can at least verify that it gets processed as an error.
    @Test
    public void emailVerificationThrowsTheCorrectError() throws Exception {
        Config config = ClientProvider.getConfig();
        
        HttpResponse response = Request
                        .Post(config.getEnvironment().getUrl() + "/api/v1/auth/verifyEmail?study=api")
                        .body(new StringEntity("{\"sptoken\":\"testtoken\"}"))
                        .execute().returnResponse();
        assertEquals(404, response.getStatusLine().getStatusCode());
        assertEquals("{\"message\":\"Account not found.\"}", EntityUtils.toString(response.getEntity()));
    }
}
