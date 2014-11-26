package org.sagebionetworks.bridge.scripts;

import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.schedules.ScheduleType;
import org.sagebionetworks.bridge.sdk.models.surveys.DataType;
import org.sagebionetworks.bridge.sdk.models.surveys.DateConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.DurationConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.IntegerConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.MultiValueConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestion;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestionOption;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyRule;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyRule.Operator;
import org.sagebionetworks.bridge.sdk.models.surveys.UiHint;

import com.google.common.collect.Lists;

/**
 * There are 41 questions in this survey. Seriously.
 */
public class ParkinsonDemographicSurvey extends BaseSurvey implements ScheduleHolder {
    
    SurveyQuestion age = new SurveyQuestion() {{
        setIdentifier("age");
        setPrompt("How old are you?");
        setUiHint(UiHint.NUMBERFIELD);
        setConstraints(new IntegerConstraints());
    }};
    
    // Too exhausting to redo all of these
    SurveyQuestion gender = addMulti("gender", "What is your sex?", false, "Male", "Female", "Prefer not to answer");
    
    SurveyQuestion race = new SurveyQuestion() {{
        setIdentifier("race");
        setPrompt("With which race do you identify?");
        setUiHint(UiHint.CHECKBOX);
        MultiValueConstraints c = new MultiValueConstraints(DataType.STRING);
        c.setAllowMultiple(true);
        c.setAllowOther(true);
        c.setEnumeration(Lists.newArrayList(
            new SurveyQuestionOption("Black or African"),
            new SurveyQuestionOption("Latino/Hispanic"),
            new SurveyQuestionOption("Native American"),
            new SurveyQuestionOption("Pacific Islander"),
            new SurveyQuestionOption("Middle Eastern"),
            new SurveyQuestionOption("Carribean"),
            new SurveyQuestionOption("South Asian"),
            new SurveyQuestionOption("East Asian"),
            new SurveyQuestionOption("White or Caucasian"),
            new SurveyQuestionOption("Mixed")
            // and allow other. Hope they catch that...
        ));
        setConstraints(c);
    }};
    
    SurveyQuestion countryOfResidence = new SurveyQuestion() {{
        setIdentifier("residence");
        setPrompt("What is your country of residence?");
        setUiHint(UiHint.SELECT);
        MultiValueConstraints c = new MultiValueConstraints(DataType.STRING);
        c.setEnumeration(new CountryOfResidenceList());
        c.setRules(Lists.newArrayList(
            new SurveyRule(Operator.eq, "USA", "state"),
            new SurveyRule(Operator.ne, "USA", "education"),
            new SurveyRule(Operator.de, null, "education")
        ));
        setConstraints(c);
    }};
    
    SurveyQuestion stateOfResidence = new SurveyQuestion() {{
        setIdentifier("state");
        setPrompt("In which state do you reside?");
        setUiHint(UiHint.SELECT);
        MultiValueConstraints c = new MultiValueConstraints(DataType.STRING);
        c.setEnumeration(new UnitedStatesList());
        setConstraints(c);
    }};
    
    SurveyQuestion education = new SurveyQuestion() {{
        setIdentifier("education");
        setPrompt("What is the highest level of education that you have completed?");
        setUiHint(UiHint.SELECT);
        MultiValueConstraints c = new MultiValueConstraints(DataType.STRING);
        c.setEnumeration(Lists.newArrayList(
            new SurveyQuestionOption("Doctoral Degree"),
            new SurveyQuestionOption("Master's Degree"),
            new SurveyQuestionOption("Some graduate school"),
            new SurveyQuestionOption("4-year college degree"),
            new SurveyQuestionOption("2-year college degree"),
            new SurveyQuestionOption("Some college"),
            new SurveyQuestionOption("High School Diploma/GED"),
            new SurveyQuestionOption("Some high school")
        ));
        setConstraints(c);
    }};
    
