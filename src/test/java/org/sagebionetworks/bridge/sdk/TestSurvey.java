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
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestion;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestionOption;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyRule;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyRule.Operator;
import org.sagebionetworks.bridge.sdk.models.surveys.TimeConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.UiHint;
import org.sagebionetworks.bridge.sdk.models.surveys.Unit;

import com.google.common.collect.Lists;

public class TestSurvey extends Survey {

    public static final String MULTIVALUE_ID = "feeling";
    public static final String STRING_ID = "phone_number";
    public static final String BOOLEAN_ID = "high_bp";
    public static final String DATE_ID = "last_checkup";
    public static final String DATETIME_ID = "last_reading";
    public static final String DECIMAL_ID = "deleuterium_dosage";
    public static final String DURATION_ID = "time_for_appt";
    public static final String INTEGER_ID = "BP X DAY";
    public static final String TIME_ID = "deleuterium_x_day";
    
    private SurveyQuestion multiValueQuestion = new SurveyQuestion() {
        {
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
            setConstraints(mvc);
            setPrompt("How do you feel today?");
            setIdentifier(MULTIVALUE_ID);
            setUiHint(UiHint.LIST);
        }
    };

    private SurveyQuestion stringQuestion = new SurveyQuestion() {
        {
            StringConstraints c = new StringConstraints();
            c.setMinLength(2);
            c.setMaxLength(255);
            c.setPattern("\\d{3}-\\d{3}-\\d{4}");
            setPrompt("Please enter an emergency phone number (###-###-####)?");
            setIdentifier(STRING_ID);
            setConstraints(c);
            setUiHint(UiHint.TEXTFIELD);
        }
    };

    private SurveyQuestion booleanQuestion = new SurveyQuestion() {
        {
            BooleanConstraints c = new BooleanConstraints();
            setPrompt("Do you have high blood pressure?");
            setIdentifier(BOOLEAN_ID);
            setConstraints(c);
            setUiHint(UiHint.CHECKBOX);
        }
    };

    private SurveyQuestion dateQuestion = new SurveyQuestion() {
        {
            DateConstraints c = new DateConstraints();
            c.setEarliestValue(DateTime.parse("2000-01-01"));
            c.setLatestValue(DateTime.parse("2020-12-31"));
            c.setAllowFuture(true);
            setPrompt("When did you last have a medical check-up?");
            setIdentifier(DATE_ID);
            setConstraints(c);
            setUiHint(UiHint.DATEPICKER);
        }
    };

    private SurveyQuestion dateTimeQuestion = new SurveyQuestion() {
        {
            DateTimeConstraints c = new DateTimeConstraints();
            c.setAllowFuture(true);
            c.setEarliestValue(DateTime.parse("2000-01-01"));
            c.setLatestValue(DateTime.parse("2020-12-31"));
            setPrompt("When is your next medical check-up scheduled?");
            setIdentifier(DATETIME_ID);
            setConstraints(c);
            setUiHint(UiHint.DATETIMEPICKER);
        }
    };

    private SurveyQuestion decimalQuestion = new SurveyQuestion() {
        {
            DecimalConstraints c = new DecimalConstraints();
            c.setMinValue(0.0d);
            c.setMaxValue(10.0d);
            c.setStep(0.1d);
            setPrompt("What dosage (in grams) do you take of deleuterium each day?");
            setIdentifier(DECIMAL_ID);
            setConstraints(c);
            setUiHint(UiHint.NUMBERFIELD);
        }
    };

    private SurveyQuestion durationQuestion = new SurveyQuestion() {
        {
            DurationConstraints c = new DurationConstraints();
            c.setMinValue(1d);
            c.setMaxValue(120d);
            c.setUnit(Unit.minutes);
            setPrompt("How log does your appointment take, on average?");
            setIdentifier(DURATION_ID);
            setConstraints(c);
            setUiHint(UiHint.SLIDER);
        }
    };

    private SurveyQuestion integerQuestion = new SurveyQuestion() {
        {
            IntegerConstraints c = new IntegerConstraints();
            c.setMinValue(0d);
            c.setMaxValue(8d);
            c.getRules().add(new SurveyRule(Operator.le, 2, "phone_number"));
            c.getRules().add(new SurveyRule(Operator.de, null, "phone_number"));

            setPrompt("How many times a day do you take your blood pressure?");
            setIdentifier(INTEGER_ID);
            setConstraints(c);
            setUiHint(UiHint.NUMBERFIELD);
        }
    };

    private SurveyQuestion timeQuestion = new SurveyQuestion() {
        {
            TimeConstraints c = new TimeConstraints();
            setPrompt("What times of the day do you take deleuterium?");
            setIdentifier(TIME_ID);
            setConstraints(c);
            setUiHint(UiHint.TIMEPICKER);
        }
    };

    public TestSurvey() {
        setName("General Blood Pressure Survey");
        setIdentifier("bloodpressure");
        List<SurveyQuestion> questions = getQuestions();
        questions.add(booleanQuestion);
        questions.add(dateQuestion);
        questions.add(dateTimeQuestion);
        questions.add(decimalQuestion);
        questions.add(integerQuestion);
        questions.add(durationQuestion);
        questions.add(timeQuestion);
        questions.add(multiValueQuestion);
        questions.add(stringQuestion);
    }
}
