package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
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
    public void test() {
        try {
            StudyConsent consent = StudyConsent.valueOf("/path/to", 22);
            studyConsentApi.addStudyConsent(consent);
            fail("addStudyConsent method should have thrown an exception due to incorrect permissions.");
        } catch (Throwable t) {
            assertTrue("The exception thrown was a BridgeServerException.",
                    t.getClass().equals(BridgeServerException.class));
        }
    }
}
