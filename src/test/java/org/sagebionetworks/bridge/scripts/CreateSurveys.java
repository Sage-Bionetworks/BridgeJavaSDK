package org.sagebionetworks.bridge.scripts;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.BridgeResearcherClient;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.models.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

public class CreateSurveys {

    public static void main(String[] args) {

        SignInCredentials signIn = SignInCredentials.valueOf()
                .setUsername("")
                .setPassword("");
        ClientProvider provider = ClientProvider.valueOf();
        provider.signIn(signIn);

        BridgeResearcherClient client = provider.getResearcherClient();

        //GuidVersionedOnHolder [guid=ecf7e761-c7e9-4bb6-b6e7-d6d15c53b209, version=2014-09-25T20:07:49.186Z]
        //GuidVersionedOnHolder [guid=e7e8b5c7-16b6-412d-bcf9-f67291781972, version=2014-09-25T20:07:50.794Z]

        //GuidVersionedOnHolder one = client.createSurvey(new PAOCICSurvey());
        Survey survey = client.getSurvey("ecf7e761-c7e9-4bb6-b6e7-d6d15c53b209", DateTime.parse("2014-09-25T20:07:49.186Z"));
        System.out.println(survey);
    }

}
