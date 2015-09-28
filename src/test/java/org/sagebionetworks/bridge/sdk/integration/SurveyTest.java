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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sagebionetworks.bridge.sdk.AdminClient;
import org.sagebionetworks.bridge.sdk.DeveloperClient;
import org.sagebionetworks.bridge.sdk.Roles;
import org.sagebionetworks.bridge.sdk.Session;
import org.sagebionetworks.bridge.sdk.TestSurvey;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.exceptions.PublishedSurveyException;
import org.sagebionetworks.bridge.sdk.exceptions.UnauthorizedException;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleGuidCreatedOnVersionHolder;
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
    private static final Logger LOG = LoggerFactory.getLogger(SurveyTest.class);

    private static TestUser developer;
    private static TestUser user;

    // We use SimpleGuidCreatedOnVersionHolder, because we need to use an immutable version holder, to ensure we're
    // deleting the correct surveys.
    private Set<SimpleGuidCreatedOnVersionHolder> surveysToDelete;

    @BeforeClass
    public static void beforeClass() {
        developer = TestUserHelper.createAndSignInUser(UploadSchemaTest.class, false, Roles.DEVELOPER);
        user = TestUserHelper.createAndSignInUser(UploadSchemaTest.class, true);
    }

    @Before
    public void before() {
        // Init surveys to delete set. It's not clear whether JUnit will re-init member vars between each method, so we
        // do it here just to be clear.
        surveysToDelete = new HashSet<>();
    }

    @After
    public void after() {
        // cleanup surveys
        Session session = TestUserHelper.getSignedInAdmin().getSession();
        AdminClient adminClient = session.getAdminClient();
        for (SimpleGuidCreatedOnVersionHolder oneSurvey : surveysToDelete) {
            try {
                adminClient.deleteSurveyPermanently(oneSurvey);
            } catch (RuntimeException ex) {
                LOG.error("Error deleting survey=" + oneSurvey + ": " + ex.getMessage(), ex);
            }
        }
    }

    @AfterClass
    public static void deleteDeveloper() {
        if (developer != null) {
            developer.signOutAndDeleteUser();
        }
    }

    @AfterClass
    public static void deleteUser() {
        if (user != null) {
            user.signOutAndDeleteUser();
        }
    }

    @Test(expected=UnauthorizedException.class)
    public void cannotSubmitAsNormalUser() {
        user.getSession().getDeveloperClient().getAllSurveysMostRecent();
    }

    @Test
    public void saveAndRetrieveSurvey() {
        DeveloperClient client = developer.getSession().getDeveloperClient();
        GuidCreatedOnVersionHolder key = createSurvey(client, TestSurvey.getSurvey());
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
        GuidCreatedOnVersionHolder key = createSurvey(client, survey);
        assertNotNull(survey.getGuid());
        assertNotNull(survey.getVersion());
        assertNotNull(survey.getCreatedOn());
        
        GuidCreatedOnVersionHolder laterKey = versionSurvey(client, key);
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

        GuidCreatedOnVersionHolder key = createSurvey(client, TestSurvey.getSurvey());
        key = versionSurvey(client, key);

        int count = client.getSurveyAllRevisions(key.getGuid()).getTotal();
        assertEquals("Two versions for this survey.", 2, count);
    }

    @Test
    public void canGetMostRecentOrRecentlyPublishedSurvey() throws InterruptedException {
        DeveloperClient client = developer.getSession().getDeveloperClient();

        GuidCreatedOnVersionHolder key = createSurvey(client, TestSurvey.getSurvey());
        key = versionSurvey(client, key);
        key = versionSurvey(client, key);

        GuidCreatedOnVersionHolder key1 = createSurvey(client, TestSurvey.getSurvey());
        key1 = versionSurvey(client, key1);
        key1 = versionSurvey(client, key1);

        GuidCreatedOnVersionHolder key2 = createSurvey(client, TestSurvey.getSurvey());
        key2 = versionSurvey(client, key2);
        key2 = versionSurvey(client, key2);

        ResourceList<Survey> recentSurveys = client.getAllSurveysMostRecent();
        containsAll(recentSurveys.getItems(), key, key1, key2);

        key = client.publishSurvey(key);
        key2 = client.publishSurvey(key2);
        ResourceList<Survey> publishedSurveys = client.getAllSurveysMostRecentlyPublished();
        containsAll(publishedSurveys.getItems(), key, key2);
    }

    @Test
    public void canUpdateASurveyAndTypesAreCorrect() {
        DeveloperClient client = developer.getSession().getDeveloperClient();

        GuidCreatedOnVersionHolder key = createSurvey(client, TestSurvey.getSurvey());
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

        GuidCreatedOnVersionHolder key = createSurvey(client, TestSurvey.getSurvey());
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
        GuidCreatedOnVersionHolder keys = createSurvey(client, survey);
        keys = client.publishSurvey(keys);
        survey.setGuidCreatedOnVersionHolder(keys);
        
        survey.setName("This is a new name");

        try {
            client.updateSurvey(survey);
            fail("attempting to update a published survey should throw an exception.");
        } catch(PublishedSurveyException e) {
        }
    }

    @Test
    public void canGetMostRecentlyPublishedSurveyWithoutTimestamp() {
        DeveloperClient client = developer.getSession().getDeveloperClient();
        Survey survey = TestSurvey.getSurvey();

        GuidCreatedOnVersionHolder key = createSurvey(client, survey);

        GuidCreatedOnVersionHolder key1 = versionSurvey(client, key);
        GuidCreatedOnVersionHolder key2 = versionSurvey(client, key1);
        client.publishSurvey(key2);
        versionSurvey(client, key2);

        Survey found = client.getSurveyMostRecentlyPublished(key2.getGuid());
        assertEquals("This returns the right version", key2.getCreatedOn(), found.getCreatedOn());
        assertNotEquals("And these are really different versions", key.getCreatedOn(), found.getCreatedOn());
    }

    @Test
    public void canCallMultiOperationMethodToMakeSurveyUpdate() {
        DeveloperClient client = developer.getSession().getDeveloperClient();
        Survey survey = TestSurvey.getSurvey();

        GuidCreatedOnVersionHolder keys = createSurvey(client, survey);

        Survey existingSurvey = client.getSurvey(keys);
        existingSurvey.setName("This is an update test");

        GuidCreatedOnVersionHolder holder = client.versionUpdateAndPublishSurvey(existingSurvey, true);
        surveysToDelete.add(new SimpleGuidCreatedOnVersionHolder(holder));

        ResourceList<Survey> allRevisions = client.getSurveyAllRevisions(keys.getGuid());
        assertEquals("There are now two versions", 2, allRevisions.getTotal());

        Survey mostRecent = client.getSurveyMostRecentlyPublished(existingSurvey.getGuid());
        assertEquals(mostRecent.getGuid(), holder.getGuid());
        assertEquals(mostRecent.getCreatedOn(), holder.getCreatedOn());
        assertEquals(mostRecent.getVersion(), holder.getVersion());
        assertEquals("The latest has a new title", "This is an update test", allRevisions.get(0).getName());
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
        
        GuidCreatedOnVersionHolder keys = createSurvey(client, survey);
        
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

    private void containsAll(List<Survey> surveys, GuidCreatedOnVersionHolder... keys) throws InterruptedException {
            Thread.sleep(2000);

        // The server may have more surveys than the ones we created, if more than one person is running tests
        // (unit or integration), or if there are persistent tests unrelated to this test.
        assertTrue("Server returned enough items", surveys.size() >= keys.length);

        // Check that we can find all of our expected surveys. Use the immutable Simple holder, so we can use a
        // set and contains().
        Set<SimpleGuidCreatedOnVersionHolder> surveyKeySet = new HashSet<>();
        for (Survey survey : surveys) {
            surveyKeySet.add(new SimpleGuidCreatedOnVersionHolder(survey));
        }
        for (GuidCreatedOnVersionHolder key : keys) {
            SimpleGuidCreatedOnVersionHolder simpleKey = new SimpleGuidCreatedOnVersionHolder(key);
            assertTrue("Survey contains expected key: " + simpleKey, surveyKeySet.contains(simpleKey));
        }
    }

    // Helper methods to ensure we always record these calls for cleanup

    private GuidCreatedOnVersionHolder createSurvey(DeveloperClient devClient, Survey survey) {
        GuidCreatedOnVersionHolder versionHolder = devClient.createSurvey(survey);
        surveysToDelete.add(new SimpleGuidCreatedOnVersionHolder(versionHolder));
        return versionHolder;
    }

    private GuidCreatedOnVersionHolder versionSurvey(DeveloperClient devClient, GuidCreatedOnVersionHolder survey) {
        GuidCreatedOnVersionHolder versionHolder = devClient.versionSurvey(survey);
        surveysToDelete.add(new SimpleGuidCreatedOnVersionHolder(versionHolder));
        return versionHolder;
    }
}
