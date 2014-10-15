package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.models.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.StudyConsent;

public class StudyConsentApiCallerTest {

    private static StudyConsentApiCaller studyConsentApi;

    @BeforeClass
    public static void initialSetup() {
        ClientProvider provider = ClientProvider.valueOf();

        // Sign in admin
        String adminEmail = provider.getConfig().getAdminEmail();
        String adminPassword = provider.getConfig().getAdminPassword();
        SignInCredentials adminSignIn = SignInCredentials.valueOf().setUsername(adminEmail).setPassword(adminPassword);
        provider.signIn(adminSignIn);

        studyConsentApi = StudyConsentApiCaller.valueOf(provider);
    }

    @Test
    public void test() {
        StudyConsent current = StudyConsent.valueOf("/path/to", 18);
        StudyConsent returned = studyConsentApi.addStudyConsent(current);
        System.out.println(returned);
        assertNotNull(returned);

        List<StudyConsent> studyConsents = studyConsentApi.getAllStudyConsents();
        System.out.println(studyConsents);
        assertNotNull("studyConsents should not be null.", studyConsents);
        assertTrue("studyConsents should have at least one StudyConsent", studyConsents.size() > 0);

        current = studyConsentApi.getStudyConsent(studyConsents.get(0).getTimestamp());
        assertNotNull("studyConsent should not be null.", current);
        assertEquals(current, studyConsents.get(0));

        studyConsentApi.setActiveStudyConsent(current.getTimestamp());

        assertEquals(current, studyConsentApi.getActiveStudyConsent());
    }

}
