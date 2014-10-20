package org.sagebionetworks.bridge.scripts;

import java.util.List;

import org.sagebionetworks.bridge.sdk.BridgeResearcherClient;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.models.GuidVersionedOnHolder;
import org.sagebionetworks.bridge.sdk.models.surveys.MultiValueConstraints;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestion;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyQuestionOption;

public class CreateSurveys {

    public static void main(String[] args) {

        ClientProvider provider = ClientProvider.valueOf();
        provider.signIn();

        BridgeResearcherClient client = provider.getResearcherClient();
        
        List<Survey> surveys = client.getAllVersionsOfAllSurveys();
        for (Survey survey : surveys) {
            survey = client.getSurvey(survey.getGuid(), survey.getVersionedOn());
            
            GuidVersionedOnHolder holder = client.versionSurvey(survey.getGuid(), survey.getVersionedOn());
            survey = client.getSurvey(holder.getGuid(), holder.getVersionedOn());

            for (SurveyQuestion question : survey.getQuestions()) {
                if (question.getConstraints() instanceof MultiValueConstraints) {
                    MultiValueConstraints con = (MultiValueConstraints)question.getConstraints();
                    
                    for (int i=0; i < con.getEnumeration().size(); i++) {
                        SurveyQuestionOption option = con.getEnumeration().get(i);
                        
                        con.getEnumeration().set(i, new SurveyQuestionOption(option.getLabel(), (String)option.getValue(), option.getImage()));
                    }
                }
            }
            client.updateSurvey(survey);
            client.publishSurvey(survey.getGuid(), survey.getVersionedOn());
        }
    }

}
