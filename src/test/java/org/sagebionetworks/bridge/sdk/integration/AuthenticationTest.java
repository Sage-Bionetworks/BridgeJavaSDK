package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Ignore;
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
    @Ignore
    public void canResendEmailVerification() {
        String username = TestUserHelper.makeUserName(AuthenticationTest.class);
        String email = username + "@sagebase.org";
        String password = "P4ssword";
        
        try {
            
            SignUpCredentials signUp = new SignUpCredentials(Tests.TEST_KEY, username, email, password, null);
            ClientProvider.signUp(signUp);
            
            // Beyond an exception being thrown, there's not a lot you can test here.
            ClientProvider.resendEmailVerification(new EmailCredentials(Tests.TEST_KEY, email));
            
        } finally {
            Config config = ClientProvider.getConfig();
            Session session = ClientProvider.signIn(config.getAdminCredentials());
            session.getAdminClient().deleteUser(email);
        }
    }
    
    @Test
    @Ignore
    public void resendingEmailVerificationToUnknownEmailDoesNotThrowException() throws Exception {
        ClientProvider.resendEmailVerification(new EmailCredentials(Tests.TEST_KEY, "fooboo-sagebridge@antwerp.com"));
    }
    
    @Test
    @Ignore
    public void requestingResetPasswordForUnknownEmailDoesNotThrowException() throws Exception {
        ClientProvider.requestResetPassword(new EmailCredentials(Tests.TEST_KEY, "fooboo-sagebridge@antwerp.com"));
    }
    
    @Test
    public void accountWithOneStudySeparateFromAccountWithSecondStudy() {
        TestUser testUser1 = TestUserHelper.createAndSignInUser(AuthenticationTest.class, true);
        Config config = ClientProvider.getConfig();
        AdminClient client = ClientProvider.signIn(config.getAdminCredentials()).getAdminClient();
        String studyId = Tests.randomIdentifier(AuthenticationTest.class);

        // Make a second study for this test:
        Study study = new Study();
        study.setIdentifier(studyId);
        study.setName("Second Study");
        study.setSponsorName("Second Study");
        study.setSupportEmail("bridge-testing+support@sagebase.org");
        study.setConsentNotificationEmail("bridge-testing+consent@sagebase.org");
        study.setTechnicalEmail("bridge-testing+technical@sagebase.org");
        study.setResetPasswordTemplate(Tests.TEST_RESET_PASSWORD_TEMPLATE);
        study.setVerifyEmailTemplate(Tests.TEST_VERIFY_EMAIL_TEMPLATE);
        client.createStudy(study);

        try {
            // Can we sign in to secondstudy? No.
            try {
                config.set(Props.STUDY_IDENTIFIER, studyId);
                ClientProvider.signIn(new SignInCredentials(studyId, testUser1.getUsername(), testUser1.getPassword()));
                fail("Should not have allowed sign in");
            } catch(BridgeServerException e) {
                assertEquals(404, e.getStatusCode());
            }
        } finally {
            config.set(Props.STUDY_IDENTIFIER, "api");
            client.deleteStudy(studyId);
            testUser1.signOutAndDeleteUser();
        }
    }
    
    // BRIDGE-465. We can at least verify that it gets processed as an error.
    @Test
    @Ignore
    public void emailVerificationThrowsTheCorrectError() throws Exception {
        Config config = ClientProvider.getConfig();
        
        HttpResponse response = Request
            .Post(config.getEnvironment().getUrl() + "/api/v1/auth/verifyEmail?study=api")
            .body(new StringEntity("{\"sptoken\":\"testtoken\",\"study\":\"api\"}"))
            .execute().returnResponse();
        assertEquals(404, response.getStatusLine().getStatusCode());
        assertEquals("{\"message\":\"Account not found.\"}", EntityUtils.toString(response.getEntity()));
    }
    
    // Should not be able to tell from the sign up response if an email is enrolled in the study or not.
    // Server change is not yet checked in for this.
    @Test
    @Ignore
    public void secondTimeSignUpLooksTheSameAsFirstTimeSignUp() {
        TestUser testUser = TestUserHelper.createAndSignInUser(AuthenticationTest.class, true);
        try {
            testUser.getSession().signOut();
            
            // Now create the same user.
            SignUpCredentials signup = new SignUpCredentials(Tests.TEST_KEY, testUser.getUsername(), testUser.getEmail(), testUser.getPassword(), null);
            ClientProvider.signUp(signup);
            // This should not have thrown an error.
            
        } finally {
            testUser.signOutAndDeleteUser();
        }
    }
}