    SurveyQuestion employment = new SurveyQuestion() {{
        setIdentifier("employment");
        setPrompt("What is your current employment status?");
        setUiHint(UiHint.SELECT);
        MultiValueConstraints c = new MultiValueConstraints(DataType.STRING);
        c.setEnumeration(Lists.newArrayList(
            new SurveyQuestionOption("Employment for wages"),
            new SurveyQuestionOption("Self-employed"),
            new SurveyQuestionOption("Out of work and looking for work"),
            new SurveyQuestionOption("Out of work but not currently looking for work"),
            new SurveyQuestionOption("A homemaker"),
            new SurveyQuestionOption("A student"),
            new SurveyQuestionOption("Military"),
            new SurveyQuestionOption("Retired"),
            new SurveyQuestionOption("Unable to work")
        ));
        setConstraints(c);
    }};
    
    SurveyQuestion maritalStatus = new SurveyQuestion() {{
        setIdentifier("maritalStatus");
        setPrompt("What is your current marital status?");
        setUiHint(UiHint.RADIOBUTTON);
        MultiValueConstraints c = new MultiValueConstraints(DataType.STRING);
        c.setAllowOther(true);
        c.setEnumeration(Lists.newArrayList(
            new SurveyQuestionOption("Single, never married"),
            new SurveyQuestionOption("Married or domestic partnership"),
            new SurveyQuestionOption("Widowed"),
            new SurveyQuestionOption("Divorced"),
            new SurveyQuestionOption("Separated")
            // and other
        ));
        setConstraints(c);
    }};
    
    SurveyQuestion areCaretaker = new SurveyQuestion() {{
       setIdentifier("are-caretaker");
       setPrompt("Are you a spouse, partner or carepartner of someone who has Parkinson disease?");
       setUiHint(UiHint.RADIOBUTTON);
       setConstraints(ScriptUtils.booleanish());
    }};
    
    SurveyQuestion pastParticipation = new SurveyQuestion() {{
        setIdentifier("past-participation");
        setPrompt("Have you ever participated in a research study or clinical trial on Parkinson disease before?");
        setUiHint(UiHint.RADIOBUTTON);
        setConstraints(ScriptUtils.booleanish());
     }};    
    
     SurveyQuestion smartphone = new SurveyQuestion() {{
         setIdentifier("smartphone");
         setPrompt("How easy is it for you to use your smart phone?");
         setUiHint(UiHint.RADIOBUTTON);
         MultiValueConstraints c = new MultiValueConstraints(DataType.STRING);
         c.setEnumeration(Lists.newArrayList(
             new SurveyQuestionOption("Very easy"),
             new SurveyQuestionOption("Easy"),
             new SurveyQuestionOption("Neither easy nor difficult"),
             new SurveyQuestionOption("Difficult"),
             new SurveyQuestionOption("Very Difficult")
         ));
         setConstraints(c);
    }};
    
    SurveyQuestion phoneUsage = new SurveyQuestion() {{
       setIdentifier("phone-usage");
       setPrompt("Do you ever use your cell phone to look for health or medical information online?");
       setUiHint(UiHint.RADIOBUTTON);
       MultiValueConstraints c = new MultiValueConstraints(DataType.STRING);
       c.setEnumeration(Lists.newArrayList(
           new SurveyQuestionOption("Yes"),
           new SurveyQuestionOption("No"),
           new SurveyQuestionOption("Not sure")
       ));
       setConstraints(c);
    }};
    
    SurveyQuestion homeUsage = new SurveyQuestion() {{
        setIdentifier("home-usage");
        setPrompt("Do you use the internet or email at home?");
        setUiHint(UiHint.RADIOBUTTON);
        setConstraints(ScriptUtils.booleanish());
    }};
     
