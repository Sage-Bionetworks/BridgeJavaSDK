package org.sagebionetworks.bridge.scripts;

import org.sagebionetworks.bridge.sdk.BridgeResearcherClient;
import org.sagebionetworks.bridge.sdk.ClientProvider;

public class CreateSurveys {

    public static void main(String[] args) {

        ClientProvider provider = ClientProvider.valueOf();
        provider.signIn();

        BridgeResearcherClient client = provider.getResearcherClient();
        
        //client.createSurvey(new PAOCICSurvey());
        //client.createSurvey(new PDQ8Survey());
        //client.publishSurvey("4aad1810-cef9-41bc-b0d9-73bcdf32df07", DateTime.parse("2014-10-16T21:36:44.386Z"));
        //client.publishSurvey("708e249a-986c-4ce6-959e-337d5e787f02", DateTime.parse("2014-10-16T21:34:39.653Z"));
    }

}
