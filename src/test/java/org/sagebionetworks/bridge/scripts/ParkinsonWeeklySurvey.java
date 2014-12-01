package org.sagebionetworks.bridge.scripts;

import org.joda.time.Period;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.schedules.ScheduleType;
import org.sagebionetworks.bridge.sdk.models.surveys.DataType;
import org.sagebionetworks.bridge.sdk.models.surveys.IntegerConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.MultiValueConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestion;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestionOption;
import org.sagebionetworks.bridge.sdk.models.surveys.UiHint;

import com.google.common.collect.Lists;

public class ParkinsonWeeklySurvey extends Survey implements ScheduleHolder {

    SurveyQuestion feeling = new SurveyQuestion() {{
        setIdentifier("feeling");
        setPrompt("How good or bad is your health TODAY (0 means the worst health you can imagine, 100 means the best health you can imagine)?");
        setUiHint(UiHint.SLIDER);
        IntegerConstraints c = new IntegerConstraints();
        c.setMaxValue(100L);
        c.setMinValue(0L);
        setConstraints(c);
    }};
    
    SurveyQuestion strenuousExersize = new SurveyQuestion() {{
        setIdentifier("strenuous-exersize");
        setPrompt("Over the past week, how many times did you do the following kinds of exercise for more than 15 minutes? Strenuous exersize (heart beats rapidly):");
        setUiHint(UiHint.NUMBERFIELD);
        setConstraints(new IntegerConstraints());
    }};
    
    SurveyQuestion moderateExersize = new SurveyQuestion() {{
        setIdentifier("moderate-exersize");
        setPrompt("Over the past week, how many times did you do the following kinds of exercise for more than 15 minutes? Moderate exersize (not exhausting):");
        setUiHint(UiHint.NUMBERFIELD);
        setConstraints(new IntegerConstraints());
    }};
    
    SurveyQuestion mildExersize = new SurveyQuestion() {{
        setIdentifier("mild-exersize");
        setPrompt("Over the past week, how many times did you do the following kinds of exercise for more than 15 minutes? Minimal effort:");
        setUiHint(UiHint.NUMBERFIELD);
        setConstraints(new IntegerConstraints());
    }};
    
    SurveyQuestion leisure = new SurveyQuestion() {{
        setIdentifier("leisure-time");
        setPrompt("During your leisure time how often do you engage in any regular activity long enough to work up a sweat (heart beats rapidly)?");
        MultiValueConstraints c = new MultiValueConstraints(DataType.STRING);
        c.setEnumeration(Lists.newArrayList(
            new SurveyQuestionOption("Often"),
            new SurveyQuestionOption("Sometimes"),
            new SurveyQuestionOption("Never/Rarely")
        ));
        setUiHint(UiHint.RADIOBUTTON);
        setConstraints(c);
    }};
    
    SurveyQuestion memory = listQuestion("memory", "Over the past week have you had problems remembering things, following conversations, paying attention, thinking clearly, or finding your way around the house or in town?",
            "Normal: No cognitive impairment.", 
            "Slight: Impairment appreciated by patient or caregiver with no concrete interference with the patient’s ability to carry out normal activities and social interactions.", 
            "Mild: Clinically evident cognitive dysfunction, but only minimal interference with the patient’s ability to carry out normal activities and social interactions.", 
            "Moderate: Cognitive deficits interfere with but do not preclude the patient’s ability to carry out normal activities and social interactions.", 
            "Severe: Cognitive dysfunction precludes the patient’s ability to carry out normal activities and social interactions.");
    
    SurveyQuestion emotions = listQuestion("emotions", "Over the past week have you felt low, sad, hopeless or unable to enjoy things?",
            "Normal: No depressed mood.",
            "Slight: Episodes of depressed mood that are not sustained for more than one day at a time. No interference with patient’s ability to carry out normal activities and social interactions.",
            "Mild: Depressed mood that is sustained over days, but without interference with normal activities and social interactions.",
            "Moderate: Depressed mood that interferes with, but does not preclude, the patient’s ability to carry out normal activities and social interactions.",
            "Severe: Depressed mood precludes patient’s ability to carry out normal activities and social interactions.");
    
    SurveyQuestion anxiety = listQuestion("anxiety", "Over the past week have you felt nervous, worried or tense?",
            "Normal: No anxious feelings.",
            "Slight: Anxious feelings present but not sustained for more than one day at a time. No interference with patient’s ability to carry out normal activities and social interactions.",
            "Mild: Anxious feelings are sustained over more than one day at a time, but without interference with patient’s ability to carry out normal activities and social interactions.",
            "Moderate: Anxious feelings interfere with, but do not preclude, the patient’s ability to carry out normal activities and social interactions.",
            "Severe: Anxious feelings preclude patient’s ability to carry out normal activities and social interactions.");
    
    SurveyQuestion socialActivities = listQuestion("social-activities", "Over the past week, have you felt indifferent to doing activities or being with people?",
            "Normal: No apathy.",
            "Slight: Apathy appreciated by patient and/or caregiver, but no interference with daily activities and social interactions.",
            "Mild: Apathy interferes with isolated activities and social interactions.",
            "Moderate: Apathy interferes with most activities and social interactions.",
            "Severe: Passive and withdrawn, complete loss of initiative.");
    
