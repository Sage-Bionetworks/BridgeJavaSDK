package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.BridgeServerException;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.ResearcherClient;
import org.sagebionetworks.bridge.sdk.Session;
import org.sagebionetworks.bridge.sdk.TestSurvey;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.models.GuidVersionedOnHolder;
import org.sagebionetworks.bridge.sdk.models.surveys.DataType;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestion;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestionOption;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyRule;

public class SurveyTest {

    private Session session;
    private UserClient user;
    private ResearcherClient researcher;

    @Before
    public void before() {
        Config config = ClientProvider.getConfig();
        session = ClientProvider.signIn(config.getAdminCredentials());
        user = session.getUserClient();
        researcher = session.getResearcherClient();
    }

    @After
    public void after() {
        session.signOut();
    }

    @Test
    public void saveAndRetrieveSurvey() {
        GuidVersionedOnHolder key = researcher.createSurvey(new TestSurvey());
        Survey survey = researcher.getSurvey(key.getGuid(), key.getVersionedOn());

        List<SurveyQuestion> questions = survey.getQuestions();
        String prompt = questions.get(1).getPrompt();
        assertEquals("Prompt is correct.", "When did you last have a medical check-up?", prompt);
    }

    @Test
    public void createVersionPublish() {
        GuidVersionedOnHolder key = researcher.createSurvey(new TestSurvey());
        GuidVersionedOnHolder laterKey = researcher.versionSurvey(key.getGuid(), key.getVersionedOn());
        assertNotEquals("Version has been updated.", key.getVersionedOn(), laterKey.getVersionedOn());

        Survey survey = researcher.getSurvey(laterKey.getGuid(), laterKey.getVersionedOn());
        assertFalse("survey is not published.", survey.isPublished());

        researcher.publishSurvey(survey.getGuid(), survey.getVersionedOn());
        survey = researcher.getSurvey(survey.getGuid(), survey.getVersionedOn());
        assertTrue("survey is now published.", survey.isPublished());
    }

    @Test
    public void getAllVersionsOfASurvey() {
        GuidVersionedOnHolder key = researcher.createSurvey(new TestSurvey());
        key = researcher.versionSurvey(key.getGuid(), key.getVersionedOn());

        int count = researcher.getAllVersionsOfASurvey(key.getGuid()).size();
        assertEquals("Two versions for this survey.", 2, count);
    }

    @Test
    public void canGetMostRecentOrRecentlyPublishedSurvey() {
        GuidVersionedOnHolder key = researcher.createSurvey(new TestSurvey());
        key = researcher.versionSurvey(key.getGuid(), key.getVersionedOn());
        key = researcher.versionSurvey(key.getGuid(), key.getVersionedOn());

        GuidVersionedOnHolder key1 = researcher.createSurvey(new TestSurvey());
        key1 = researcher.versionSurvey(key1.getGuid(), key1.getVersionedOn());
        key1 = researcher.versionSurvey(key1.getGuid(), key1.getVersionedOn());

        GuidVersionedOnHolder key2 = researcher.createSurvey(new TestSurvey());
        key2 = researcher.versionSurvey(key2.getGuid(), key2.getVersionedOn());
        key2 = researcher.versionSurvey(key2.getGuid(), key2.getVersionedOn());

        List<Survey> recentSurveys = researcher.getRecentVersionsOfAllSurveys();
        assertTrue("Recent versions of surveys exist in recentSurveys.", containsAll(recentSurveys, key, key1, key2));

        researcher.publishSurvey(key.getGuid(), key.getVersionedOn());
        researcher.publishSurvey(key2.getGuid(), key2.getVersionedOn());
        List<Survey> publishedSurveys = researcher.getPublishedVersionsOfAllSurveys();
        assertTrue("Published surveys contain recently published.", containsAll(publishedSurveys, key, key2));
    }

    @Test
    public void canUpdateASurveyAndTypesAreCorrect() {
        GuidVersionedOnHolder key = researcher.createSurvey(new TestSurvey());
        Survey survey = researcher.getSurvey(key.getGuid(), key.getVersionedOn());
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
        researcher.updateSurvey(survey);
        survey = researcher.getSurvey(survey.getGuid(), survey.getVersionedOn());
        assertEquals("Name should have changed.", survey.getName(), "New name");
    }

    @Test
    public void participantCannotRetrieveUnpublishedSurvey() {
        GuidVersionedOnHolder key = researcher.createSurvey(new TestSurvey());
        try {
            user.getSurvey(key.getGuid(), key.getVersionedOn());
            fail("Should not get here.");
        } catch (Throwable t) {
            assertEquals("Survey shouldn't be found, so call should error.", t.getClass(), BridgeServerException.class);
        }
    }

    private DataType constraintTypeForQuestion(List<SurveyQuestion> questions, int index) {
        return questions.get(index).getConstraints().getDataType();
    }

    private boolean containsAll(List<Survey> surveys, GuidVersionedOnHolder... keys) {
        int count = 0;
        for (Survey survey : surveys) {
            for (GuidVersionedOnHolder key : keys) {
                if (survey.getGuid().equals(key.getGuid()) && survey.getVersionedOn().equals(key.getVersionedOn())) {
                    count++;
                }
            }
        }
        return count == keys.length;
    }


}
