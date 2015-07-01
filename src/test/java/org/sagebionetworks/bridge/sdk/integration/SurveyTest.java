package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
import org.sagebionetworks.bridge.sdk.DeveloperClient;
import org.sagebionetworks.bridge.sdk.Roles;
import org.sagebionetworks.bridge.sdk.TestSurvey;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.exceptions.PublishedSurveyException;
import org.sagebionetworks.bridge.sdk.exceptions.UnauthorizedException;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.surveys.Constraints;
import org.sagebionetworks.bridge.sdk.models.surveys.DataType;
import org.sagebionetworks.bridge.sdk.models.surveys.DateTimeConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.Image;
import org.sagebionetworks.bridge.sdk.models.surveys.StringConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyElement;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyInfoScreen;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestion;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestionOption;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyRule;
import org.sagebionetworks.bridge.sdk.models.surveys.UiHint;

public class SurveyTest {
    
    private TestUser developer;
    private TestUser user;

    @Before
    public void before() {
        developer = TestUserHelper.createAndSignInUser(SurveyTest.class, true, Roles.DEVELOPER);
        user = TestUserHelper.createAndSignInUser(SurveyTest.class, true);
    }

    @After
    public void after() {
        try {
            user.signOutAndDeleteUser();
            DeveloperClient client = developer.getSession().getDeveloperClient();
            deleteAllSurveysInStudy(client);
            assertEquals("Should be no surveys.", 0, client.getAllSurveysMostRecent().getTotal());
        } finally {
            developer.signOutAndDeleteUser();
        }
    }

    private void deleteAllSurveysInStudy(DeveloperClient client) {
        for (Survey survey : client.getAllSurveysMostRecent()) {
            for (Survey revision : client.getSurveyAllRevisions(survey.getGuid())) {
                client.deleteSurvey(revision);
            }
        }
    }
    
    @Test(expected=UnauthorizedException.class)
    public void cannotSubmitAsNormalUser() {
        user.getSession().getDeveloperClient().getAllSurveysMostRecent();
    }

    @Test
    public void saveAndRetrieveSurvey() {
        DeveloperClient client = developer.getSession().getDeveloperClient();
        GuidCreatedOnVersionHolder key = client.createSurvey(TestSurvey.getSurvey());
        Survey survey = client.getSurvey(key);

        List<SurveyElement> questions = survey.getElements();
        String prompt = ((SurveyQuestion)questions.get(1)).getPrompt();
        assertEquals("Prompt is correct.", "When did you last have a medical check-up?", prompt);
        client.publishSurvey(key);
        
        UserClient userClient = user.getSession().getUserClient();
        survey = userClient.getSurveyMostRecentlyPublished(key.getGuid());
        // And again, correct
        questions = survey.getElements();
        prompt = ((SurveyQuestion)questions.get(1)).getPrompt();
        assertEquals("Prompt is correct.", "When did you last have a medical check-up?", prompt);
    }

    @Test
    public void createVersionPublish() {
        DeveloperClient client = developer.getSession().getDeveloperClient();

        Survey survey = TestSurvey.getSurvey();
        assertNull(survey.getGuid());
        assertNull(survey.getVersion());
        assertNull(survey.getCreatedOn());
        GuidCreatedOnVersionHolder key = client.createSurvey(survey);
        assertNotNull(survey.getGuid());
        assertNotNull(survey.getVersion());
        assertNotNull(survey.getCreatedOn());
        
        GuidCreatedOnVersionHolder laterKey = client.versionSurvey(key);
        assertNotEquals("Version has been updated.", key.getCreatedOn(), laterKey.getCreatedOn());

        survey = client.getSurvey(laterKey.getGuid(), laterKey.getCreatedOn());
        assertFalse("survey is not published.", survey.isPublished());

        client.publishSurvey(survey);
        survey = client.getSurvey(survey.getGuid(), survey.getCreatedOn());
        assertTrue("survey is now published.", survey.isPublished());
    }

    @Test
    public void getAllVersionsOfASurvey() {
        DeveloperClient client = developer.getSession().getDeveloperClient();

        GuidCreatedOnVersionHolder key = client.createSurvey(TestSurvey.getSurvey());
        key = client.versionSurvey(key);

        int count = client.getSurveyAllRevisions(key.getGuid()).getTotal();
        assertEquals("Two versions for this survey.", 2, count);
    }