    SurveyQuestion medicalUsage  = new SurveyQuestion() {{
        setIdentifier("medical-usage");
        setPrompt("Do you ever use the Internet to look for health or medical information online?");
        setUiHint(UiHint.RADIOBUTTON);
        // Could be a boolean, but many of these have a third option, which is "don't know"
        MultiValueConstraints c = new MultiValueConstraints(DataType.STRING);
        c.setEnumeration(Lists.newArrayList(
            new SurveyQuestionOption("Yes, I have done this", "yes"),
            new SurveyQuestionOption("No, I have never done this", "no")
        ));
        c.getRules().add(new SurveyRule(Operator.eq, "yes", "medical-usage-yesterday"));
        c.getRules().add(new SurveyRule(Operator.de, null, "video-usage"));
        c.getRules().add(new SurveyRule(Operator.ne, "yes", "video-usage"));
        setConstraints(c);
    }};
    
    SurveyQuestion medicalUsageYesterday = new SurveyQuestion() {{
        setIdentifier("medical-usage-yesterday");
        setPrompt("Did you happen to do this yesterday, or not?");
        setUiHint(UiHint.RADIOBUTTON);
        MultiValueConstraints c = new MultiValueConstraints(DataType.STRING);
        c.setEnumeration(Lists.newArrayList(
            new SurveyQuestionOption("Did this yesterday", "yes"),
            new SurveyQuestionOption("Did not do this yesterday", "no"),
            new SurveyQuestionOption("Don't know", "dont-know")
        ));
        setConstraints(c);
    }};
    
    SurveyQuestion videoUsage = new SurveyQuestion() {{
        setIdentifier("video-usage");
        setPrompt("Do you ever use your smartphone to participate in a video call or video chat?");
        setUiHint(UiHint.RADIOBUTTON);
        setConstraints(ScriptUtils.booleanish());
    }};
    
    SurveyQuestion hasPD = new SurveyQuestion() {{
        setIdentifier("has-pd");
        setPrompt("Have you been clinically diagnosed with Parkinson Disease?");
        setUiHint(UiHint.RADIOBUTTON);
        MultiValueConstraints c = ScriptUtils.booleanish();
        c.getRules().add(new SurveyRule(Operator.eq, "true", "professional-diagnosis"));
        c.getRules().add(new SurveyRule(Operator.de, null, "deep-brain-stimulation"));
        c.getRules().add(new SurveyRule(Operator.ne, "true", "deep-brain-stimulation"));
        setConstraints(c);
    }};
    
    SurveyQuestion professionalDiagnosis = new SurveyQuestion() {{
        setIdentifier("professional-diagnosis");
        setPrompt("Have you been diagnosed by a medical professional with Parkinson disease?");
        setUiHint(UiHint.RADIOBUTTON);
        setConstraints(ScriptUtils.booleanish());
    }};
    
    SurveyQuestion onsetYear = new SurveyQuestion() {{
        setIdentifier("onset-year");
        setPrompt("In what year did your movement symptoms begin?");
        setUiHint(UiHint.NUMBERFIELD);
        setConstraints(new IntegerConstraints());
    }};
    
    SurveyQuestion diagnosisYear = new SurveyQuestion() {{
        setIdentifier("diagnosis-year");
        setPrompt("In what year were you diagnosed with Parkinson disease?");
        setUiHint(UiHint.NUMBERFIELD);
        setConstraints(new IntegerConstraints());
    }};
      
    SurveyQuestion medicationStartYear = new SurveyQuestion() {{
        setIdentifier("medication-start-year");
        setPrompt("In what year did you begin taking Parkinson disease medication? Leave blank if you have not started taking medication.");
        setUiHint(UiHint.NUMBERFIELD);
        setConstraints(new IntegerConstraints());
    }};
    
