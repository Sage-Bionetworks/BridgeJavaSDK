package org.sagebionetworks.bridge.scripts;

import org.joda.time.Period;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.schedules.ScheduleType;
import org.sagebionetworks.bridge.sdk.models.surveys.IntegerConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestion;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestionOption;
import org.sagebionetworks.bridge.sdk.models.surveys.TimeConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.UiHint;

public class BreastcancerMonthlySurvey extends BaseSurvey implements ScheduleHolder {

    private final SurveyQuestionOption[] options = new SurveyQuestionOption[] { 
        new SurveyQuestionOption("Almost always", "1"),
        new SurveyQuestionOption("Very often", "2"),
        new SurveyQuestionOption("Fairly often", "3"),
        new SurveyQuestionOption("Once in a while", "4"),
        new SurveyQuestionOption("Very infrequently", "5"),
        new SurveyQuestionOption("Almost never", "6")
    };
    
    private final SurveyQuestionOption[] options2 = new SurveyQuestionOption[] { 
        new SurveyQuestionOption("Not at all", "0"),
        new SurveyQuestionOption("Several days", "1"),
        new SurveyQuestionOption("More than half the days", "2"),
        new SurveyQuestionOption("Nearly every day", "3")
    };
    
    private final SurveyQuestionOption[] options3 = new SurveyQuestionOption[] { 
        new SurveyQuestionOption("Not during the past month", "0"),
        new SurveyQuestionOption("Less than once a week", "1"),
        new SurveyQuestionOption("Once or twice a week", "2"),
        new SurveyQuestionOption("Three or more times a week", "3")
    };
    
    private final SurveyQuestionOption[] options4 = new SurveyQuestionOption[] {
        new SurveyQuestionOption("No problem at all", "0"),
        new SurveyQuestionOption("Only a very slight problem", "1"),
        new SurveyQuestionOption("Somewhat of a problem", "2"),
        new SurveyQuestionOption("A very big problem", "3")
    };
    
    private final SurveyQuestionOption[] options5 = new SurveyQuestionOption[] {
        new SurveyQuestionOption("Very good", "0"),
        new SurveyQuestionOption("Fairly good", "1"),
        new SurveyQuestionOption("Fairly bad", "2"),
        new SurveyQuestionOption("Very bad", "3")
    };
    
