package org.sagebionetworks.bridge.scripts;

import org.joda.time.Period;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.schedules.ScheduleType;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestionOption;

public class BreastcancerWeeklySurvey extends BaseSurvey implements ScheduleHolder {
    public BreastcancerWeeklySurvey() {
        setName("BCS Weekly Survey");
        setIdentifier("bcs-weekly");
        
        addSlider("q1","Rate your level of fatigue on the day you felt MOST fatigued during the past week (10 is the most fatigued you can be):");
        addSlider("q2","Rate your level of fatigue on the day you felt LEAST fatigued during the past week (10 is the most fatigued you can be):");
        addSlider("q3","Rate your level of fatigue on the AVERAGE in the past week (10 is the most fatigued you can be):");
        addSlider("q4","Indicate how many days, in the past week, you felt fatigued for any part of the day:", 7);
        
        SurveyQuestionOption[] options = new SurveyQuestionOption[] {
            new SurveyQuestionOption("Very slightly or not at all","1"),
            new SurveyQuestionOption("A little","2"),
            new SurveyQuestionOption("Moderately","3"),
            new SurveyQuestionOption("Quite a bit","4"),
            new SurveyQuestionOption("Extremely","5")
        };
        addMulti("q5a", "During the past week, how much have you felt interested:", false, options);
        addMulti("q5b", "During the past week, how much have you felt distressed:", false, options);
        addMulti("q5c", "During the past week, how much have you felt excited:", false, options);
        addMulti("q5d", "During the past week, how much have you felt upset:", false, options);
        addMulti("q5e", "During the past week, how much have you felt strong:", false, options);
        addMulti("q5f", "During the past week, how much have you felt guilty:", false, options);
        addMulti("q5g", "During the past week, how much have you felt scared:", false, options);
        addMulti("q5h", "During the past week, how much have you felt hostile:", false, options);
        addMulti("q5i", "During the past week, how much have you felt enthusiastic:", false, options);
        addMulti("q5j", "During the past week, how much have you felt proud:", false, options);
        addMulti("q5k", "During the past week, how much have you felt irritable:", false, options);
        addMulti("q5l", "During the past week, how much have you felt alert:", false, options);
        addMulti("q5m", "During the past week, how much have you felt ashamed:", false, options);
        addMulti("q5n", "During the past week, how much have you felt inspired:", false, options);
        addMulti("q5o", "During the past week, how much have you felt nervous:", false, options);
        addMulti("q5p", "During the past week, how much have you felt determined:", false, options);
        addMulti("q5q", "During the past week, how much have you felt attentive:", false, options);
        addMulti("q5r", "During the past week, how much have you felt jittery:", false, options);
        addMulti("q5s", "During the past week, how much have you felt active:", false, options);
        addMulti("q5t", "During the past week, how much have you felt afraid:", false, options);
        
        addSlider("q6a", "In the last week, how many times did you exercise strenuously (heart beats rapidly) for more than 15 minutes during your free time?");
        addSlider("q6b", "In the last week, how many times did you exercise moderately (not exhausting) for more than 15 minutes during your free time?");
        addSlider("q6c", "In the last week, how many times did you engage in mild exercise (minimal effort) for more than 15 minutes during your free time?");
        
        addMulti("q7", "Considering the last week, during your leisure-time, how often did you engage in any regular activity long enough to work up a sweat?", 
        		false, "Often", "Sometimes", "Never/Rarely");
        
        // Skipping the optional questions because you have got to be kidding me.
    }

	@Override
	public Schedule getSchedule(GuidCreatedOnVersionHolder survey) {
        Schedule schedule = new Schedule();
        schedule.setLabel("Weekly Survey");
        schedule.setCronTrigger("0 0 6 ? * FRI *");
        schedule.setExpires(Period.days(4));
        ScriptUtils.setSurveyActivity(schedule, survey);
        schedule.setScheduleType(ScheduleType.recurring);
        return schedule;
	}
    
}
