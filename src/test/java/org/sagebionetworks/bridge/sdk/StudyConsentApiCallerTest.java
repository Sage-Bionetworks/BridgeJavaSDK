package org.sagebionetworks.bridge.sdk;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.models.StudyConsent;

public class StudyConsentApiCallerTest {

    private StudyConsentApiCaller studyConsentApi;

    private TestUser testUser;

    @Before
    public void before() {
        testUser = TestUserHelper.createAndSignInUser(StudyConsentApiCallerTest.class, true);
        studyConsentApi = StudyConsentApiCaller.valueOf(testUser.getSession());
    }

    @After
    public void after() {
        testUser.signOutAndDeleteUser();
    }

    @Test(expected = BridgeServerException.class)
    public void cannotCreateStudyConsentUnlessAdmin() {
        StudyConsent consent = StudyConsent.valueOf("/path/to", 22);
        studyConsentApi.addStudyConsent(consent);
    }
}
