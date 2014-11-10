package org.sagebionetworks.bridge.scripts;

import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.ResearcherClient;
import org.sagebionetworks.bridge.sdk.Session;
import org.sagebionetworks.bridge.sdk.TestSurvey;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

public class CreateSurveys {

    public static void main(String[] args) {
        Config config = ClientProvider.getConfig();
        config.set(Config.Props.HOST, "https://pd-staging.sagebridge.org");
        Session session = ClientProvider.signIn(config.getAdminCredentials());

        ResearcherClient client = session.getResearcherClient();

        Survey survey = new TestSurvey();
        
        GuidCreatedOnVersionHolder keys = client.createSurvey(survey);
        client.publishSurvey(keys);
    }

}
