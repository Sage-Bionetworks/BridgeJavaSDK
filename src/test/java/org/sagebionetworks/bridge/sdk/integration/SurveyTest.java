package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.sagebionetworks.bridge.sdk.TestSurvey.BOOLEAN_ID;
import static org.sagebionetworks.bridge.sdk.TestSurvey.DATETIME_ID;
import static org.sagebionetworks.bridge.sdk.TestSurvey.DATE_ID;
import static org.sagebionetworks.bridge.sdk.TestSurvey.DECIMAL_ID;
import static org.sagebionetworks.bridge.sdk.TestSurvey.DURATION_ID;
import static org.sagebionetworks.bridge.sdk.TestSurvey.INTEGER_ID;
import static org.sagebionetworks.bridge.sdk.TestSurvey.MULTIVALUE_ID;
import static org.sagebionetworks.bridge.sdk.TestSurvey.TIME_ID;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.ResearcherClient;
import org.sagebionetworks.bridge.sdk.TestSurvey;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.exceptions.BridgeServerException;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.surveys.Constraints;
import org.sagebionetworks.bridge.sdk.models.surveys.DataType;
import org.sagebionetworks.bridge.sdk.models.surveys.DateTimeConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestion;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestionOption;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyRule;

import com.google.common.collect.Lists;

public class SurveyTest {

    private TestUser researcher;
    private TestUser user;
    private List<GuidCreatedOnVersionHolder> keys = Lists.newArrayList();

    @Before
    public void before() {
        researcher = TestUserHelper.createAndSignInUser(SurveyTest.class, true, Tests.RESEARCHER_ROLE);
        user = TestUserHelper.createAndSignInUser(SurveyTest.class, true);
    }

    @After
    public void after() {
        ResearcherClient client = researcher.getSession().getResearcherClient();
        for (GuidCreatedOnVersionHolder key : keys) {
            client.closeSurvey(key);
            client.deleteSurvey(key);
        }
        researcher.signOutAndDeleteUser();
        user.signOutAndDeleteUser();
    }

    @Test(expected=BridgeServerException.class)
    public void cannotSubmitAsNormalUser() {
        user.getSession().getResearcherClient().getAllVersionsOfAllSurveys();
    }

    @Test
    public void saveAndRetrieveSurvey() {
        ResearcherClient client = researcher.getSession().getResearcherClient();
        GuidCreatedOnVersionHolder key = client.createSurvey(new TestSurvey());
        keys.add(key);
        Survey survey = client.getSurvey(key);

        List<SurveyQuestion> questions = survey.getQuestions();
        String prompt = questions.get(1).getPrompt();
        assertEquals("Prompt is correct.", "When did you last have a medical check-up?", prompt);
    }

    @Test
    public void createVersionPublish() {
        ResearcherClient client = researcher.getSession().getResearcherClient();
        
        GuidCreatedOnVersionHolder key = client.createSurvey(new TestSurvey());
        keys.add(key);
        GuidCreatedOnVersionHolder laterKey = client.versionSurvey(key);
        keys.add(laterKey);
        assertNotEquals("Version has been updated.", key.getCreatedOn(), laterKey.getCreatedOn());

        Survey survey = client.getSurvey(laterKey.getGuid(), laterKey.getCreatedOn());
        assertFalse("survey is not published.", survey.isPublished());

        client.publishSurvey(survey);
        survey = client.getSurvey(survey.getGuid(), survey.getCreatedOn());
        assertTrue("survey is now published.", survey.isPublished());
    }

    @Test
    public void getAllVersionsOfASurvey() {
        ResearcherClient client = researcher.getSession().getResearcherClient();
        
        GuidCreatedOnVersionHolder key = client.createSurvey(new TestSurvey());
        keys.add(key);
        key = client.versionSurvey(key);
        keys.add(key);
        
        int count = client.getAllVersionsOfASurvey(key.getGuid()).getTotal();
        assertEquals("Two versions for this survey.", 2, count);

        client.closeSurvey(key);
    }

    @Test
    public void canGetMostRecentOrRecentlyPublishedSurvey() {
        ResearcherClient client = researcher.getSession().getResearcherClient();
        
        GuidCreatedOnVersionHolder key = client.createSurvey(new TestSurvey());
        keys.add(key);
        key = client.versionSurvey(key);
        keys.add(key);
        key = client.versionSurvey(key);
        keys.add(key);

        GuidCreatedOnVersionHolder key1 = client.createSurvey(new TestSurvey());
        keys.add(key1);
        key1 = client.versionSurvey(key1);
        keys.add(key1);
        key1 = client.versionSurvey(key1);
        keys.add(key1);

        GuidCreatedOnVersionHolder key2 = client.createSurvey(new TestSurvey());
        keys.add(key2);
        key2 = client.versionSurvey(key2);
        keys.add(key2);
        key2 = client.versionSurvey(key2);
        keys.add(key2);

        ResourceList<Survey> recentSurveys = client.getRecentVersionsOfAllSurveys();
        assertTrue("Recent versions of surveys exist in recentSurveys.", containsAll(recentSurveys.getItems(), key, key1, key2));

        client.publishSurvey(key);
        client.publishSurvey(key2);
        ResourceList<Survey> publishedSurveys = client.getPublishedVersionsOfAllSurveys();
        assertTrue("Published surveys contain recently published.", containsAll(publishedSurveys.getItems(), key, key2));
    }