    SurveyQuestion sleeping = listQuestion("sleeping", "Over the past week, have you had trouble going to sleep at night or staying asleep through the night? Consider how rested you felt after waking up in the morning?", 
            "Normal: No problems.", 
            "Slight: Sleep problems are present but usually do not cause trouble getting a full night of sleep.",  
            "Mild: Sleep problems usually cause some difficulties getting a full night of sleep.", 
            "Moderate: Sleep problems cause a lot of difficulties getting a full night of sleep, but I still usually sleep for more than half the night.", 
            "Severe: I usually do not sleep for most of the night.");
    
    SurveyQuestion wakefulness = listQuestion("wakefulness", "Over the past week, have you had trouble staying awake during the daytime?", 
            "Normal: No daytime sleepiness.", 
            "Slight: Daytime sleepiness occurs but I can resist and I stay awake.",  
            "Mild: Sometimes I fall asleep when alone and relaxing. For example, while reading or watching TV.", 
            "Moderate: I sometimes fall asleep when I should not. For example, while eating or talking with other people.", 
            "Severe: I often fall asleep when I should not. For example, while eating or talking with other people.");
            
    SurveyQuestion speech = listQuestion("speech", "Over the past week, have you had problems with your speech?",
            "Normal: Not at all (no problems).",
            "Slight: My speech is soft, slurred or uneven, but it does not cause others to ask me to repeat myself.",
            "Mild: My speech causes people to ask me to occasionally repeat myself, but not everyday.",
            "Moderate: My speech is unclear enough that others ask me to repeat myself every day even though most of my speech is understood.",
            "Severe: Most or all of my speech cannot be understood.");
    
    SurveyQuestion eating = listQuestion("eating", "Over the past week, have you usually had troubles handling your food and using eating utensils? For example, do you have trouble handling finger foods or using forks, knifes, spoons, chopsticks?",
            "Normal: Not at all (No problems).", 
            "Slight: I am slow, but I do not need any help handling my food and have not had food spills while eating.", 
            "Mild: I am slow with my eating and have occasional food spills. I may need help with a few tasks such as cutting meat.", 
            "Moderate: I need help with many eating tasks but can manage some alone.",
            "Severe: I need help for most or all eating tasks.");

    SurveyQuestion dressing = listQuestion("dressing", "Over the past week, have you usually had problems dressing? For example, are you slow or do you need help with buttoning, using zippers, putting on or taking off your clothes or jewelry?",
            "Normal: Not at all (no problems).", 
            "Slight: I am slow but I do not need help.", 
            "Mild: I am slow and need help for a few dressing tasks (buttons, bracelets).", 
            "Moderate: I need help for many dressing tasks.",
            "Severe: I need help for most or all dressing tasks.");
    
    SurveyQuestion washing = listQuestion("washing", "Over the past week, have you usually been slow or do you need help with washing, bathing, shaving, brushing teeth, combing your hair or with other personal hygiene?", 
            "Normal: Not at all (no problems).", 
            "Slight: I am slow but I do not need any help.", 
            "Mild: I need someone else to help me with some hygiene tasks.", 
            "Moderate: I need help for many hygiene tasks.",
            "Severe: I need help for most or all of my hygiene tasks." );
    
    SurveyQuestion handwriting = listQuestion("handwriting", "Over the past week, have people usually had trouble reading your handwriting?",
            "Normal: Not at all (no problems).",
            "Slight: My writing is slow, clumsy or uneven, but all words are clear.", 
            "Mild: Some words are unclear and difficult to read.",
            "Moderate: Many words are unclear and difficult to read.", 
            "Severe: Most or all words cannot be read.");
    
    SurveyQuestion hobbies = listQuestion("hobbies", "Over the past week, have you usually had trouble doing your hobbies or other things that you like to do?", 
            "Normal: Not at all (no problems).", 
            "Slight: I am a bit slow but do these activities easily.", 
            "Mild: I have some difficulty doing these activities.",
            "Moderate: I have major problems doing these activities, but still do most.", 
            "Severe: I am unable to do most or all of these activities.");
    
    SurveyQuestion turningOverInBed = listQuestion("turning-over-in-bed", "Over the past week, do you usually have trouble turning over in bed?", 
            "Normal: Not at all (no problems).",
            "Slight: I have a bit of trouble turning, but I do not need any help.",
            "Mild I have a lot of trouble turning and need occasional help from someone else.",
            "Moderate: To turn over I often need help from someone else.",
            "Severe: I am unable to turn over without help from someone else."); 

    SurveyQuestion tremors = listQuestion("tremors", "Over the past week, have you usually had shaking or tremor?",
            "Normal: Not at all. I have no shaking or tremor.", 
            "Slight: Shaking or tremor occurs but does not cause problems with any activities.",
            "Mild: Shaking or tremor causes problems with only a few activities.",
            "Moderate: Shaking or tremor causes problems with many of my daily activities.",
            "Severe: Shaking or tremor causes problems with most or all activities.");

