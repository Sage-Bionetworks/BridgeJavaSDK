package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.models.GuidVersionedOnHolder;
import org.sagebionetworks.bridge.sdk.models.surveys.DataType;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestion;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestionOption;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyRule;

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
        try {
            surveyApi.getAllVersionsOfAllSurveys();
        } catch (Throwable t) {
            assertEquals(t.getClass(), BridgeServerException.class);
        }
    }

    @Test
    public void saveAndRetrieveSurvey() {
        GuidVersionedOnHolder key = surveyApi.createNewSurvey(new TestSurvey().getSurvey());
        Survey survey = surveyApi.getSurveyForResearcher(key.getGuid(), key.getVersionedOn());

        List<SurveyQuestion> questions = survey.getQuestions();
        String prompt = questions.get(1).getPrompt();
        assertEquals("Prompt is correct.", "When did you last have a medical check-up?", prompt);
    }

    @Test
    public void createVersionPublish() {
        GuidVersionedOnHolder key = surveyApi.createNewSurvey(new TestSurvey().getSurvey());
        GuidVersionedOnHolder laterKey = surveyApi.createNewVersionForSurvey(key.getGuid(), key.getVersionedOn());
        assertNotEquals("Version has been updated.", key.getVersionedOn(), laterKey.getVersionedOn());

        Survey survey = surveyApi.getSurveyForResearcher(laterKey.getGuid(), laterKey.getVersionedOn());
        assertFalse("survey is not published.", survey.isPublished());

        surveyApi.publishSurvey(survey.getGuid(), survey.getVersionedOn());
        survey = surveyApi.getSurveyForResearcher(survey.getGuid(), survey.getVersionedOn());
        assertTrue("survey is now published.", survey.isPublished());
    }

    @Test
    public void getAllVersionsOfASurvey() {
        GuidVersionedOnHolder key = surveyApi.createNewSurvey(new TestSurvey().getSurvey());
        key = surveyApi.createNewVersionForSurvey(key.getGuid(), key.getVersionedOn());

        int count = surveyApi.getAllVersionsForSurvey(key.getGuid()).size();
        assertTrue("At least two versions for this survey.", count >= 2); // original version: checked for two. Can't
                                                                          // here, because can't delete surveys.
    }

    @Test
    public void canGetMostRecentOrRecentlyPublishedSurvey() {
        GuidVersionedOnHolder key = surveyApi.createNewSurvey(new TestSurvey().getSurvey());
        key = surveyApi.createNewVersionForSurvey(key.getGuid(), key.getVersionedOn());
        key = surveyApi.createNewVersionForSurvey(key.getGuid(), key.getVersionedOn());

        GuidVersionedOnHolder key1 = surveyApi.createNewSurvey(new TestSurvey().getSurvey());
        key1 = surveyApi.createNewVersionForSurvey(key1.getGuid(), key1.getVersionedOn());
        key1 = surveyApi.createNewVersionForSurvey(key1.getGuid(), key1.getVersionedOn());

        GuidVersionedOnHolder key2 = surveyApi.createNewSurvey(new TestSurvey().getSurvey());
        key2 = surveyApi.createNewVersionForSurvey(key2.getGuid(), key2.getVersionedOn());
        key2 = surveyApi.createNewVersionForSurvey(key2.getGuid(), key2.getVersionedOn());

        List<Survey> recentSurveys = surveyApi.getRecentVersionsOfAllSurveys();
        assertTrue("Recent versions of surveys exist in recentSurveys.", containsAll(recentSurveys, key, key1, key2));

        surveyApi.publishSurvey(key.getGuid(), key.getVersionedOn());
        surveyApi.publishSurvey(key2.getGuid(), key2.getVersionedOn());
        List<Survey> publishedSurveys = surveyApi.getPublishedVersionsOfAllSurveys();
        assertTrue("Published surveys contain recently published.", containsAll(publishedSurveys, key, key1, key2));
    }

    @Test
    public void canUpdateASurveyAndTypesAreCorrect() {
        GuidVersionedOnHolder key = surveyApi.createNewSurvey(new TestSurvey().getSurvey());
        Survey survey = surveyApi.getSurveyForResearcher(key.getGuid(), key.getVersionedOn());
        assertEquals("Type is Survey.", survey.getClass(), Survey.class);

        List<SurveyQuestion> questions = survey.getQuestions();
        assertEquals("Type is SurveyQuestion.", questions.get(0).getClass(), SurveyQuestion.class);
        assertEquals("Type is BooleanConstraints.", DataType.BOOLEAN, constraintTypeForQuestion(questions, 0));
        assertEquals("Type is DateConstraints", DataType.DATE, constraintTypeForQuestion(questions, 1));
        assertEquals("Type is DateTimeConstraints", DataType.DATETIME, constraintTypeForQuestion(questions, 2));
        assertEquals("Type is DecimalConstraints", DataType.DECIMAL, constraintTypeForQuestion(questions, 3));
        assertEquals("Type is IntegerConstraints", DataType.INTEGER, constraintTypeForQuestion(questions, 4));
        assertEquals("Type is IntegerConstraints", SurveyRule.class, questions.get(4).getConstraints().getRules()
                .get(0).getClass());
        assertEquals("Type is DurationConstraints", DataType.DURATION, constraintTypeForQuestion(questions, 5));
        assertEquals("Type is TimeConstraints", DataType.TIME, constraintTypeForQuestion(questions, 6));
        assertTrue("Type is MultiValueConstraints", questions.get(7).getConstraints().getAllowMultiple());
        assertEquals("Type is SurveyQuestionOption", SurveyQuestionOption.class, questions.get(7).getConstraints()
                .getEnumeration().get(0).getClass());

        survey.setName("New name");
        surveyApi.updateSurvey(survey);
        survey = surveyApi.getSurveyForResearcher(survey.getGuid(), survey.getVersionedOn());
        assertEquals("Name should have changed.", survey.getName(), "New name");
    }

    @Test
    public void participantCannotRetrieveUnpublishedSurvey() {
        GuidVersionedOnHolder key = surveyApi.createNewSurvey(new TestSurvey().getSurvey());
        try {
            surveyApi.getSurveyForUser(key.getGuid(), key.getVersionedOn());
            fail("Should not get here.");
        } catch (Throwable t) {
            assertEquals("Survey shouldn't be found, so call should error.", t.getClass(), BridgeServerException.class);
        }
    }

    private DataType constraintTypeForQuestion(List<SurveyQuestion> questions, int index) {
        return questions.get(index).getConstraints().getDataType();
    }

    private boolean containsAll(List<Survey> surveys, GuidVersionedOnHolder key, GuidVersionedOnHolder key1,
            GuidVersionedOnHolder key2) {
        int count = 0;
        for (Survey survey : surveys) {
            boolean guidsEqual = survey.getGuid().equals(key.getGuid());
            boolean guidsEqual1 = survey.getGuid().equals(key1.getGuid());
            boolean guidsEqual2 = survey.getGuid().equals(key2.getGuid());

            boolean versionsEqual = survey.getVersionedOn().equals(key.getVersionedOn());
            boolean versionsEqual1 = survey.getVersionedOn().equals(key1.getVersionedOn());
            boolean versionsEqual2 = survey.getVersionedOn().equals(key2.getVersionedOn());

            if (guidsEqual && versionsEqual) {
                count++;
            }
            if (guidsEqual1 && versionsEqual1) {
                count++;
            }
            if (guidsEqual2 && versionsEqual2) {
                count++;
            }
        }
        return count == 3;
    }
}