    @Test
    public void canUpdateASurveyAndTypesAreCorrect() {
        ResearcherClient client = researcher.getSession().getResearcherClient();
        
        GuidCreatedOnVersionHolder key = client.createSurvey(new TestSurvey());
        keys.add(key);
        Survey survey = client.getSurvey(key.getGuid(), key.getCreatedOn());
        assertEquals("Type is Survey.", survey.getClass(), Survey.class);

        List<SurveyQuestion> questions = survey.getQuestions();
        assertEquals("Type is SurveyQuestion.", questions.get(0).getClass(), SurveyQuestion.class);

        assertEquals("Type is BooleanConstraints.", DataType.BOOLEAN, getConstraints(survey, BOOLEAN_ID).getDataType());
        assertEquals("Type is DateConstraints", DataType.DATE, getConstraints(survey, DATE_ID).getDataType());
        assertEquals("Type is DateTimeConstraints", DataType.DATETIME, getConstraints(survey, DATETIME_ID).getDataType());
        assertEquals("Type is DecimalConstraints", DataType.DECIMAL, getConstraints(survey, DECIMAL_ID).getDataType());
        Constraints intCon = getConstraints(survey, INTEGER_ID);
        assertEquals("Type is IntegerConstraints", DataType.INTEGER, intCon.getDataType());
        assertEquals("Has a rule of type SurveyRule", SurveyRule.class, intCon.getRules().get(0).getClass());
        assertEquals("Type is DurationConstraints", DataType.DURATION, getConstraints(survey, DURATION_ID).getDataType());
        assertEquals("Type is TimeConstraints", DataType.TIME, getConstraints(survey, TIME_ID).getDataType());
        Constraints multiCon = getConstraints(survey, MULTIVALUE_ID);
        assertTrue("Type is MultiValueConstraints", multiCon.getAllowMultiple());
        assertEquals("Type is SurveyQuestionOption", SurveyQuestionOption.class, multiCon.getEnumeration().get(0).getClass());

        survey.setName("New name");
        client.updateSurvey(survey);
        survey = client.getSurvey(survey.getGuid(), survey.getCreatedOn());
        assertEquals("Name should have changed.", survey.getName(), "New name");
    }

    @Test
    public void dateBasedConstraintsPersistedCorrectly() {
        ResearcherClient client = researcher.getSession().getResearcherClient();
        
        GuidCreatedOnVersionHolder key = client.createSurvey(new TestSurvey());
        Survey survey = client.getSurvey(key);

        DateTimeConstraints dateCon = (DateTimeConstraints)getConstraints(survey, DATETIME_ID);
        DateTime earliest = dateCon.getEarliestValue();
        DateTime latest = dateCon.getLatestValue();
        assertNotNull("Earliest has been set", earliest);
        assertEquals("Date is correct", DateTime.parse("2000-01-01").withZone(DateTimeZone.UTC), earliest);
        assertNotNull("Latest has been set", latest);
        assertEquals("Date is correct", DateTime.parse("2020-12-31").withZone(DateTimeZone.UTC), latest);
    }

    @Test(expected=BridgeServerException.class)
    public void participantCannotRetrieveUnpublishedSurvey() {
        ResearcherClient client = researcher.getSession().getResearcherClient();
        GuidCreatedOnVersionHolder key = client.createSurvey(new TestSurvey());
        keys.add(key);

        UserClient userClient = user.getSession().getUserClient();
        userClient.getSurvey(key);
        fail("Should not get here.");
    }

    private Constraints getConstraints(Survey survey, String id) {
        return survey.getQuestionByIdentifier(id).getConstraints();
    }

    private boolean containsAll(List<Survey> surveys, GuidCreatedOnVersionHolder... keys) {
        int count = 0;
        for (Survey survey : surveys) {
            for (GuidCreatedOnVersionHolder key : keys) {
                if (survey.getGuid().equals(key.getGuid()) && survey.getCreatedOn().equals(key.getCreatedOn())) {
                    count++;
                }
            }
        }
        return count == keys.length;
    }
}
