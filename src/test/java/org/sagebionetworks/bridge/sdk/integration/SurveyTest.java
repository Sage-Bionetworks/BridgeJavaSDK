package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.sagebionetworks.bridge.sdk.TestSurvey.*;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.BridgeServerException;
import org.sagebionetworks.bridge.sdk.ResearcherClient;
import org.sagebionetworks.bridge.sdk.TestSurvey;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.models.GuidVersionedOnHolder;
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
    private List<GuidVersionedOnHolder> keys = Lists.newArrayList();

    @Before
    public void before() {
        researcher = TestUserHelper.createAndSignInUser(SurveyTest.class, true, Tests.RESEARCHER_ROLE);
        user = TestUserHelper.createAndSignInUser(SurveyTest.class, true);
    }

    @After
    public void after() {
        ResearcherClient client = researcher.getSession().getResearcherClient();
        for (GuidVersionedOnHolder key : keys) {
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
        GuidVersionedOnHolder key = client.createSurvey(new TestSurvey());
        keys.add(key);
        Survey survey = client.getSurvey(key);

        List<SurveyQuestion> questions = survey.getQuestions();
        String prompt = questions.get(1).getPrompt();
        assertEquals("Prompt is correct.", "When did you last have a medical check-up?", prompt);
    }

    @Test
    public void createVersionPublish() {
        ResearcherClient client = researcher.getSession().getResearcherClient();
        
        GuidVersionedOnHolder key = client.createSurvey(new TestSurvey());
        keys.add(key);
        GuidVersionedOnHolder laterKey = client.versionSurvey(key);
        keys.add(laterKey);
        assertNotEquals("Version has been updated.", key.getVersionedOn(), laterKey.getVersionedOn());

        Survey survey = client.getSurvey(laterKey.getGuid(), laterKey.getVersionedOn());
        assertFalse("survey is not published.", survey.isPublished());

        client.publishSurvey(survey);
        survey = client.getSurvey(survey.getGuid(), survey.getVersionedOn());
        assertTrue("survey is now published.", survey.isPublished());
    }

    @Test
    public void getAllVersionsOfASurvey() {
        ResearcherClient client = researcher.getSession().getResearcherClient();
        
        GuidVersionedOnHolder key = client.createSurvey(new TestSurvey());
        keys.add(key);
        key = client.versionSurvey(key);
        keys.add(key);
        
        int count = client.getAllVersionsOfASurvey(key.getGuid()).size();
        assertEquals("Two versions for this survey.", 2, count);
        
        client.closeSurvey(key);
    }

    @Test
    public void canGetMostRecentOrRecentlyPublishedSurvey() {
        ResearcherClient client = researcher.getSession().getResearcherClient();
        
        GuidVersionedOnHolder key = client.createSurvey(new TestSurvey());
        keys.add(key);
        key = client.versionSurvey(key);
        keys.add(key);
        key = client.versionSurvey(key);
        keys.add(key);

        GuidVersionedOnHolder key1 = client.createSurvey(new TestSurvey());
        keys.add(key1);
        key1 = client.versionSurvey(key1);
        keys.add(key1);
        key1 = client.versionSurvey(key1);
        keys.add(key1);

        GuidVersionedOnHolder key2 = client.createSurvey(new TestSurvey());
        keys.add(key2);
        key2 = client.versionSurvey(key2);
        keys.add(key2);
        key2 = client.versionSurvey(key2);
        keys.add(key2);

        List<Survey> recentSurveys = client.getRecentVersionsOfAllSurveys();
        assertTrue("Recent versions of surveys exist in recentSurveys.", containsAll(recentSurveys, key, key1, key2));

        client.publishSurvey(key);
        client.publishSurvey(key2);
        List<Survey> publishedSurveys = client.getPublishedVersionsOfAllSurveys();
        assertTrue("Published surveys contain recently published.", containsAll(publishedSurveys, key, key2));
    }

    @Test
    public void canUpdateASurveyAndTypesAreCorrect() {
        ResearcherClient client = researcher.getSession().getResearcherClient();
        
        GuidVersionedOnHolder key = client.createSurvey(new TestSurvey());
        keys.add(key);
        Survey survey = client.getSurvey(key.getGuid(), key.getVersionedOn());
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
        survey = client.getSurvey(survey.getGuid(), survey.getVersionedOn());
        assertEquals("Name should have changed.", survey.getName(), "New name");
    }

    @Test
    public void dateBasedConstraintsPersistedCorrectly() {
        ResearcherClient client = researcher.getSession().getResearcherClient();
        
        GuidVersionedOnHolder key = client.createSurvey(new TestSurvey());
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
        GuidVersionedOnHolder key = client.createSurvey(new TestSurvey());
        keys.add(key);
        
        UserClient userClient = user.getSession().getUserClient();
        userClient.getSurvey(key);
        fail("Should not get here.");
    }

    private Constraints getConstraints(Survey survey, String id) {
        return survey.getQuestionByIdentifier(id).getConstraints();
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
