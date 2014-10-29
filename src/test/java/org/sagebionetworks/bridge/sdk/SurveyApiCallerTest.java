package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.models.GuidVersionedOnHolder;
import org.sagebionetworks.bridge.sdk.models.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.surveys.BooleanConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.DataType;
import org.sagebionetworks.bridge.sdk.models.surveys.DateConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.DateTimeConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.DecimalConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.DurationConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.Image;
import org.sagebionetworks.bridge.sdk.models.surveys.IntegerConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.MultiValueConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.StringConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestion;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestionOption;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyRule;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyRule.Operator;
import org.sagebionetworks.bridge.sdk.models.surveys.TimeConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.UiHint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

public class SurveyApiCallerTest {

    private static ClientProvider provider;
    private static SurveyApiCaller surveyApi;
    private static UserManagementApiCaller userManagementApi;
    private static SignInCredentials user;
    private static SignInCredentials admin;

    @BeforeClass
    public static void initialSetup() {
        provider = ClientProvider.valueOf();
        surveyApi= SurveyApiCaller.valueOf(provider);
        userManagementApi = UserManagementApiCaller.valueOf(provider);

        // create admin sign in
        String adminEmail = provider.getConfig().getAdminEmail();
        String adminPassword = provider.getConfig().getAdminPassword();
        admin = SignInCredentials.valueOf().setUsername(adminEmail).setPassword(adminPassword);

        provider.signIn(admin);

        // create user and user sign in
        String userEmail = "testtest@sagebase.org";
        String userPassword = "password";
        String username= "test";
        boolean consent = true;
        userManagementApi.createUser(userEmail, username, userPassword, consent);
        user = SignInCredentials.valueOf().setUsername(userEmail).setPassword(userPassword);
    }

    @AfterClass
    public static void finalTeardown() {
        userManagementApi.deleteUser(user.getUsername());
    }

    @Before
    public void before() {
        List<Survey> surveys = surveyApi.getAllVersionsOfAllSurveys();
        for (Survey survey : surveys) {
            surveyApi.closeSurvey(survey.getGuid(), survey.getVersionedOn());
        }
    }

    @After
    public void after() {
        provider.signOut();
        provider.signIn(admin);
    }