    @Test
    public void canGetMostRecentOrRecentlyPublishedSurvey() {
        DeveloperClient client = developer.getSession().getDeveloperClient();

        GuidCreatedOnVersionHolder key = client.createSurvey(TestSurvey.getSurvey());
        key = client.versionSurvey(key);
        key = client.versionSurvey(key);

        GuidCreatedOnVersionHolder key1 = client.createSurvey(TestSurvey.getSurvey());
        key1 = client.versionSurvey(key1);
        key1 = client.versionSurvey(key1);

        GuidCreatedOnVersionHolder key2 = client.createSurvey(TestSurvey.getSurvey());
        key2 = client.versionSurvey(key2);
        key2 = client.versionSurvey(key2);

        ResourceList<Survey> recentSurveys = client.getAllSurveysMostRecent();
        assertTrue("Recent versions of surveys exist in recentSurveys.", containsAll(recentSurveys.getItems(), key, key1, key2));

        client.publishSurvey(key);
        client.publishSurvey(key2);
        ResourceList<Survey> publishedSurveys = client.getAllSurveysMostRecent();
        assertTrue("Published surveys contain recently published.", containsAll(publishedSurveys.getItems(), key, key2));
    }

    @Test
    public void canUpdateASurveyAndTypesAreCorrect() {
        DeveloperClient client = developer.getSession().getDeveloperClient();

        GuidCreatedOnVersionHolder key = client.createSurvey(TestSurvey.getSurvey());
        Survey survey = client.getSurvey(key.getGuid(), key.getCreatedOn());
        assertEquals("Type is Survey.", survey.getClass(), Survey.class);

        List<SurveyElement> questions = survey.getElements();
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
        GuidCreatedOnVersionHolder holder = client.updateSurvey(survey);
        // These should be updated.
        assertEquals(holder.getVersion(), survey.getVersion());
        
        survey = client.getSurvey(survey.getGuid(), survey.getCreatedOn());
        assertEquals("Name should have changed.", survey.getName(), "New name");
    }

    @Test
    public void dateBasedConstraintsPersistedCorrectly() {
        DeveloperClient client = developer.getSession().getDeveloperClient();

        GuidCreatedOnVersionHolder key = client.createSurvey(TestSurvey.getSurvey());
        Survey survey = client.getSurvey(key);

        DateTimeConstraints dateCon = (DateTimeConstraints)getConstraints(survey, DATETIME_ID);
        DateTime earliest = dateCon.getEarliestValue();
        DateTime latest = dateCon.getLatestValue();
        assertNotNull("Earliest has been set", earliest);
        assertEquals("Date is correct", DateTime.parse("2000-01-01").withZone(DateTimeZone.UTC), earliest);
        assertNotNull("Latest has been set", latest);
        assertEquals("Date is correct", DateTime.parse("2020-12-31").withZone(DateTimeZone.UTC), latest);
    }

    @Test
    public void researcherCannotUpdatePublishedSurvey() {
        DeveloperClient client = developer.getSession().getDeveloperClient();
        Survey survey = TestSurvey.getSurvey();
        GuidCreatedOnVersionHolder keys = client.createSurvey(survey);
        keys = client.publishSurvey(keys);
        survey.setGuidCreatedOnVersionHolder(keys);
        
        survey.setName("This is a new name");

        try {
            client.updateSurvey(survey);
            fail("attempting to update a published survey should throw an exception.");
        } catch(PublishedSurveyException e) {
            assertEquals("PublishedSurveyException holds same guid as key used to delete Survey.",
                    keys.getGuid(), e.getGuid());

            // Need to getMillis because DateTimes aren't treated as equal if they exist in different time zones.
            assertEquals("PublishedSurveyException holds same createdOn as key used to delete Survey.",
                    keys.getCreatedOn().getMillis(), e.getCreatedOn().getMillis());
        }
    }

    @Test
    public void canGetMostRecentlyPublishedSurveyWithoutTimestamp() {
        DeveloperClient client = developer.getSession().getDeveloperClient();
        Survey survey = TestSurvey.getSurvey();

        GuidCreatedOnVersionHolder key = client.createSurvey(survey);

        GuidCreatedOnVersionHolder key1 = client.versionSurvey(key);
        GuidCreatedOnVersionHolder key2 = client.versionSurvey(key1);
        client.publishSurvey(key2);
        client.versionSurvey(key2);

        Survey found = client.getSurveyMostRecentlyPublished(key2.getGuid());
        assertEquals("This returns the right version", key2.getCreatedOn(), found.getCreatedOn());
        assertNotEquals("And these are really different versions", key.getCreatedOn(), found.getCreatedOn());
    }

