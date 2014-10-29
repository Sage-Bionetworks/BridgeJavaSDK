package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.ResearcherClient;
import org.sagebionetworks.bridge.sdk.Session;
import org.sagebionetworks.bridge.sdk.TestSurvey;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.models.GuidHolder;
import org.sagebionetworks.bridge.sdk.models.surveys.MultiValueConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyAnswer;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestion;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyResponse;

import com.google.common.collect.Lists;

public class SurveyResponseTest {

    private Session session;
    private UserClient user;
    private ResearcherClient researcher;

    private TestSurvey testSurvey;
    private Survey survey;

    @Before
    public void before() {
        Config config = ClientProvider.getConfig();
        session = ClientProvider.signIn(config.getAdminCredentials());
        user = session.getUserClient();
        researcher = session.getResearcherClient();

        testSurvey = new TestSurvey();
        survey = testSurvey.getSurvey();
        researcher.createSurvey(survey);
    }

    @After
    public void after() {
        researcher.closeSurvey(survey.getGuid(), survey.getVersionedOn());
        session.signOut();
    }

    @Test
    public void submitAnswersColdForASurvey() {
        SurveyQuestion question1 = testSurvey.getBooleanQuestion();
        SurveyQuestion question2 = testSurvey.getIntegerQuestion();

        List<SurveyAnswer> answers = Lists.newArrayList();
        String questionGuid = question1.getGuid();
        String answerField = "true";
        String client = "test";
        DateTime answeredOn = DateTime.now();
        SurveyAnswer answer1 = SurveyAnswer.valueOf(questionGuid, false, answerField, answeredOn, client);
        answers.add(answer1);

        questionGuid = question2.getGuid();
        answerField = null;
        client = "test";
        answeredOn = DateTime.now();
        SurveyAnswer answer2 = SurveyAnswer.valueOf(questionGuid, false, answerField, answeredOn, client);
        answers.add(answer2);

        GuidHolder key = user.submitAnswersToSurvey(survey, answers);

        SurveyResponse surveyResponse = user.getSurveyResponse(key.getGuid());
        assertEquals("There should be two answers.", surveyResponse.getSurveyAnswers().size(), 2);
    }

    @Test
    public void canSubmitEveryKindOfAnswerType() {
        List<SurveyAnswer> answers = Lists.newArrayList();

        SurveyQuestion question = survey.getQuestions().get(0); // boolean
        String answerField = "true";
        DateTime answeredOn = DateTime.now();
        String client = "mobile";
        String questionGuid = question.getGuid();
        SurveyAnswer answer = SurveyAnswer.valueOf(questionGuid, false, answerField, answeredOn, client);
        answers.add(answer);

        question = survey.getQuestions().get(1); // datetime
        String answerField1 = DateTime.now().toString(ISODateTimeFormat.dateTime());
        DateTime answeredOn1 = DateTime.now();
        String client1 = "mobile";
        String questionGuid1 = question.getGuid();
        SurveyAnswer answer1 = SurveyAnswer.valueOf(questionGuid1, false, answerField1, answeredOn1, client1);
        answers.add(answer1);

        question = survey.getQuestions().get(2); // datetime
        String answerField2 = DateTime.now().toString(ISODateTimeFormat.dateTime());
        DateTime answeredOn2 = DateTime.now();
        String client2 = "mobile";
        String questionGuid2 = question.getGuid();
        SurveyAnswer answer2 = SurveyAnswer.valueOf(questionGuid2, false, answerField2, answeredOn2, client2);
        answers.add(answer2);

        question = survey.getQuestions().get(3); // decimal
        String answerField3 = "4.6";
        DateTime answeredOn3 = DateTime.now();
        String client3 = "mobile";
        String questionGuid3 = question.getGuid();
        SurveyAnswer answer3 = SurveyAnswer.valueOf(questionGuid3, false, answerField3, answeredOn3, client3);
        answers.add(answer3);

        question = survey.getQuestions().get(4); // integer
        String answerField4 = "4";
        DateTime answeredOn4 = DateTime.now();
        String client4 = "mobile";
        String questionGuid4 = question.getGuid();
        SurveyAnswer answer4 = SurveyAnswer.valueOf(questionGuid4, false, answerField4, answeredOn4, client4);
        answers.add(answer4);

        question = survey.getQuestions().get(5); // duration
        String answerField5 = "PTH";
        DateTime answeredOn5 = DateTime.now();
        String client5 = "mobile";
        String questionGuid5 = question.getGuid();
        SurveyAnswer answer5 = SurveyAnswer.valueOf(questionGuid5, false, answerField5, answeredOn5, client5);
        answers.add(answer5);

        question = survey.getQuestions().get(6); // time
        // "14:45:15.357Z" doesn't work because it has a time zone. They should be able
        // to enter a time zone if they want though, right? This is *too* restrictive.
        String answerField6 = "14:45:15.357";
        DateTime answeredOn6 = DateTime.now();
        String client6 = "mobile";
        String questionGuid6 = question.getGuid();
        SurveyAnswer answer6 = SurveyAnswer.valueOf(questionGuid6, false, answerField6, answeredOn6, client6);
        answers.add(answer6);

        question = survey.getQuestions().get(7); // multichoice integer
        assertTrue("Question is multichoice.", question.getConstraints() instanceof MultiValueConstraints);
        String answerField7 = Lists.<String>newArrayList("3").toArray().toString();
        DateTime answeredOn7 = DateTime.now();
        String client7 = "mobile";
        String questionGuid7 = question.getGuid();
        SurveyAnswer answer7 = SurveyAnswer.valueOf(questionGuid7, false, answerField7, answeredOn7, client7);
        answers.add(answer7);

        question = survey.getQuestions().get(8); // string
        String answerField8 = "123-456-7890";
        DateTime answeredOn8 = DateTime.now();
        String client8 = "mobile";
        String questionGuid8 = question.getGuid();
        SurveyAnswer answer8 = SurveyAnswer.valueOf(questionGuid8, false, answerField8, answeredOn8, client8);
        answers.add(answer8);

        // Submit all these tricky examples of answers through the API.
        user.submitAnswersToSurvey(survey, answers);
    }

}
