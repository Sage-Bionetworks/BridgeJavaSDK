package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

public class SurveyApiCallerTest {

    private TestUser testUser;
    private SurveyApiCaller surveyApi;

    @Before
    public void before() {
        testUser = TestUserHelper.createAndSignInUser(SurveyApiCallerTest.class, true, "teststudy_researcher");
        
        surveyApi = SurveyApiCaller.valueOf(testUser.getSession());
        List<Survey> surveys = surveyApi.getAllVersionsOfAllSurveys();
        for (Survey survey : surveys) {
            surveyApi.closeSurvey(survey.getGuid(), survey.getVersionedOn());
        }
    }
    
    @After
    public void after() {
        testUser.signOutAndDeleteUser();
    }

    @Test
    @Ignore
    public void cannotSubmitAsNormalUser() {
    }

}