    SurveyQuestion healthCareProvider = new SurveyQuestion() {{
       setIdentifier("healthcare-provider");
       setPrompt("What kind of health care provider currently cares for your Parkinson disease?");
       setUiHint(UiHint.RADIOBUTTON);
       MultiValueConstraints c = new MultiValueConstraints(DataType.STRING);
       c.setAllowOther(true);
       c.setEnumeration(Lists.newArrayList(
           new SurveyQuestionOption("Parkinson Disease/Movement Disorder Specialist"),
           new SurveyQuestionOption("General Neurologist (non-Parkinson Disease specialist)"),
           new SurveyQuestionOption("Primary Care Doctor"),
           new SurveyQuestionOption("Nurse Practitioner or Physician's Assistant"),
           new SurveyQuestionOption("Don't know")
           // or other
       ));
       setConstraints(c);
    }};
    
    SurveyQuestion deepBrainStimulation = new SurveyQuestion() {{
        setIdentifier("deep-brain-stimulation");
        setPrompt("Have you ever had Deep Brain Stimulation?");
        setUiHint(UiHint.RADIOBUTTON);
        MultiValueConstraints c = ScriptUtils.booleanish();
        c.getRules().add(new SurveyRule(Operator.eq, "true", "when-dbs"));
        c.getRules().add(new SurveyRule(Operator.ne, "true", "surgery"));
        c.getRules().add(new SurveyRule(Operator.de, null, "surgery"));
        setConstraints(c);
    }};
    
    SurveyQuestion whenDBS = new SurveyQuestion() {{
        setIdentifier("when-dbs");
        setPrompt("When did you have DBS?");
        setUiHint(UiHint.DATEPICKER);
        setConstraints(new DateConstraints());
    }};
    
    SurveyQuestion surgery = new SurveyQuestion() {{
        setIdentifier("surgery");
        setPrompt("Have you ever had any surgery for Parkinson disease, other than DBS?");
        setUiHint(UiHint.RADIOBUTTON);
        setConstraints(ScriptUtils.booleanish());
    }};
    
    SurveyQuestion smoked = new SurveyQuestion() {{
        setIdentifier("smoked");
        setPrompt("Have you ever smoked?");
        setUiHint(UiHint.RADIOBUTTON);
        MultiValueConstraints c = ScriptUtils.booleanish();
        c.getRules().add(new SurveyRule(Operator.eq, "true", "when-smoked"));
        c.getRules().add(new SurveyRule(Operator.ne, "true", "health-history"));
        c.getRules().add(new SurveyRule(Operator.de, null, "health-history"));
        setConstraints(c);
    }};
    
    // There are several issues with this, I've filed a JIRA for improvements to the surveys
    // to handle this kind of a range
    SurveyQuestion whenSmoked = new SurveyQuestion() {{
        setIdentifier("when-smoked");
        setPrompt("What year did you start and end smoking (leave blank if you are still smoking)?");
        setUiHint(UiHint.DATEPICKER);
        setConstraints(new DurationConstraints());
    }};
    
