package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

public class SurveyApiCallerTest {

    private TestUser testResearcher;
    private TestUser testUser;
    private SurveyApiCaller surveyApi;

    @Before
    public void before() {
        testResearcher = TestUserHelper.createAndSignInUser(SurveyApiCallerTest.class, true, "teststudy_researcher");

        surveyApi = SurveyApiCaller.valueOf(testResearcher.getSession());
        List<Survey> surveys = surveyApi.getAllVersionsOfAllSurveys();
        for (Survey survey : surveys) {
            surveyApi.closeSurvey(survey.getGuid(), survey.getVersionedOn());
        }
    }

    @After
    public void after() {
        if (testResearcher != null && testResearcher.isSignedIn()) {
            testResearcher.signOutAndDeleteUser();
        }
        if (testUser != null && testUser.isSignedIn()) {
            testUser.signOutAndDeleteUser();
        }
    }

    @Test
    public void cannotSubmitAsNormalUser() {
        testResearcher.signOutAndDeleteUser();
        testUser = TestUserHelper.createAndSignInUser(SurveyApiCallerTest.class, true, null);
        try {
            surveyApi.getAllVersionsOfAllSurveys();
        } catch (Throwable t) {
            assertEquals(t.getClass(), BridgeServerException.class);
        }
    }
}
