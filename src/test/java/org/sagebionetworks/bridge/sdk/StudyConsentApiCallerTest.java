package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.models.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.StudyConsent;

public class StudyConsentApiCallerTest {

    private StudyConsentApiCaller studyConsentApi;
    private UserManagementApiCaller userManagementApi;
    private ClientProvider provider;
    private SignInCredentials admin;
    private SignInCredentials user;

    @Before
    public void before() {
        provider = ClientProvider.valueOf();

        userManagementApi = UserManagementApiCaller.valueOf(provider);
        studyConsentApi = StudyConsentApiCaller.valueOf(provider);

        // Get admin sign in
        String adminEmail = provider.getConfig().getAdminEmail();
        String adminPassword = provider.getConfig().getAdminPassword();
        admin = SignInCredentials.valueOf().setUsername(adminEmail).setPassword(adminPassword);

        // Get user sign in
        String userEmail = "tests@sagebase.org";
        String username = "testname";
        String userPassword = "password";
        boolean consent = true;
        user = SignInCredentials.valueOf().setUsername(userEmail).setPassword(userPassword);

        // Create user.
        provider.signIn(admin);
        userManagementApi.createUser(userEmail, username, userPassword, consent);
        provider.signOut();
    }

    @After
    public void after() {
        userManagementApi.deleteUser(user.getUsername());
    }

    @Test
    public void test() {
        provider.signIn(user);
        try {
            StudyConsent consent = StudyConsent.valueOf("/path/to", 22);
            studyConsentApi.addStudyConsent(consent);
            fail("addStudyConsent method should have thrown an exception due to incorrect permissions.");
        } catch (Throwable t) {
            assertTrue("The exception thrown was a BridgeServerException.",
                    t.getClass().equals(BridgeServerException.class));
        }

        provider.signOut();
        provider.signIn(admin);

        StudyConsent current = StudyConsent.valueOf("/path/to", 18);
        StudyConsent returned = studyConsentApi.addStudyConsent(current);
        assertNotNull(returned);

        List<StudyConsent> studyConsents = studyConsentApi.getAllStudyConsents();
        assertNotNull("studyConsents should not be null.", studyConsents);
        assertTrue("studyConsents should have at least one StudyConsent", studyConsents.size() > 0);

        current = studyConsentApi.getStudyConsent(studyConsents.get(0).getCreatedOn());
        assertNotNull("studyConsent should not be null.", current);
        assertEquals("Retrieved study consent should equal one asked for.", current, studyConsents.get(0));

        studyConsentApi.setActiveStudyConsent(current.getCreatedOn());

        assertFalse("Active consent is returned.",
                current.isActive() == studyConsentApi.getActiveStudyConsent().isActive());
    }

}