    SurveyQuestion balance = listQuestion("balance", "Over the past week, have you usually had problems with balance and walking?", 
            "Normal: Not at all (no problems).",
            "Slight: I am slightly slow or may drag a leg. I never use a walking aid.",
            "Mild: I occasionally use a walking aid, but I do not need any help from another person.",
            "Moderate: I usually use a walking aid (cane, walker) to walk safely without falling. However, I do not usually need the support of another person.",
            "Severe: I usually use the support of another persons to walk safely without falling.");

    SurveyQuestion walking = listQuestion("walking", "Over the past week, on your usual day when walking, do you suddenly stop or freeze as if your feet are stuck to the floor.",
            "Normal: Not at all (no problems).",
            "Slight: I briefly freeze but I can easily start walking again. I do not need help from someone else or a walking aid (cane or walker) because of freezing.",
            "Mild: I freeze and have trouble starting to walk again, but I do not need someone’s help or a walking aid (cane or walker) because of freezing.",
            "Moderate: When I freeze I have a lot of trouble starting to walk again and, because of freezing, I sometimes need to use a walking aid or need someone else’s help.",
            "Severe: Because of freezing, most or all of the time, I need to use a walking aid or someone’s help.");
    
    private SurveyQuestion listQuestion(String identifier, String prompt, String... options) {
        SurveyQuestion q = new SurveyQuestion();
        q.setIdentifier(identifier);
        q.setPrompt(prompt);
        MultiValueConstraints c = new MultiValueConstraints(DataType.STRING);
        c.setEnumeration(Lists.newArrayList(
            new SurveyQuestionOption(options[0], "0"),
            new SurveyQuestionOption(options[1], "1"),
            new SurveyQuestionOption(options[2], "2"),
            new SurveyQuestionOption(options[3], "3"),
            new SurveyQuestionOption(options[4], "4")
        ));
        q.setUiHint(UiHint.RADIOBUTTON);
        q.setConstraints(c);
        return q;
    }
    
    private MultiValueConstraints tableConstraints = new MultiValueConstraints(DataType.STRING) {{
        setEnumeration(Lists.newArrayList(
            new SurveyQuestionOption("Never"),
            new SurveyQuestionOption("Rarely"),
            new SurveyQuestionOption("Sometimes"),
            new SurveyQuestionOption("Often"),
            new SurveyQuestionOption("Always")
        ));
    }};
    
    private SurveyQuestion tableQuestion(String identifier, String prompt) {
        SurveyQuestion q = new SurveyQuestion();
        q.setIdentifier(identifier);
        q.setPrompt(prompt);
        q.setUiHint(UiHint.RADIOBUTTON);
        q.setConstraints(tableConstraints);
        return q;
    }
    
    public ParkinsonWeeklySurvey() {
        setName("Parkinson Weekly Survey");
        setIdentifier("parkinson-weekly");
        
        getQuestions().add(feeling);
        getQuestions().add(strenuousExersize);
        getQuestions().add(moderateExersize);
        getQuestions().add(mildExersize);
        getQuestions().add(leisure);
        getQuestions().add(memory);
        getQuestions().add(emotions);
        getQuestions().add(tableQuestion("7-day-workthless", "In the past 7 days, I felt worthless"));
        getQuestions().add(tableQuestion("7-day-helpless", "In the past 7 days, I felt helpless"));
        getQuestions().add(tableQuestion("7-day-depressed", "In the past 7 days, I felt depressed"));
        getQuestions().add(tableQuestion("7-day-hopeless", "In the past 7 days, I felt hopeless"));
        getQuestions().add(tableQuestion("7-day-failure", "In the past 7 days, I felt like a failure"));
        getQuestions().add(tableQuestion("7-day-unhappy", "In the past 7 days, I felt unhappy"));
        getQuestions().add(tableQuestion("7-day-nothing-to-look-forward-to", "In the past 7 days, I felt that I had nothing to look forward to"));
        getQuestions().add(tableQuestion("7-day-nothing-to-cheer-me-up", "In the past 7 days, I felt that nothing could cheer me up"));
        getQuestions().add(anxiety);
        getQuestions().add(socialActivities);
        getQuestions().add(sleeping);
        getQuestions().add(wakefulness);
        getQuestions().add(speech);
        getQuestions().add(eating);
        getQuestions().add(dressing);
        getQuestions().add(washing);
        getQuestions().add(handwriting);
        getQuestions().add(hobbies);
        getQuestions().add(turningOverInBed);
        getQuestions().add(tremors);
        getQuestions().add(balance);
        getQuestions().add(walking);
    }

	@Override
	public Schedule getSchedule(GuidCreatedOnVersionHolder survey) {
        Schedule schedule = new Schedule();
        schedule.setLabel("Weekly Survey");
        schedule.setCronTrigger("0 0 6 ? * FRI *");
        schedule.setExpires(Period.days(4));
        ScriptUtils.setSurveyForSchedule(schedule, survey);
        schedule.setScheduleType(ScheduleType.recurring);
		return schedule;
	}
}
