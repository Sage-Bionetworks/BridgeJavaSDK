package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.joda.time.DateTime;
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
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyElement;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestion;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestionOption;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyRule;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyRule.Operator;
import org.sagebionetworks.bridge.sdk.models.surveys.TimeConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.UiHint;
import org.sagebionetworks.bridge.sdk.models.surveys.Unit;

import com.google.common.collect.Lists;

public class TestSurvey {
    
    public static final String MULTIVALUE_ID = "feeling";
    public static final String STRING_ID = "phone_number";
    public static final String BOOLEAN_ID = "high_bp";
    public static final String DATE_ID = "last_checkup";
    public static final String DATETIME_ID = "last_reading";
    public static final String DECIMAL_ID = "deleuterium_dosage";
    public static final String DURATION_ID = "time_for_appt";
    public static final String INTEGER_ID = "BP X DAY";
    public static final String TIME_ID = "deleuterium_x_day";
    
    public static final Survey getSurvey() {
        Survey survey = new Survey();
        
        SurveyQuestion multiValueQuestion = new SurveyQuestion();
        Image terrible = new Image("http://terrible.svg", 600, 300);
        Image poor = new Image("http://poor.svg", 600, 300);
        Image ok = new Image("http://ok.svg", 600, 300);
        Image good = new Image("http://good.svg", 600, 300);
        Image great = new Image("http://great.svg", 600, 300);
        MultiValueConstraints mvc = new MultiValueConstraints(DataType.INTEGER);
        List<SurveyQuestionOption> options = Lists.newArrayList(
                new SurveyQuestionOption("Terrible", "1", terrible), new SurveyQuestionOption("Poor", "2", poor),
                new SurveyQuestionOption("OK", "3", ok), new SurveyQuestionOption("Good", "4", good),
                new SurveyQuestionOption("Great", "5", great));
        mvc.setEnumeration(options);
        mvc.setAllowOther(false);
        mvc.setAllowMultiple(true);
        multiValueQuestion.setConstraints(mvc);
        multiValueQuestion.setPrompt("How do you feel today?");
        multiValueQuestion.setIdentifier(MULTIVALUE_ID);
        multiValueQuestion.setUiHint(UiHint.LIST);
        
        SurveyQuestion stringQuestion = new SurveyQuestion();
        StringConstraints c = new StringConstraints();
        c.setMinLength(2);
        c.setMaxLength(255);
        c.setPattern("\\d{3}-\\d{3}-\\d{4}");
        stringQuestion.setPrompt("Please enter an emergency phone number (###-###-####)?");
        stringQuestion.setIdentifier(STRING_ID);
        stringQuestion.setConstraints(c);
        stringQuestion.setUiHint(UiHint.TEXTFIELD);
        
        SurveyQuestion booleanQuestion = new SurveyQuestion();
        BooleanConstraints c9 = new BooleanConstraints();
        booleanQuestion.setPrompt("Do you have high blood pressure?");
        booleanQuestion.setIdentifier(BOOLEAN_ID);
        booleanQuestion.setConstraints(c9);
        booleanQuestion.setUiHint(UiHint.CHECKBOX);
        
        SurveyQuestion dateQuestion = new SurveyQuestion();
        DateConstraints c2 = new DateConstraints();
        c2.setEarliestValue(DateTime.parse("2000-01-01"));
        c2.setLatestValue(DateTime.parse("2020-12-31"));
        c2.setAllowFuture(true);
        dateQuestion.setPrompt("When did you last have a medical check-up?");
        dateQuestion.setIdentifier(DATE_ID);
        dateQuestion.setConstraints(c2);
        dateQuestion.setUiHint(UiHint.DATEPICKER);
        
        SurveyQuestion dateTimeQuestion = new SurveyQuestion();
        DateTimeConstraints c3 = new DateTimeConstraints();
        c3.setAllowFuture(true);
        c3.setEarliestValue(DateTime.parse("2000-01-01"));
        c3.setLatestValue(DateTime.parse("2020-12-31"));
        dateTimeQuestion.setPrompt("When is your next medical check-up scheduled?");
        dateTimeQuestion.setIdentifier(DATETIME_ID);
        dateTimeQuestion.setConstraints(c3);
        dateTimeQuestion.setUiHint(UiHint.DATETIMEPICKER);
        
        SurveyQuestion decimalQuestion = new SurveyQuestion();
        DecimalConstraints c4 = new DecimalConstraints();
        c4.setMinValue(0.0d);
        c4.setMaxValue(10.0d);
        c4.setStep(0.1d);
        decimalQuestion.setPrompt("What dosage (in grams) do you take of deleuterium each day?");
        decimalQuestion.setIdentifier(DECIMAL_ID);
        decimalQuestion.setConstraints(c4);
        decimalQuestion.setUiHint(UiHint.NUMBERFIELD);
        
        SurveyQuestion durationQuestion = new SurveyQuestion();
        DurationConstraints c5 = new DurationConstraints();
        c5.setMinValue(1d);
        c5.setMaxValue(120d);
        c5.setUnit(Unit.MINUTES);
        durationQuestion.setPrompt("How log does your appointment take, on average?");
        durationQuestion.setIdentifier(DURATION_ID);
        durationQuestion.setConstraints(c5);
        durationQuestion.setUiHint(UiHint.SLIDER);
        
        SurveyQuestion integerQuestion = new SurveyQuestion();
        IntegerConstraints c6 = new IntegerConstraints();
        c6.setMinValue(0d);
        c6.setMaxValue(8d);
        c6.getRules().add(new SurveyRule(Operator.LE, 2, "phone_number"));
        c6.getRules().add(new SurveyRule(Operator.DE, null, "phone_number"));
        integerQuestion.setPrompt("How many times a day do you take your blood pressure?");
        integerQuestion.setIdentifier(INTEGER_ID);
        integerQuestion.setConstraints(c6);
        integerQuestion.setUiHint(UiHint.NUMBERFIELD);
        
        SurveyQuestion timeQuestion = new SurveyQuestion();
        TimeConstraints c7 = new TimeConstraints();
        timeQuestion.setPrompt("What times of the day do you take deleuterium?");
        timeQuestion.setIdentifier(TIME_ID);
        timeQuestion.setConstraints(c7);
        timeQuestion.setUiHint(UiHint.TIMEPICKER);
        
        survey.setName("General Blood Pressure Survey");
        survey.setIdentifier("bloodpressure");
        List<SurveyElement> elements = survey.getElements();
        elements.add(booleanQuestion);
        elements.add(dateQuestion);
        elements.add(dateTimeQuestion);
        elements.add(decimalQuestion);
        elements.add(integerQuestion);
        elements.add(durationQuestion);
        elements.add(timeQuestion);
        elements.add(multiValueQuestion);
        elements.add(stringQuestion);

        return survey;
    }

}
