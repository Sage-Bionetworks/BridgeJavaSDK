package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.ResearcherClient;
import org.sagebionetworks.bridge.sdk.TestSurvey;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.models.GuidHolder;
import org.sagebionetworks.bridge.sdk.models.GuidVersionedOnHolder;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyAnswer;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestion;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyResponse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SurveyResponseTest {

    private TestUser researcher;
    private TestUser user;
    
    private Survey survey;
    private GuidVersionedOnHolder keys;
    
    @Before
    public void before() {
        researcher = TestUserHelper.createAndSignInUser(SurveyResponseTest.class, true, Tests.RESEARCHER_ROLE); 
        user = TestUserHelper.createAndSignInUser(SurveyResponseTest.class, true);

        ResearcherClient client = researcher.getSession().getResearcherClient();
        TestSurvey testSurvey = new TestSurvey();
        keys = client.createSurvey(testSurvey);
        client.publishSurvey(keys);
        
        // It's unfortunate but all of the questions have been assigned GUIDs so we need to 
        // retrieve the whole thing here in order to submit answers. The API should return
        // all the guids for the questions.
        survey = client.getSurvey(keys.getGuid(), keys.getVersionedOn());
    }

    @After
    public void after() {
        try {
            ResearcherClient client = researcher.getSession().getResearcherClient();
            client.closeSurvey(survey);
            client.deleteSurvey(survey);
        } finally {
            researcher.signOutAndDeleteUser();
            user.signOutAndDeleteUser();
        }
    }

    @Test
    public void submitAnswersColdForASurvey() {
        UserClient client = user.getSession().getUserClient();
        
        SurveyQuestion question1 = survey.getQuestionByIdentifier("high_bp");
        SurveyQuestion question2 = survey.getQuestionByIdentifier("BP X DAY");

        List<SurveyAnswer> answers = Lists.newArrayList();
        
        SurveyAnswer answer = question1.createAnswerForQuestion("true", "desktop");
        answers.add(answer);
        
        answer = question2.createAnswerForQuestion("4", "desktop");
        answers.add(answer);

        GuidHolder keys = client.submitAnswersToSurvey(survey, answers);

        SurveyResponse surveyResponse = client.getSurveyResponse(keys.getGuid());
        assertEquals("There should be two answers.", surveyResponse.getSurveyAnswers().size(), 2);
    }

    @Test
    public void canSubmitEveryKindOfAnswerType() {
        List<SurveyAnswer> answers = Lists.newArrayList();
        
        DateTime date = DateTime.parse("2014-10-30T18:33:36.081Z");
        Map<String,String> values = Maps.newHashMap();
        values.put("high_bp", "true");
        values.put("last_reading", date.toString(ISODateTimeFormat.dateTime()));
        values.put("last_checkup", date.toString(ISODateTimeFormat.date()));
        values.put("phone_number", "123-456-7890");
        values.put("deleuterium_dosage", "4.6");
        values.put("BP X DAY", "4");
        values.put("deleuterium_x_day", date.toString(ISODateTimeFormat.hourMinuteSecond()));
        values.put("time_for_appt", "PT4H");
        values.put("feeling", "[see array]"); // It's an array.
        
        SurveyQuestion question = survey.getQuestionByIdentifier("high_bp"); // boolean
        SurveyAnswer answer = question.createAnswerForQuestion(values.get("high_bp"), "mobile");
        answers.add(answer);

        question = survey.getQuestionByIdentifier("last_reading"); // datetime
        answer = question.createAnswerForQuestion(values.get("last_reading"), "mobile");
        answers.add(answer);

        question = survey.getQuestionByIdentifier("last_checkup"); // date
        answer = question.createAnswerForQuestion(values.get("last_checkup"), "mobile");
        answers.add(answer);
                
        question = survey.getQuestionByIdentifier("phone_number"); // string
        answer = question.createAnswerForQuestion(values.get("phone_number"), "mobile");
        answers.add(answer);

        question = survey.getQuestionByIdentifier("deleuterium_dosage"); // decimal
        answer = question.createAnswerForQuestion(values.get("deleuterium_dosage"), "mobile");
        answers.add(answer);
        
        question = survey.getQuestionByIdentifier("BP X DAY"); // integer
        answer = question.createAnswerForQuestion(values.get("BP X DAY"), "mobile");
        answers.add(answer);

        question = survey.getQuestionByIdentifier("deleuterium_x_day"); // time
        answer = question.createAnswerForQuestion(values.get("deleuterium_x_day"), "mobile");
        answers.add(answer);
        
        question = survey.getQuestionByIdentifier("time_for_appt"); // duration
        answer = question.createAnswerForQuestion(values.get("time_for_appt"), "mobile");
        answers.add(answer);
        
        question = survey.getQuestionByIdentifier("feeling"); // duration
        answer = question.createAnswerForQuestion(Lists.newArrayList("1", "3"), "mobile");
        answers.add(answer);

        UserClient client = user.getSession().getUserClient();
        survey = client.getSurvey(keys);
        GuidHolder holder = client.submitAnswersToSurvey(survey, answers);
        
        SurveyResponse response = client.getSurveyResponse(holder.getGuid());
        
        for (SurveyAnswer savedAnswer : response.getSurveyAnswers()) {
            SurveyQuestion q = survey.getQuestionByGUID(savedAnswer.getQuestionGuid());
            String originalValue = values.get(q.getIdentifier());
            if ("[see array]".equals(originalValue)) {
                assertEquals("Answers are correct", Lists.newArrayList("1", "3"), savedAnswer.getAnswers());
            } else {
                assertEquals("Answer is correct", originalValue, savedAnswer.getAnswer());    
            }
        }
    }
    
}
