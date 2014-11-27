package org.sagebionetworks.bridge.scripts;

import java.util.List;

import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.schedules.Schedule;
import org.sagebionetworks.bridge.sdk.models.schedules.ScheduleType;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyRule;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyRule.Operator;

public class BreastcancerEnrollmentSurvey extends BaseSurvey implements ScheduleHolder {
    
    public BreastcancerEnrollmentSurvey() {
        setName("BCS Enrollment Survey");
        setIdentifier("bcs-enrollment");
        
        addDate("q1", "What is your birth date?");
        addMulti("q2",
                "What category below best describes your racial/ethnic background? If you are of mixed racial/ethnic background, choose the category with which you most closely identify.",
                true,
                "White", "African American/Black", "Asian (Chinese, Japanese, Korean, South East Asian, Filipino)",
                "Native Hawaiian or Other Pacific Islander",
                "Native American/Alaska Native (American Indian, Aleutian, Eskimo)", "Indian Subcontinent",
                "Don’t know");
        addYesNo("q3", "Are you Hispanic or Latina?");
        addMulti("q4", "What is your current marital/relationship status?",
        		false,
                "Married",
                "Divorced",
                "Widowed", 
                "Legally separated",
                "Married but not living with spouse",
                "Committed relationship (partner opposite sex)",
                "Committed relationship (partner same sex)",
                "Single (never married)");
        addMulti("q5", "What category below best describes the highest level of formal education you have completed?",
        		false,
                "Grade school  (1-8 years)",
                "Some high school  (9-11 years)",
                "High school graduate or GED  (12 years)",
                "Vocational or training school after high school graduation",
                "Some college",
                "Associate Degree (A.D. or A.A.)",
                "College graduate (B.A. or B.S.)",
                "Some college or professional school after college graduation",
                "Completed Master’s Degree (M.A., M.S., M.P.H., M.S.W., M.Div., etc.)",
                "Completed Doctoral Degree (Ph.D., M.D., D.D.S., J.D., etc.)");
        addMulti("q6", "What is your CURRENT employment status?",
        		false,
                "Employed full-time (including self-employed)",
                "Employed part-time (including self-employed)",
                "Full-time homemaker",
                "Full-time or part-time volunteer",
                "Full-time or part-time student",
                "On temporary medical leave",
                "Retired",
                "Unemployed",
                "Permanently disabled");
        addMulti("q7", "Check one income range that best describes your family's total income before taxes for the previous year, including salaries, wages, tips, social security, welfare and any other income.",
        		false,
                "Under $15,000",
                "$15,001 - $30,000",
                "$30,001 - $60,000",
                "$60,001 - $100,000",
                "Over $100,000");
        List<SurveyRule> rules = addYesNo("q8", "Have you been treated for breast cancer?").getConstraints().getRules();
        rules.add(new SurveyRule(Operator.ne, "true", "q17"));
        rules.add(new SurveyRule(Operator.de, null, "q17"));

        addDate("q9", "What was the month and year of your initial breast cancer diagnosis (date of 1st cancer biopsy)?");
        
        addMulti("q10", "In which breast(s) was your cancer diagnosed?", false, "Right", "Left", "Both");

        addMulti("q11a", "Did you have a lupectomy?", false, "Left Breast", "Right Breast", "No");
        addMulti("q11b", "Did you have a sentinel node biopsy?", false, "Left Breast", "Right Breast", "No");
        addMulti("q11c", "Did you have an axillary node dissection?", false, "Left Breast", "Right Breast", "No");
        addMulti("q11d", "Did you have a mastectomy?", false, "Left Breast", "Right Breast", "No");
        addMulti("q11e", "Did you have a mastectomy with immediate reconstruction?", false, "Left Breast", "Right Breast", "No");
        addMulti("q11f", "Did you have an expander with later breast reconstruction planned?", false, "Left Breast", "Right Breast", "No");
        
        rules = addYesNo("q12", "Did you receive chemotherapy after your breast cancer diagnosis or surgery?").getConstraints().getRules();
        rules.add(new SurveyRule(Operator.ne, "true", "q13"));
        rules.add(new SurveyRule(Operator.de, null, "q13"));
        
        addDate("q12a", "When did you last have chemotherapy?");
        addMulti("q12b", "What kind of chemotherapy treatment did you have?",
        		true,
                "TC (Taxotere and Cytoxan)",
                "TAC (Taxotere, Adriamycin, and Cytoxan)",
                "CMF (Cytoxan, Methotrexate and 5FU)",
                "CMF+A (Cytoxan, Methotrexate, 5FU and Adriamycin)",
                "AC (Adriamycin and Cytoxan)",
                "FAC or CAF (Cytoxan, Adriamycin and 5FU)",
                "AC+Taxol (Adriamycin, Cytoxan and Taxol)",
                "CEF or FEC (5FU, Epirubicin and Cytoxan)",
                "TC (Taxotere and Carboplatin)");
        
        addMulti("q13", "Did you receive or are you currently receiving Herceptin (trastuzumab) or some other form of therapy that targets HER2 overexpression in your tumor?",
        		false,
                "Yes, Herceptin",
                "Yes, some other therapy",
                "No");

        rules = addYesNo("q14", "Did you receive radiation as part of your breast cancer treatment?").getConstraints().getRules();
        rules.add(new SurveyRule(Operator.ne, "true", "q15"));
        rules.add(new SurveyRule(Operator.de, null, "q15"));
        
        addDate("q14a", "When did you last have radiation?");
        
        addMulti("q15", "Are you receiving endocrine therapy for breast cancer (tamoxifen, ovarian suppression, Femara (letrozole), Anastrozole (arimidex), Aromasin (exemestane) now?",
                false, "Yes", "No", "Don't know");
        
        rules = addYesNo("q16", "Have you ever taken hormone replacement therapy (estrogen in any form, other than birth control pills)?").getConstraints().getRules();
        rules.add(new SurveyRule(Operator.ne, "true", "q17"));
        rules.add(new SurveyRule(Operator.de, null, "q17"));
        
        addDate("q16a", "When did you last have hormone replacement therapy?");
        
        addList("q17",
                "Do you currently have any of the following conditions?",
                "Allergies",
                "Arthritis",
                "Asthma",
                "Diabetes",
                "Glaucoma",
                "Frequent headaches/migraines",
                "Heart disease (heart attack, angina, heart failure)",
                "High blood pressure",
                "Osteopenia or osteoporosis requiring medication",
                "Thyroid problems",
                "Moderate to severe psychological difficulties (such as depression, anxiety, recent suicide attempts, or recent mental health hospitalization)",
                "Problems with alcohol", "Problems with drug use or dependence (either prescription or street drugs)",
                "I do not have any of these conditions");
        
        addYesNo("q18", "Do you feel pain in your chest at rest, during your daily activities of living, OR when you do physical activity?");
        addYesNo("q19", "Do you lose balance because of dizziness OR have you lost consciousness in the last 12 months? Please answer NO if your dizziness was associated with over-breathing (including during vigorous exercise).");
        addYesNo("q20", "Do you have a bone or joint problem that could be made worse by becoming more physically active? Please answer NO if you had a joint problem in the past, but it does not limit your current ability to be physically active");
        addYesNo("q21", "Has your doctor ever said that you should only do medically supervised physical activity?");
        addMulti("q22", "In general, would you say your health is: ", false, "Excellent", "Very Good",
                "Good", "Fair", "Poor");
        addMulti("q23", "Compared to one year ago, how would you rate your health in general now?", false, 
                "Much better now than one year ago", 
                "Somewhat better now than one year ago", 
                "About the same", 
                "Somewhat worse than one year ago", 
                "Much worse than one year ago");
        
        String[] options = new String[] {"Yes, limited a lot", "Yes, limited a little", "No, not limited at all"};
        
        addMulti("q24", "In a typical day, does your health now limit vigorous activities, such as running, lifting heavy objects, participating in strenuous sports?", false, options);
        addMulti("q25", "In a typical day, does your health now limit moderate activities, such as moving a table, pushing a vacuum cleaner, bowling, or playing golf?", false, options);
        addMulti("q26", "In a typical day, does your health now limit lifting or carrying groceries?", false, options);
        addMulti("q27", "In a typical day, does your health now limit climbing several flights of stairs?", false, options);
        addMulti("q28", "In a typical day, does your health now limit climbing one flight of stairs?", false, options);
        addMulti("q29", "In a typical day, does your health now limit bending, kneeling, or stooping?", false, options);
        addMulti("q30", "In a typical day, does your health now limit walking more than a mile?", false, options);
        addMulti("q31", "In a typical day, does your health now limit walking several blocks?", false, options);
        addMulti("q32", "In a typical day, does your health now limit walking one block?", false, options);
        addMulti("q33", "In a typical day, does your health now limit bathing or dressing yourself?", false, options);
        
        addYesNo("q34", "During the past 4 weeks, has your PHYSICAL health meant that you cut down the amount of time you spent on work or other activities?");
        addYesNo("q35", "During the past 4 weeks, has your PHYSICAL health meant that you accomplished less than you would like?");
        addYesNo("q36", "During the past 4 weeks, has your PHYSICAL health meant that you were limited in the kind of work or other activities?");
        addYesNo("q37", "During the past 4 weeks, has your PHYSICAL health meant that you had difficulty performing the work or other activities (for example, it took extra effort)?");
        
        addYesNo("q38", "During the past 4 weeks, have EMOTIONAL problems meant that you cut down the amount of time you spent on work or other activities?");
        addYesNo("q39", "During the past 4 weeks, have EMOTIONAL problems meant that you accomplished less than you would like?");
        addYesNo("q40", "During the past 4 weeks, have EMOTIONAL problems meant that you didn't do work or other activities as carefully as usual?");

        addMulti("q41", "During the past 4 weeks, to what extent has your physical health or emotional problems interfered with your normal social activities with family, friends, neighbors, or groups?",
        		false,
                "Not at all",
                "Slightly",
                "Moderately",
                "Quite a bit",
                "Extremely");
        
        addMulti("q42", "How much bodily pain have you had during the past 4 weeks?", false, "None",
                "Very mild", "Mild", "Moderate", "Severe", "Very severe");
        addMulti("q43",
                "During the past 4 weeks, how much did pain interfere with your normal work (including both work outside the home and housework)?",
                false, "Not at all", "A little bit", "Moderately", "Quite a bit", "Extremely");
        
        options = new String[] { "All of the time", "Most of the time", "A good bit of the time", "Some of the time",
                "A little of the time", "None of the time" };
        
        addMulti("q44", "In the past four weeks, how much of the time did you feel full of pep?", false, options);
        addMulti("q45", "In the past four weeks, how much of the time have you been a very nervous person?", false, options);
        addMulti("q46", "In the past four weeks, how much of the time have you felt so down in the dumps that nothing could cheer you up?", false, options);
        addMulti("q47", "In the past four weeks, how much of the time have you felt calm and peaceful?", false, options);
        addMulti("q48", "In the past four weeks, how much of the time did you have a lot of energy?", false, options);
        addMulti("q49", "In the past four weeks, how much of the time have you felt downhearted and blue?", false, options);
        addMulti("q50", "In the past four weeks, how much of the time did you feel worn out?", false, options);
        addMulti("q51", "In the past four weeks, how much of the time have you been a happy person?", false, options);
        addMulti("q52", "In the past four weeks, how much of the time did you feel tired?", false, options);

        addMulti("q53", "During the past 4 weeks, how much of the time have your physical health or emotional problems interfered with your social activities (like visiting with your friends, relatives, etc?)", false, options);
        
        options = new String[] { "Definitely True", "Mostly True", "Don’t Know", "Mostly False", "Definitely False" };
        addMulti("q54", "I seem to get sick a little easier than other people.", false, options);
        addMulti("q55", "I am as healthy as anybody I know.", false, options);
        addMulti("q56", "I expect my health to get worse.", false, options);
        addMulti("q57", "My health is excellent.", false, options);

        options = new String[] { "Not at all", "Slightly", "Moderately", "Quite a bit", "Extremely" };
        
        addMulti("q58a", "Have hot flashes bothered you in the last week?", false, options);
        addMulti("q58b", "Has nausea bothered you in the last week?", false, options);
        addMulti("q58c", "Has vomiting bothered you in the last week?", false, options);
        addMulti("q58d", "Have you had difficulty with bladder control when laughing or crying in the last week?", false, options);
        addMulti("q58e", "Have you had difficulty with bladder control at other times in the last week?", false, options);
        addMulti("q58f", "Have you had vaginal dryness in the last week?", false, options);
        addMulti("q58g", "Have you had pain with intercourse in the last week?", false, options);
        addMulti("q58h", "Have you had general aches and pains in the last week?", false, options);
        addMulti("q58i", "Have you had joint pains in the last week?", false, options);
        addMulti("q58j", "Have you had muscle stiffness in the last week?", false, options);
        addMulti("q58k", "Have you had weight gain in the last week?", false, options);
        addMulti("q58l", "Have you been unhappy with the appearance of your body in the last week?", false, options);
        addMulti("q58m", "Have you experienced forgetfullness in the last week?", false, options);
        addMulti("q58n", "Have you had night sweats in the last week?", false, options);
        addMulti("q58o", "Have you had difficulty concentrating in the last week?", false, options);
        addMulti("q58p", "Have you been easily distracted in the last week?", false, options);
        addMulti("q58q", "Have you had a lack of interest in sex in the last week?", false, options);
        addMulti("q58r", "Have you experienced low sexual enjoyment in the last week?", false, options);
        // We cannot branch here based on question #8
        addMulti("q58s", "Have you had arm swelling (lymphedema) in the last week?", false, options);
        addMulti("q58t", "Have you had decreased range of motion in arm on surgey side in the last week?", false, options);
    }

	@Override
	public Schedule getSchedule(GuidCreatedOnVersionHolder survey) {
        Schedule schedule = new Schedule();
        schedule.setLabel("Enrollment Survey");
        schedule.setScheduleType(ScheduleType.once);
        ScriptUtils.setSurveyForSchedule(schedule, survey);
        return schedule;
	}
}
