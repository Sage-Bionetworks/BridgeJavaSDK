package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.models.StudyConsent;

public class StudyConsentApiCallerTest {

    private StudyConsentApiCaller studyConsentApi;
    
    private TestUser researcher;

    @Before
    public void before() {
        researcher = TestUserHelper.createAndSignInUser(StudyConsentApiCallerTest.class, true, "admin", "teststudy_researcher");
        
        studyConsentApi = StudyConsentApiCaller.valueOf(researcher.getSession());
    }

    @After
    public void after() {
        researcher.signOutAndDeleteUser();
    }

    @Test
    @Ignore
    public void test() {
        try {
            StudyConsent consent = StudyConsent.valueOf("/path/to", 22);
            studyConsentApi.addStudyConsent(consent);
            fail("addStudyConsent method should have thrown an exception due to incorrect permissions.");
        } catch (Throwable t) {
            assertTrue("The exception thrown was a BridgeServerException.",
                    t.getClass().equals(BridgeServerException.class));
        }

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
