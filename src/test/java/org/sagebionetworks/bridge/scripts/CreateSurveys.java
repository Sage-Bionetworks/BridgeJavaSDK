package org.sagebionetworks.bridge.scripts;

import java.util.List;

import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.ResearcherClient;
import org.sagebionetworks.bridge.sdk.Session;
import org.sagebionetworks.bridge.sdk.models.GuidVersionedOnHolder;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

public class CreateSurveys {

    public static void main(String[] args) {
        Config config = ClientProvider.getConfig();
        Session session = ClientProvider.signIn(config.getAccountCredentials());

        ResearcherClient client = session.getResearcherClient();

        List<Survey> surveys = client.getAllVersionsOfAllSurveys();
        for (Survey survey : surveys) {
            survey = client.getSurvey(survey.getGuid(), survey.getVersionedOn());

            GuidVersionedOnHolder holder = client.versionSurvey(survey.getGuid(), survey.getVersionedOn());
            survey = client.getSurvey(holder.getGuid(), holder.getVersionedOn());
            client.updateSurvey(survey);
            client.publishSurvey(survey.getGuid(), survey.getVersionedOn());
        }
    }

}
