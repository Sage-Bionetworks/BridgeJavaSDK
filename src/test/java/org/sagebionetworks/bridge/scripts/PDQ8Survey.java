package org.sagebionetworks.bridge.scripts;

import java.util.List;

import org.sagebionetworks.bridge.sdk.models.surveys.DataType;
import org.sagebionetworks.bridge.sdk.models.surveys.MultiValueConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestion;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestionOption;
import org.sagebionetworks.bridge.sdk.models.surveys.UIHint;

import com.google.common.collect.Lists;

public class PDQ8Survey extends Survey {
    
    private List<SurveyQuestion> questions = Lists.newArrayList();
    
    public PDQ8Survey() {
        setName("Parkinson’s Disease Quality of Life Questionnaire");
        setIdentifier("PDQ8");
        setQuestions(questions);
        makeQuestion("had difficulty getting around in public?", "mobility");
        makeQuestion("had difficulty dressing yourself?","dressing");
        makeQuestion("felt depressed?", "depression");
        makeQuestion("had problems with your close personal relationships?", "relationships");
        makeQuestion("had problems with your concentration, e.g. when reading or watching TV?", "concentration");
        makeQuestion("felt unable to communicate with people properly?", "communication");
        makeQuestion("had painful muscle cramps or spasms?", "pain");
        makeQuestion("felt embarrassed in public due to having Parkinson’s disease?", "embarassment");
    }
    
    private static final String PREAMBLE = "Due to having Parkinson’s disease, how often during the last month have you ";
    private static final List<SurveyQuestionOption> options = Lists.newArrayList(
        new SurveyQuestionOption("Never", "0", null),
        new SurveyQuestionOption("Occassionally", "1", null),
        new SurveyQuestionOption("Sometimes", "2", null),
        new SurveyQuestionOption("Often", "3", null),
        new SurveyQuestionOption("Always (or cannot do at all)", "4", null)
    );
    private static final MultiValueConstraints constraints = new MultiValueConstraints() {
        {
            setDataType(DataType.INTEGER);
            setEnumeration(options);
        }
    };
    private void makeQuestion(final String prompt, final String id) {
        questions.add(new SurveyQuestion() {{
            setPrompt(PREAMBLE + prompt);
            setIdentifier(id);
            setConstraints(constraints);
            setUIHint(UIHint.SLIDER);
        }});
    }
}
