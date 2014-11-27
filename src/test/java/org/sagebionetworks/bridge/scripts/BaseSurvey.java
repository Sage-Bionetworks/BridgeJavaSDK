package org.sagebionetworks.bridge.scripts;

import java.util.Arrays;
import java.util.List;

import org.sagebionetworks.bridge.sdk.models.surveys.DataType;
import org.sagebionetworks.bridge.sdk.models.surveys.DateConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.DateTimeConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.IntegerConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.MultiValueConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestion;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestionOption;
import org.sagebionetworks.bridge.sdk.models.surveys.UiHint;

import com.google.common.collect.Lists;

public class BaseSurvey extends Survey {
    private SurveyQuestion add(String identifier, String prompt) {
        SurveyQuestion q = new SurveyQuestion();
        q.setIdentifier(identifier);
        q.setPrompt(prompt);
        getQuestions().add(q);
        return q;
    }
    protected SurveyQuestion addSlider(String identifier, String prompt) {
        return addSlider(identifier, prompt, 10L);
    }
    protected SurveyQuestion addSlider(String identifier, String prompt, long max) {
        SurveyQuestion q = add(identifier, prompt);
        q.setUiHint(UiHint.SLIDER);
        IntegerConstraints c = new IntegerConstraints();
        c.setMinValue(0L);
        c.setMaxValue(max);
        q.setConstraints(c);
        return q;
    }
    protected SurveyQuestion addDate(String identifier, String prompt) {
        SurveyQuestion q = add(identifier, prompt);
        q.setUiHint(UiHint.DATEPICKER);
        q.setConstraints(new DateConstraints());
        return q;
    }
    protected SurveyQuestion addDateTime(String identifier, String prompt) {
        SurveyQuestion q = add(identifier, prompt);
        q.setUiHint(UiHint.DATETIMEPICKER);
        q.setConstraints(new DateTimeConstraints());
        return q;
    }
    protected SurveyQuestion addMulti(String identifier, String prompt, boolean allowOther, SurveyQuestionOption... options) {
        SurveyQuestion q = add(identifier, prompt);
        q.setUiHint(UiHint.RADIOBUTTON);
        MultiValueConstraints c = new MultiValueConstraints(DataType.STRING);
        c.setAllowOther(allowOther);
        List<SurveyQuestionOption> list = Arrays.asList(options);
        c.setEnumeration(list);
        q.setConstraints(c);
        return q;
    }
    protected SurveyQuestion addMulti(String identifier, String prompt, boolean allowOther, String... options) {
        SurveyQuestionOption[] opts = new SurveyQuestionOption[options.length];
        for (int i=0; i < options.length; i++) {
            opts[i] = new SurveyQuestionOption(options[i]);
        }
        return addMulti(identifier, prompt, allowOther, opts);
    }
    protected SurveyQuestion addList(String identifier, String prompt, String... options) {
        SurveyQuestion q = add(identifier, prompt);
        q.setUiHint(UiHint.LIST);
        MultiValueConstraints c = new MultiValueConstraints(DataType.STRING);
        List<SurveyQuestionOption> list = Lists.newArrayList();
        for (int i=0; i < options.length; i++) {
            SurveyQuestionOption option = new SurveyQuestionOption(options[i]);
            list.add(option);
        }
        c.setAllowOther(false);
        c.setEnumeration(list);
        c.setAllowMultiple(true);
        q.setConstraints(c);
        return q;
    }
    protected SurveyQuestion addYesNo(String identifier, String prompt) {
        SurveyQuestion q = add(identifier, prompt);
        q.setUiHint(UiHint.RADIOBUTTON);
        q.setConstraints(ScriptUtils.booleanish());
        return q;
    }
}
