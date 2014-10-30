package org.sagebionetworks.bridge.sdk;

import java.util.List;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

public class TestSurvey extends Survey {

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
            setIdentifier("feeling");
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
            setIdentifier("phone_number");
            setConstraints(c);
            setUiHint(UiHint.TEXTFIELD);
        }
    };

    private SurveyQuestion booleanQuestion = new SurveyQuestion() {
        {
            BooleanConstraints c = new BooleanConstraints();
            setPrompt("Do you have high blood pressure?");
            setIdentifier("high_bp");
            setConstraints(c);
            setUiHint(UiHint.CHECKBOX);
        }
    };

    private SurveyQuestion dateQuestion = new SurveyQuestion() {
        {
            DateConstraints c = new DateConstraints();
            setPrompt("When did you last have a medical check-up?");
            setIdentifier("last_checkup");
            setConstraints(c);
            setUiHint(UiHint.DATEPICKER);
        }
    };

    private SurveyQuestion dateTimeQuestion = new SurveyQuestion() {
        {
            DateTimeConstraints c = new DateTimeConstraints();
            c.setAllowFuture(true);
            setPrompt("When is your next medical check-up scheduled?");
            setIdentifier("last_reading");
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
            setIdentifier("deleuterium_dosage");
            setConstraints(c);
            setUiHint(UiHint.NUMBERFIELD);
        }
    };

    private SurveyQuestion durationQuestion = new SurveyQuestion() {
        {
            DurationConstraints c = new DurationConstraints();
            setPrompt("How log does your appointment take, on average?");
            setIdentifier("time_for_appt");
            setConstraints(c);
            setUiHint(UiHint.DATEPICKER);
        }
    };

    private SurveyQuestion integerQuestion = new SurveyQuestion() {
        {
            IntegerConstraints c = new IntegerConstraints();
            c.setMinValue(0L);
            c.setMaxValue(4L);
            c.getRules().add(new SurveyRule(Operator.le, 2, "phone_number"));
            c.getRules().add(new SurveyRule(Operator.de, null, "phone_number"));

            setPrompt("How many times a day do you take your blood pressure?");
            setIdentifier("BP X DAY");
            setConstraints(c);
            setUiHint(UiHint.NUMBERFIELD);
        }
    };

    private SurveyQuestion timeQuestion = new SurveyQuestion() {
        {
            TimeConstraints c = new TimeConstraints();
            setPrompt("What times of the day do you take deleuterium?");
            setIdentifier("deleuterium_x_day");
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

    @JsonIgnore
    public SurveyQuestion getBooleanQuestion() {
        return booleanQuestion;
    }

    @JsonIgnore
    public SurveyQuestion getDateQuestion() {
        return dateQuestion;
    }

    @JsonIgnore
    public SurveyQuestion getDateTimeQuestion() {
        return dateTimeQuestion;
    }

    @JsonIgnore
    public SurveyQuestion getDecimalQuestion() {
        return decimalQuestion;
    }

    @JsonIgnore
    public SurveyQuestion getIntegerQuestion() {
        return integerQuestion;
    }

    @JsonIgnore
    public SurveyQuestion getDurationQuestion() {
        return durationQuestion;
    }

    @JsonIgnore
    public SurveyQuestion getTimeQuestion() {
        return timeQuestion;
    }

    @JsonIgnore
    public SurveyQuestion getMultiValueQuestion() {
        return multiValueQuestion;
    }

    @JsonIgnore
    public SurveyQuestion getStringQuestion() {
        return stringQuestion;
    }

}