    SurveyQuestion healthHistory = new SurveyQuestion() {{
        setIdentifier("health-history");
        setPrompt("Has a doctor ever told you that you have, or have you ever taken medication for any of the following conditions? Please check all that apply.");
        setUiHint(UiHint.LIST);
        MultiValueConstraints c = new MultiValueConstraints(DataType.STRING);
        c.setAllowMultiple(true);
        c.setEnumeration(Lists.newArrayList(
            new SurveyQuestionOption("Acute Myocardial Infarction/Heart Attack"),
            new SurveyQuestionOption("Alzheimer Disease or Alzheimer dementia"),
            new SurveyQuestionOption("Atrial Fibrillation"),
            new SurveyQuestionOption("Anxiety"),
            new SurveyQuestionOption("Cataract"),
            new SurveyQuestionOption("Kidney Disease"),
            new SurveyQuestionOption("Chronic Obstructive Pulumonary Disease (COPD) or Asthma"),
            new SurveyQuestionOption("Heart Failure/Congestive Heart Failure"),
            new SurveyQuestionOption("Diabetes or Prediabetes or High Blood Sugar"),
            new SurveyQuestionOption("Glaucoma"),
            new SurveyQuestionOption("Hip/Pelvic Fracture"),
            new SurveyQuestionOption("Ischemic Heart Disease"),
            new SurveyQuestionOption("Depression"),
            new SurveyQuestionOption("Anxiety"),
            new SurveyQuestionOption("Osteoporosis"),
            new SurveyQuestionOption("Rheumatoid Arthritis"),
            new SurveyQuestionOption("Dementia"),
            new SurveyQuestionOption("Stroke/Transient Ischemic Attack (TIA)"),
            new SurveyQuestionOption("Breast Cancer"),
            new SurveyQuestionOption("Colo-rectal Cancer"),
            new SurveyQuestionOption("Prostate Cancer"),
            new SurveyQuestionOption("Lung Cancer"),
            new SurveyQuestionOption("Endometrial/Uterine Cancer"),
            new SurveyQuestionOption("Head Injury with Loss of Consciousness/Concussion"),
            new SurveyQuestionOption("Any other kind of cancer OR tumor"),
            new SurveyQuestionOption("Urinay Tract infections"),
            new SurveyQuestionOption("Obstructive Sleeop Apnea"),
            new SurveyQuestionOption("Schizophrenia or Bipolar Disorder"),
            new SurveyQuestionOption("Peripheral Vascular Disease"),
            new SurveyQuestionOption("High Blood Pressure/Hypertension"),
            new SurveyQuestionOption("Fainting/Syncope"),
            new SurveyQuestionOption("Alcoholism"),
            new SurveyQuestionOption("Multiple Sclerosis"),
            new SurveyQuestionOption("Impulse control disorder"),
            new SurveyQuestionOption("AIDS or HIV"),
            new SurveyQuestionOption("Liver Disease"),
            new SurveyQuestionOption("Leukemia or Lymphoma"),
            new SurveyQuestionOption("Ulcer Disease"),
            new SurveyQuestionOption("Connective Tissue Disease"),
            new SurveyQuestionOption("Coronary Artery Disease"),
            new SurveyQuestionOption("Anemia"),
            new SurveyQuestionOption("Asthma")
        ));
        setConstraints(c);
    }};
    
    public ParkinsonDemographicSurvey() {
        setName("Parkinson Enrollment Survey");
        setIdentifier("parkinson-enrollment");
        getQuestions().add(age);
        getQuestions().add(gender);
        getQuestions().add(race);
        getQuestions().add(countryOfResidence);
        getQuestions().add(stateOfResidence);
        getQuestions().add(education);
        getQuestions().add(employment);
        getQuestions().add(maritalStatus);
        getQuestions().add(areCaretaker);
        getQuestions().add(pastParticipation);
        getQuestions().add(smartphone);
        getQuestions().add(phoneUsage);
        getQuestions().add(homeUsage);
        getQuestions().add(medicalUsage);
        getQuestions().add(medicalUsageYesterday);
        getQuestions().add(videoUsage);
        getQuestions().add(hasPD);
        getQuestions().add(professionalDiagnosis);
        getQuestions().add(onsetYear);
        getQuestions().add(diagnosisYear);
        getQuestions().add(medicationStartYear);
        getQuestions().add(healthCareProvider);
        getQuestions().add(deepBrainStimulation);
        getQuestions().add(whenDBS);
        getQuestions().add(surgery);
        getQuestions().add(smoked);
        getQuestions().add(whenSmoked);
        getQuestions().add(healthHistory);
    }

	@Override
	public Schedule getSchedule(GuidCreatedOnVersionHolder survey) {
        Schedule schedule = new Schedule();
        schedule.setLabel("Enrollment survey");
        ScriptUtils.setSurveyForSchedule(schedule, survey);
        schedule.setScheduleType(ScheduleType.once);
        return schedule;
	}
    
}