    public BreastcancerMonthlySurvey() {
        setName("BCS Monthly Survey");
        setIdentifier("bcs-monthly");
        
        addMulti("q1","How often do you forget something that has been told to you within the last day or two?",false,options);
        addMulti("q2","How often do you forget events which have occurred in the last day or two?",false,options);
        addMulti("q3","How often do you forget people whom you met in the last day or two?",false,options);
        addMulti("q4","How often do you forget things that you knew a year or more ago?",false,options);
        addMulti("q5","How often do you forget people whom you knew or met a year or more ago?",false,options);
        addMulti("q6","How often do you lose track of time, or do things either earlier or later than they are usually done or are supposed to be done?",false,options);
        addMulti("q7","How often do you fail to finish something you start because you forgot that you were doing it? (Including such things as forgetting to put out cigarettes, turning off the stove, etc.)",false,options);
        addMulti("q8","How often do you fail to complete a task that you start because you have forgotten how to do one or more aspects of it?",false,options);
        addMulti("q9","How often do you lose things or have trouble remembering where they are?",false,options);
        addMulti("q10","How often do you forget things that you are supposed to do or have agreed to do (such as putting gas in the car, paying bills, taking care of errands, etc.)?",false,options);
        addMulti("q11","How often do you have difficulties understanding what is said to  you?",false,options);
        addMulti("q12","How often do you have difficulties recognizing or identifying printed words?",false,options);
        addMulti("q13","How often do you have difficulty understanding reading material  which at one time you could have understood?",false,options);
        addMulti("q14","Is it easier to have people show you things than it is to have them tell you about things?",false,options);
        addMulti("q15","When you speak, are your words indistinct or improperly pronounced?",false,options);
        addMulti("q15a","If this happens, how often do people have difficulty understanding what words you are trying to say?",false,options);
        addMulti("q16","How often do you have difficulty thinking of names of things?",false,options);
        addMulti("q17","How often do you have difficulty thinking of the words (other than names) for what you want to say?",false,options);
        addMulti("q18","When you write things, how often do you have difficulty forming the letters correctly?",false,options);
        addMulti("q19","Do you have more difficulty spelling, or make more errors in spelling, than you used to?",false,options);
        addMulti("q20","How often do your thoughts seem confused or illogical?",false,options);
        addMulti("q21","How often do you become distracted from what you are doing or saying by insignificant things which at one time you would have been able to ignore?",false,options);
        addMulti("q22","How often do you become confused  about (or make a mistake about) where you are?",false,options);
        addMulti("q23","How often do you have difficulty finding  your way about?",false,options);
        addMulti("q24","Do you have more difficulty now than you used to in calculating or working with numbers (including managing finances, paying bills, etc.)?",false,options);
        addMulti("q25","Do you have more difficulty now than you used to in planning or organizing activities (e.g., deciding what to do and how it should be done)?",false,options);
        addMulti("q26","Do you have more difficulty now than you used to in solving problems that come up around the house, at your job, etc.? (In other words, when something new has to be accomplished, or some new difficulty comes up, do you have more trouble figuring out what should be done and how to do it)?",false,options);
        addMulti("q27","Do you have more difficulty now than you used to in following directions to get somewhere?",false,options);
        addMulti("q28","Do you have more difficulty now than you used to in following instructions  concerning how to do things?",false,options);
        
        addMulti("q29a","Over the last TWO WEEKS how often have you experienced little interest or pleasure in doing things?",false,options2);
        addMulti("q29b","Over the last TWO WEEKS how often have you been feeling down, depressed, or hopeless?",false,options2);
        addMulti("q29c","Over the last TWO WEEKS how often have you had trouble falling or staying asleep, or sleeping too much?",false,options2);
        addMulti("q29d","Over the last TWO WEEKS how often have felt tired or had little energy?",false,options2);
        addMulti("q29e","Over the last TWO WEEKS how often have you had poor appetite or overeating?",false,options2);
        addMulti("q29f","Over the last TWO WEEKS how often have felt bad about yourself – or felt that you are a failure or have let yourself or your family down?",false,options2);
        addMulti("q29g","Over the last TWO WEEKS how often have you had trouble concentrating on things, such as reading the newspaper or watching television?",false,options2);
        addMulti("q29h","Over the last TWO WEEKS how often have moved or spoke so slowly that other people could have noticed? Or the opposite – been so fidgety or restless that you have been moving around a lot more than usual?",false,options2);
        
        addMulti("q30a","Over the last TWO WEEKS how often have you felt nervous, anxious, or on edge?",false,options2);
        addMulti("q30b","Over the last TWO WEEKS how often have you not been able to stop or control worrying?",false,options2);
        addMulti("q30c","Over the last TWO WEEKS how often have you worried too much about different things?",false,options2);
        addMulti("q30d","Over the last TWO WEEKS how often have you had trouble relaxing?",false,options2);
        addMulti("q30e","Over the last TWO WEEKS how often have you been so restless that it's hard to sit still?",false,options2);
        addMulti("q30f","Over the last TWO WEEKS how often have you become easily annoyed or irritable?",false,options2);
        addMulti("q30g","Over the last TWO WEEKS how often have you felt afraid as if something awful might happen?",false,options2);
        
        addDateTime("q31", "What time have you usually gone to bed?");
        
        SurveyQuestion q = new SurveyQuestion();
        q.setIdentifier("q32");
        q.setPrompt("How long (in minutes) has it taken you to fall asleep each night?");
        q.setUiHint(UiHint.SLIDER);
        IntegerConstraints c = new IntegerConstraints();
        c.setMinValue(0L);
        c.setMaxValue(180L);
        c.setStep(10L);
        q.setConstraints(c);
        getQuestions().add(q);
        
        addDateTime("q33", "When have you usually gotten up in the morning?");
        
        q = new SurveyQuestion();
        q.setIdentifier("q34");
        q.setPrompt("How many hours of actual sleep did you get at night? (This may be different than the number of hours you spend in bed)?");
        q.setUiHint(UiHint.TIMEPICKER);
        q.setConstraints(new TimeConstraints());
        getQuestions().add(q);
        
        addMulti("q35a", "During the PAST MONTH, how often could you not get to sleep within 30 minutes?", false, options3);
        addMulti("q35b", "During the PAST MONTH, how often did you wake up in the middle of the night or early morning?", false, options3);
        addMulti("q35c", "During the PAST MONTH, how often could you not get to sleep because you had to get up to use the bathroom?", false, options3);
        addMulti("q35d", "During the PAST MONTH, how often could you not get to sleep because you could not breathe comfortably?", false, options3);
        addMulti("q35e", "During the PAST MONTH, how often could you not get to sleep because you coughed or snored loudly?", false, options3);
        addMulti("q35f", "During the PAST MONTH, how often could you not get to sleep because you felt too cold?", false, options3);
        addMulti("q35g", "During the PAST MONTH, how often could you not get to sleep because you felt too hot?", false, options3);
        addMulti("q35h", "During the PAST MONTH, how often could you not get to sleep because you had bad dreams?", false, options3);
        addMulti("q35i", "During the PAST MONTH, how often could you not get to sleep because you had pain?", false, options3);
        
        addMulti("q36", "During the past month, how often have you taken medicine (prescribed or “over the counter”) to help you sleep?", false, options3);
        addMulti("q37", "During the past month, how often have you had trouble staying awake while driving, eating meals, or engaging in social activity?", false, options3);
        
        addMulti("q38", "During the past month, how much of a problem has it been for you to keep up enthusiasm to get things done?", false, options4);

        addMulti("q39", "During the past month, how would you rate your sleep quality overall?", false, options5);
        
        
    }

	@Override
	public Schedule getSchedule(GuidCreatedOnVersionHolder survey) {
        Schedule schedule = new Schedule();
        schedule.setLabel("Monthly Survey");
        schedule.setCronTrigger("0 0 6 ? 1/1 THU#1 *");
        schedule.setExpires(Period.days(7));
        ScriptUtils.setSurveyForSchedule(schedule, survey);
        schedule.setScheduleType(ScheduleType.recurring);
        return schedule;
	}
}