    @Test
    public void canCallMultiOperationMethodToMakeSurveyUpdate() {
        DeveloperClient client = developer.getSession().getDeveloperClient();
        Survey survey = TestSurvey.getSurvey();

        GuidCreatedOnVersionHolder keys = client.createSurvey(survey);

        Survey existingSurvey = client.getSurvey(keys);
        existingSurvey.setName("This is an update test");

        GuidCreatedOnVersionHolder holder = client.versionUpdateAndPublishSurvey(existingSurvey, true);

        ResourceList<Survey> allRevisions = client.getSurveyAllRevisions(keys.getGuid());
        assertEquals("There are now two versions", 2, allRevisions.getTotal());

        Survey mostRecent = client.getSurveyMostRecentlyPublished(existingSurvey.getGuid());
        assertEquals(mostRecent.getGuid(), holder.getGuid());
        assertEquals(mostRecent.getCreatedOn(), holder.getCreatedOn());
        assertEquals(mostRecent.getVersion(), holder.getVersion());
        assertEquals("The latest has a new title", "This is an update test", allRevisions.get(0).getName());
    }
    
    @Test
    public void canRetrieveSurveyByIdentifier() {
        DeveloperClient client = developer.getSession().getDeveloperClient();
        
        Survey survey = TestSurvey.getSurvey();
        GuidCreatedOnVersionHolder keys = client.createSurvey(survey);
        client.publishSurvey(keys);
        
        client.getSurveyMostRecentlyPublishedByIdentifier(survey.getIdentifier());
    }
    
    @Test
    public void canSaveAndRetrieveInfoScreen() {
        DeveloperClient client = developer.getSession().getDeveloperClient();
        
        Survey survey = new Survey();
        survey.setIdentifier("test-survey");
        survey.setName("Test study");
        
        SurveyInfoScreen screen = new SurveyInfoScreen();
        screen.setIdentifier("foo");
        screen.setTitle("Title");
        screen.setPrompt("Prompt");
        screen.setPromptDetail("Prompt detail");
        Image image = new Image("https://pbs.twimg.com/profile_images/1642204340/ReferencePear_400x400.PNG", 400, 400);
        screen.setImage(image);
        survey.getElements().add(screen);
        
        // Add a question too just to verify that's okay
        SurveyQuestion question = new SurveyQuestion();
        question.setIdentifier("bar");
        question.setPrompt("Prompt");
        question.setFireEvent(true);
        question.setUiHint(UiHint.TEXTFIELD);
        question.setConstraints(new StringConstraints());
        survey.getElements().add(question);
        
        GuidCreatedOnVersionHolder keys = client.createSurvey(survey);
        
        Survey newSurvey = client.getSurvey(keys);
        assertEquals(2, newSurvey.getElements().size());
        
        SurveyInfoScreen newScreen = (SurveyInfoScreen)newSurvey.getElements().get(0);
        
        assertEquals(SurveyInfoScreen.class, newScreen.getClass());
        assertNotNull(newScreen.getGuid());
        assertEquals("foo", newScreen.getIdentifier());
        assertEquals("Title", newScreen.getTitle());
        assertEquals("Prompt", newScreen.getPrompt());
        assertEquals("Prompt detail", newScreen.getPromptDetail());
        assertEquals("https://pbs.twimg.com/profile_images/1642204340/ReferencePear_400x400.PNG", newScreen.getImage().getSource());
        assertEquals(400, newScreen.getImage().getWidth());
        assertEquals(400, newScreen.getImage().getHeight());
        
        SurveyQuestion newQuestion = (SurveyQuestion)newSurvey.getElements().get(1);
        assertEquals(SurveyQuestion.class, newQuestion.getClass());
        assertNotNull(newQuestion.getGuid());
        assertEquals("bar", newQuestion.getIdentifier());
        assertEquals("Prompt", newQuestion.getPrompt());
        assertEquals(true, newQuestion.getFireEvent());
        assertEquals(UiHint.TEXTFIELD, newQuestion.getUIHint());
    }

    private Constraints getConstraints(Survey survey, String id) {
        return ((SurveyQuestion)survey.getElementByIdentifier(id)).getConstraints();
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