    @Test
    public void cannotSubmitAsNormalUser() {
        provider.signOut();
        provider.signIn(user);
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

        provider.signOut();
        provider.signIn(user);
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

    private boolean containsAll(List<Survey> surveys, GuidVersionedOnHolder key, GuidVersionedOnHolder key1, GuidVersionedOnHolder key2) {
        int count = 0;
        for (Survey survey : surveys) {
            boolean guidsEqual = survey.getGuid().equals(key.getGuid());
            boolean guidsEqual1 = survey.getGuid().equals(key1.getGuid());
            boolean guidsEqual2 = survey.getGuid().equals(key2.getGuid());

            boolean versionsEqual = survey.getVersionedOn().equals(key.getVersionedOn());
            boolean versionsEqual1 = survey.getVersionedOn().equals(key1.getVersionedOn());
            boolean versionsEqual2 = survey.getVersionedOn().equals(key2.getVersionedOn());

            if (guidsEqual && versionsEqual) { count++; }
            if (guidsEqual1 && versionsEqual1) { count++; }
            if (guidsEqual2 && versionsEqual2) { count++; }
        }
        return count == 3;
    }

    public class TestSurvey {

        private final Survey survey;

        private SurveyQuestion multiValueQuestion = new SurveyQuestion() {
            {
                Image terrible = new Image("http://terrible.svg", 600, 300);
                Image poor = new Image("http://poor.svg", 600, 300);
                Image ok = new Image("http://ok.svg", 600, 300);
                Image good = new Image("http://good.svg", 600, 300);
                Image great = new Image("http://great.svg", 600, 300);
                MultiValueConstraints mvc = new MultiValueConstraints(DataType.INTEGER);
                List<SurveyQuestionOption> options = Lists.newArrayList(
                    new SurveyQuestionOption("Terrible", "1", terrible),
                    new SurveyQuestionOption("Poor", "2", poor),
                    new SurveyQuestionOption("OK", "3", ok),
                    new SurveyQuestionOption("Good", "4", good),
                    new SurveyQuestionOption("Great", "5", great)
                );
                mvc.setEnumeration(options);
                mvc.setAllowOther(false);
                mvc.setAllowMultiple(true);
                setConstraints(mvc);
                setPrompt("How do you feel today?");
                setIdentifier("feeling");
                setUiHint(UiHint.LIST);
                setGuid(UUID.randomUUID().toString());
            }
        };

        private SurveyQuestion stringQuestion = new SurveyQuestion() {
            {
                StringConstraints c = new StringConstraints();
                c.setMinLength(2);
                c.setMaxLength(255);
                c.setPattern("\\d{3}-\\d{3}-\\d{4}");
                setPrompt("Please enter an emergency phone number (###-###-####)?");
                setIdentifier("name");
                setUiHint(UiHint.TEXTFIELD);
                setConstraints(c);
                setGuid(UUID.randomUUID().toString());
            }
        };

        private SurveyQuestion booleanQuestion = new SurveyQuestion() {
            {
                BooleanConstraints c = new BooleanConstraints();
                setPrompt("Do you have high blood pressure?");
                setIdentifier("high_bp");
                setUiHint(UiHint.CHECKBOX);
                setConstraints(c);
                setGuid(UUID.randomUUID().toString());
            }
        };

        private SurveyQuestion dateQuestion = new SurveyQuestion() {
            {
                DateConstraints c = new DateConstraints();
                setPrompt("When did you last have a medical check-up?");
                setIdentifier("last_checkup");
                setUiHint(UiHint.DATEPICKER);
                setConstraints(c);
                setGuid(UUID.randomUUID().toString());
            }
        };

        private SurveyQuestion dateTimeQuestion = new SurveyQuestion() {
            {
                DateTimeConstraints c = new DateTimeConstraints();
                c.setAllowFuture(true);
                setPrompt("When is your next medical check-up scheduled?");
                setIdentifier("last_reading");
                setUiHint(UiHint.DATETIMEPICKER);
                setConstraints(c);
                setGuid(UUID.randomUUID().toString());
            }
        };

        private SurveyQuestion decimalQuestion = new SurveyQuestion() {
            {
                DecimalConstraints c = new DecimalConstraints();
                c.setMinValue(0.0d);
                c.setMaxValue(10.0d);
                c.setStep(0.1d);
                setPrompt("What dosage (in grams) do you take of deleuterium each day?");
                setIdentifier("deleuterium_dosage");
                setUiHint(UiHint.SLIDER);
                setConstraints(c);
                setGuid(UUID.randomUUID().toString());
            }
        };

        private SurveyQuestion durationQuestion = new SurveyQuestion() {
            {
                DurationConstraints c = new DurationConstraints();
                setPrompt("How log does your appointment take, on average?");
                setIdentifier("time_for_appt");
                setUiHint(UiHint.TIMEPICKER);
                setConstraints(c);
                setGuid(UUID.randomUUID().toString());
            }
        };

        private SurveyQuestion integerQuestion = new SurveyQuestion() {
            {
                IntegerConstraints c = new IntegerConstraints();
                c.setMinValue(0L);
                c.setMaxValue(4L);
                c.getRules().add(new SurveyRule(Operator.LE, 2, "name"));
                c.getRules().add(new SurveyRule(Operator.DE, null, "name"));

                setPrompt("How many times a day do you take your blood pressure?");
                setIdentifier("bp_x_day");
                setUiHint(UiHint.NUMBERFIELD);
                setConstraints(c);
                setGuid(UUID.randomUUID().toString());
            }
        };

        private SurveyQuestion timeQuestion = new SurveyQuestion() {
            {
                TimeConstraints c = new TimeConstraints();
                setPrompt("What times of the day do you take deleuterium?");
                setIdentifier("deleuterium_x_day");
                setUiHint(UiHint.TIMEPICKER);
                setConstraints(c);
                setGuid(UUID.randomUUID().toString());
            }
        };

        public TestSurvey() {
            String guid = null;
            String name = "General Blood Pressure Survey";
            String identifier = "bloodpressure";
            DateTime modifiedOn = null;
            DateTime versionedOn = null;
            Long version = null;
            boolean published = true;
            List<SurveyQuestion> questions = Lists.newArrayList();
            questions.add(booleanQuestion);
            questions.add(dateQuestion);
            questions.add(dateTimeQuestion);
            questions.add(decimalQuestion);
            questions.add(integerQuestion);
            questions.add(durationQuestion);
            questions.add(timeQuestion);
            questions.add(multiValueQuestion);
            questions.add(stringQuestion);

            survey = Survey.valueOf(guid, versionedOn, modifiedOn, version, name, identifier, published, questions);
        }

        public Survey getSurvey() { return survey; }
        public String getGuid() { return survey.getGuid(); }
        public DateTime getVersionedOn() { return survey.getVersionedOn(); }
        public DateTime getModifiedOn() { return survey.getModifiedOn(); }
        public String getName() { return survey.getName(); }
        public String getIdentifier() { return survey.getIdentifier(); }
        public boolean isPublished() { return survey.isPublished(); }
        public List<SurveyQuestion> getQuestions() { return survey.getQuestions(); }

        public SurveyQuestion getBooleanQuestion() {
            return booleanQuestion;
        }

        public SurveyQuestion getDateQuestion() {
            return dateQuestion;
        }

        public SurveyQuestion getDateTimeQuestion() {
            return dateTimeQuestion;
        }

        public SurveyQuestion getDecimalQuestion() {
            return decimalQuestion;
        }

        public SurveyQuestion getIntegerQuestion() {
            return integerQuestion;
        }

        public SurveyQuestion getDurationQuestion() {
            return durationQuestion;
        }

        public SurveyQuestion getTimeQuestion() {
            return timeQuestion;
        }

        public SurveyQuestion getMultiValueQuestion() {
            return multiValueQuestion;
        }

        public SurveyQuestion getStringQuestion() {
            return stringQuestion;
        }

        public String toJSON() throws Exception {
            return new ObjectMapper().writeValueAsString(this);
        }

    }
}
