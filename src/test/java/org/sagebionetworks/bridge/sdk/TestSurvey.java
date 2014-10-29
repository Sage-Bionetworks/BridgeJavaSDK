package org.sagebionetworks.bridge.sdk;

import java.util.List;
import java.util.UUID;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

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

    public Survey getSurvey() {
        return survey;
    }

    public String getGuid() {
        return survey.getGuid();
    }

    public DateTime getVersionedOn() {
        return survey.getVersionedOn();
    }

    public DateTime getModifiedOn() {
        return survey.getModifiedOn();
    }

    public String getName() {
        return survey.getName();
    }

    public String getIdentifier() {
        return survey.getIdentifier();
    }

    public boolean isPublished() {
        return survey.isPublished();
    }

    public List<SurveyQuestion> getQuestions() {
        return survey.getQuestions();
    }

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
